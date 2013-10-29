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

package com.emartynov.android.app.urlsetter.android.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.Toast;
import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.packagemanager.ComponentSwitcher;
import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundURL;
import com.emartynov.android.app.urlsetter.model.event.ResolveURL;
import com.emartynov.android.app.urlsetter.service.mixpanel.MixLogger;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UrlActivity extends InjectedActivity
{
    public static final long TIMEOUT_IN_SECONDS = 5 * DateUtils.SECOND_IN_MILLIS;

    @Inject
    Bus bus;
    @Inject
    MixLogger logger;

    private Timer timer;

    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        createLongOperationTimer();

        bus.register( this );

        Uri uri = getIntent().getData();

        showShortToast( getString( R.string.resolving_url, uri ) );

        bus.post( new ResolveURL( uri ) );

        logger.init( this );
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
                showShortToast( getString( R.string.operation_takes_longer ) );
            }
        }, TIMEOUT_IN_SECONDS );
    }

    private void showShortToast ( final String toastText )
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

    private void showToast ( String toastText )
    {
        Toast toast = Toast.makeText( this, toastText, Toast.LENGTH_LONG );
        toast.show();
    }

    private void showShortToastAndFinish ( final String toastText )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                showToast( toastText );

                finish();
            }
        } );
    }

    @Override
    protected void onDestroy ()
    {
        super.onDestroy();

        bus.unregister( this );

        logger.flush();
    }

    @Subscribe
    public void launchURL ( FoundURL event )
    {
        cancelTimer();

        showUri( event.getResolvedUri() );

        logger.trackEvent( "Resolved", event.getLoggingParams() );
    }

    private void showUri ( Uri uri )
    {
        ComponentSwitcher.disableComponent( this, getPackageName(), getPackageName() + ".ProcessActivity" );

        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );

        startActivity( intent );

        showShortToast( getString( R.string.resolved_url, uri ) );

        new Thread( new Runnable()
        {
            @Override
            public void run ()
            {
                try
                {
                    Thread.sleep( 1000 );
                }
                catch ( InterruptedException ignored )
                {
                }

                ComponentSwitcher.enableComponent( UrlActivity.this, getPackageName(), getPackageName() + ".ProcessActivity" );

                finish();
            }
        } ).start();
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

        String errorString = getString( R.string.error_while_resolving_url, event.getException() );

        showShortToastAndFinish( errorString );

        showUri( event.getUri() );

        logger.trackEvent( "Error", event.getLoggingParams() );
    }
}