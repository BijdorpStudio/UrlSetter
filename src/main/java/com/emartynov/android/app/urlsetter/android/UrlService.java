/*
 * Copyright 2013 Eugen Martynov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
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
import com.crashlytics.android.Crashlytics;
import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.model.URLResolver;
import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundURL;
import com.emartynov.android.app.urlsetter.model.event.ResolveFacebookURL;
import com.emartynov.android.app.urlsetter.model.event.ResolveURL;
import com.emartynov.android.app.urlsetter.service.Mixpanel;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

public class UrlService extends Service
{
    public static final long TIMEOUT_IN_SECONDS = 5 * DateUtils.SECOND_IN_MILLIS;
    private static final String FACEBOOK_HOST = "m.facebook.com";

    private URLResolver urlResolver;
    @Inject
    Bus bus;
    @Inject
    Mixpanel logger;

    private Timer timer;
    private Handler handler;

    @Override
    public void onCreate ()
    {
        Crashlytics.start( this );

        ((UrlApplication) getApplication()).inject( this );

        urlResolver = new URLResolver( bus );

        bus.register( this );
        logger.init( this );
    }

    @Subscribe
    public void resolveUrl ( ResolveURL event )
    {
        createLongOperationTimer();

        showToastOnUI( getString( R.string.resolving_url, event.getUri() ) );
    }

    @Override
    public IBinder onBind ( Intent intent )
    {
        return null;
    }

    @Override
    public int onStartCommand ( Intent intent, int flags, int startId )
    {
        handler = new Handler();

        if ( intent != null )
        {
            resolveUrl( intent.getData() );
        }

        return START_STICKY;
    }

    private void resolveUrl ( Uri uri )
    {
        if ( FACEBOOK_HOST.equals( uri.getHost() ) )
        {
            bus.post( new ResolveFacebookURL( uri ) );
        }
        else
        {
            bus.post( new ResolveURL( uri ) );
        }
    }

    private void createLongOperationTimer ()
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
    public void launchURL ( FoundURL event )
    {
        cancelTimer();

//        showToastOnUI( getString( R.string.resolved_url, event.getResolvedUri() ) );

        showUri( event.getResolvedUri() );

        logger.trackEvent( "Resolved", event.getLoggingParams() );
    }

    private void showUri ( Uri uri )
    {
        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

        startActivity( intent );

        checkToStop();
    }

    private void checkToStop ()
    {
        if ( urlResolver.isIdle() )
        {
            stopSelf();
        }
    }

    private void cancelTimer ()
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

        Crashlytics.logException( event.getException() );

        String errorString = getString( R.string.error_while_resolving_url, event.getException() );

        showToastOnUI( errorString );

        showUri( event.getUri() );

        logger.trackEvent( "Error", event.getLoggingParams() );

        checkToStop();
    }
}
