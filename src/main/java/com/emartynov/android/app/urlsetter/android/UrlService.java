/*
 * Copyright 2013-2014 @BijdorpStudio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emartynov.android.app.urlsetter.android;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.model.UrlDiskLruCache;
import com.emartynov.android.app.urlsetter.model.UrlResolver;
import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundUrl;
import com.emartynov.android.app.urlsetter.model.event.ResolveUrl;
import com.emartynov.android.app.urlsetter.model.event.UrlEvent;
import com.emartynov.android.app.urlsetter.service.Crashlytics;
import com.emartynov.android.app.urlsetter.service.Mixpanel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

public class UrlService extends Service
{
    public static final long TIMEOUT_IN_SECONDS = 10 * DateUtils.SECOND_IN_MILLIS;
    private static final String FACEBOOK_HOST = "m.facebook.com";

    @Inject
    UrlResolver urlResolver;
    @Inject
    Bus bus;
    @Inject
    Mixpanel logger;
    @Inject
    UrlDiskLruCache cache;
    @Inject
    Crashlytics crashlytics;
    @Inject
    ThreadPoolExecutor executor;


    private Timer timer;
    private Handler handler;

    @Override
    public void onCreate ()
    {
        ( (UrlApplication) getApplication() ).inject( this );

        crashlytics.start( this );

        bus.register( this );
        logger.init( this );

        handler = new Handler();
    }

    @Override
    public IBinder onBind ( Intent intent )
    {
        return null;
    }

    @Override
    public int onStartCommand ( final Intent intent, int flags, int startId )
    {
        if ( intent != null )
        {
            executor.execute( new Runnable()
            {
                @Override
                public void run ()
                {
                    resolveUrl( intent.getData() );
                }
            } );
        }

        return START_NOT_STICKY;
    }

    private void resolveUrl ( Uri uri )
    {
        Uri targetUri = uri;

        if ( isFacebook( uri ) )
        {
            String hiddenUrl = uri.getQueryParameter( "u" );
            if ( hiddenUrl != null )
            {
                targetUri = Uri.parse( hiddenUrl );
            }
        }

        getFromCacheOrResolve( new ResolveUrl( targetUri ) );
    }

    private boolean isFacebook ( Uri uri )
    {
        return FACEBOOK_HOST.equals( uri.getHost() );
    }

    private void getFromCacheOrResolve ( UrlEvent event )
    {
        Uri resolvedUri = cache.get( event.getUri() );
        if ( resolvedUri == null )
        {
            bus.post( event );
        }
        else
        {
            bus.post( new FoundUrl( event.getUri(), resolvedUri ) );
        }
    }

    @Subscribe
    public void resolveUrl ( ResolveUrl event )
    {
        createLongOperationTimer();

        showToastOnUI( getString( R.string.resolving_url, event.getUri() ) );

        trackStart( isFacebook( event.getUri() ) );
    }

    private void trackStart ( boolean fromFacebook )
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "facebook", String.valueOf( fromFacebook ) );
        logger.trackEvent( "Started", params );
    }

    private synchronized void createLongOperationTimer ()
    {
        cancelTimer();

        timer = new Timer();
        timer.schedule( new TimerTask()
        {
            @Override
            public void run ()
            {
                showToastOnUI( getString( R.string.operation_takes_longer ) );
            }
        }, TIMEOUT_IN_SECONDS );
    }

    private void showToastOnUI ( final String toastText )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                showToast( toastText );
            }
        } );
    }

    private void runOnUiThread ( Runnable runnable )
    {
        handler.post( runnable );
    }

    private void showToast ( String toastText )
    {
        Toast toast = Toast.makeText( this, toastText, Toast.LENGTH_SHORT );
        toast.show();
    }

    @Override
    public void onDestroy ()
    {
        super.onDestroy();

        bus.unregister( this );

        logger.flush();
    }

    @Subscribe
    public void launchURL ( FoundUrl event )
    {
        cancelTimer();

        launchResolvedUri( event.getResolvedUri() );

        logger.trackEvent( "Resolved", event.getLoggingParams() );

        cache.save( event.getUri(), event.getResolvedUri() );
    }

    private void launchResolvedUri ( Uri uri )
    {
        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

        startActivity( intent );

        checkToStop();
    }

    private void checkToStop ()
    {
        if ( isIdle() )
        {
            stopSelf();
        }
    }

    private boolean isIdle ()
    {
        return ( executor.getTaskCount() - executor.getCompletedTaskCount() ) == 0;
    }

    private synchronized void cancelTimer ()
    {
        if ( timer != null )
        {
            timer.cancel();
        }
    }

    @Subscribe
    public void downloadError ( final DownloadingError event )
    {
        cancelTimer();

        crashlytics.logException( event.getException() );

        launchUriWithoutUs( event.getLastResolvedUri() );

        logger.trackEvent( "Error", event.getLoggingParams() );

        checkToStop();
    }

    private void launchUriWithoutUs ( Uri uri )
    {
        PackageManager packageManager = getPackageManager();

        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );

        List<ResolveInfo> possibleIntents = packageManager.queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY );
        ArrayList<Intent> intents = new ArrayList<Intent>( possibleIntents.size() );

        if ( possibleIntents.size() > 0 )
        {
            for ( ResolveInfo resolveInfo : possibleIntents )
            {
                if ( !resolveInfo.activityInfo.packageName.startsWith( getBaseContext().getPackageName() ) )
                {
                    Intent target = new Intent( intent );
                    target.setData( uri );
                    target.setPackage( resolveInfo.activityInfo.packageName );

                    intents.add( target );
                }
            }
            launchChooser( intents );
        }
    }

    private void launchChooser ( final ArrayList<Intent> intents )
    {
        Intent firstIntent = intents.remove( 0 );
        Parcelable[] parcelableIntents = intents.toArray( new Parcelable[ intents.size() ] );
        String dialogCaption = getString( R.string.select_application_for, firstIntent.getData() );

        Intent chooserIntent = Intent.createChooser( firstIntent, dialogCaption );
        chooserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, parcelableIntents );
        startActivity( chooserIntent );
    }

}
