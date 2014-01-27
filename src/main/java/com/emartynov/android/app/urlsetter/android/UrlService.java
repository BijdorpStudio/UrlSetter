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
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.packagemanager.IntentHelper;
import com.emartynov.android.app.urlsetter.model.UrlDiskLruCache;
import com.emartynov.android.app.urlsetter.model.UrlResolver;
import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundUrl;
import com.emartynov.android.app.urlsetter.model.event.ResolveUrl;
import com.emartynov.android.app.urlsetter.model.event.UrlEvent;
import com.emartynov.android.app.urlsetter.service.Crashlytics;
import com.emartynov.android.app.urlsetter.service.Mixpanel;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

public class UrlService extends Service
{
    public static final long APOLOGIZE_TIMEOUT = 10 * DateUtils.SECOND_IN_MILLIS;
    public static final long STOP_TIMEOUT = 5 * DateUtils.MINUTE_IN_MILLIS;
    private static final String FACEBOOK_HOST = "m.facebook.com";

    @Inject
    UrlResolver urlResolver;
    @Inject
    Mixpanel logger;
    @Inject
    UrlDiskLruCache cache;
    @Inject
    Crashlytics crashlytics;
    @Inject
    ThreadPoolExecutor executor;
    @Inject
    IntentHelper intentHelper;

    private Timer timer;
    private Handler handler;

    @Override
    public void onCreate ()
    {
        handler = new Handler();

        ( (UrlApplication) getApplication() ).inject( this );

        crashlytics.start( this );

        logger.init( this );
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
        boolean showMessageBefore = !cache.isLoaded();
        if ( showMessageBefore )
        {
            showToastOnUI( getString( R.string.resolving_url, uri ) );
        }

        Uri targetUri = uri;

        if ( isFacebook( uri ) )
        {
            String hiddenUrl = uri.getQueryParameter( "u" );
            if ( hiddenUrl != null )
            {
                targetUri = Uri.parse( hiddenUrl );
            }
        }

        getFromCacheOrResolve( new ResolveUrl( targetUri ), showMessageBefore );
    }

    private boolean isFacebook ( Uri uri )
    {
        return FACEBOOK_HOST.equals( uri.getHost() );
    }

    private void getFromCacheOrResolve ( ResolveUrl event, boolean messageShown )
    {
        Uri resolvedUri = cache.get( event.getUri() );
        if ( resolvedUri == null )
        {
            if ( !messageShown )
            {
                showToastOnUI( getString( R.string.resolving_url, event.getUri() ) );
            }

            resolveUrl( event );
        }
        else
        {
            launchURL( new FoundUrl( event.getUri(), resolvedUri ) );
        }

        trackStart( isFacebook( event.getUri() ) );
    }

    public void resolveUrl ( ResolveUrl event )
    {
        createLongOperationTimer();

        UrlEvent result = urlResolver.resolveURL( event );

        if ( result instanceof DownloadingError )
        {
            downloadError( (DownloadingError) result );
        }
        else if ( result instanceof FoundUrl )
        {
            launchURL( (FoundUrl) result );
        }
    }

    private void trackStart ( boolean fromFacebook )
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "facebook", String.valueOf( fromFacebook ) );

        logger.trackEvent( Mixpanel.RESOLVING_STARTED_EVENT, params );
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
        }, APOLOGIZE_TIMEOUT );
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

        logger.flush();
    }

    public void launchURL ( FoundUrl event )
    {
        cancelTimer();

        if ( checkInfiniteLoop( event ) )
        {
            reportUnresolvedUrlError( event );
        }
        else
        {
            launchAndCacheResolvedUri( event );
        }
    }

    private boolean checkInfiniteLoop ( FoundUrl event )
    {
        return event.getResolvedUri().equals( event.getUri() ) && intentHelper.isFilterUri( this, event.getUri() );
    }

    private void reportUnresolvedUrlError ( FoundUrl event )
    {
        RuntimeException dummyException = new RuntimeException( "Resolver returned same URL" );
        downloadError( new DownloadingError( event.getUri(), event.getUri(), dummyException ) );
    }

    private void launchAndCacheResolvedUri ( FoundUrl event )
    {
        launchResolvedUri( event.getResolvedUri() );

        logger.trackEvent( Mixpanel.RESOLVED_URL_EVENT, event.getLoggingParams() );

        cache.save( event.getUri(), event.getResolvedUri() );
    }

    private void launchResolvedUri ( Uri uri )
    {
        intentHelper.launchUri( this, uri );

        checkToStop();
    }

    private void checkToStop ()
    {
        if ( isOnlyOne() )
        {
            cancelTimer();
            timer = new Timer();
            timer.schedule( new TimerTask()
            {
                @Override
                public void run ()
                {
                    if ( isIdle() )
                    {
                        stopSelf();
                    }
                }
            }, STOP_TIMEOUT );
        }
    }

    private boolean isOnlyOne ()
    {
        return checkNumberOfTasks( 1 );
    }

    private boolean isIdle ()
    {
        return checkNumberOfTasks( 0 );
    }

    private boolean checkNumberOfTasks ( int numberOfTasks )
    {
        return ( executor.getTaskCount() - executor.getCompletedTaskCount() ) == numberOfTasks;
    }

    private synchronized void cancelTimer ()
    {
        if ( timer != null )
        {
            timer.cancel();
        }
    }

    public void downloadError ( final DownloadingError event )
    {
        cancelTimer();

        crashlytics.logException( event.getException() );

        intentHelper.launchUriWithoutUs( this, event.getLastResolvedUri() );

        logger.trackEvent( Mixpanel.RESOLVING_ERROR_EVENT, event.getLoggingParams() );

        checkToStop();
    }
}
