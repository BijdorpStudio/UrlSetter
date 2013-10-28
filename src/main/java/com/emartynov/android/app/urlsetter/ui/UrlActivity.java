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

package com.emartynov.android.app.urlsetter.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.Toast;
import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundURL;
import com.emartynov.android.app.urlsetter.model.event.ResolveURL;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

public class UrlActivity extends InjectedActivity
{
    public static final long TIMEOUT_IN_SECONDS = 5 * DateUtils.SECOND_IN_MILLIS;

    @Inject
    Bus bus;
    private Timer timer;

    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        bus.register( this );

        Uri uri = getIntent().getData();

        showLongToast( getString( R.string.resolving_url, uri ) );

        createLongOperationTimer();

        bus.post( new ResolveURL( uri ) );
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
                showLongToast( getString( R.string.operation_takes_longer ) );
            }
        }, TIMEOUT_IN_SECONDS );
    }

    private void showLongToast ( final String toastText )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                Toast toast = Toast.makeText( UrlActivity.this, toastText, Toast.LENGTH_SHORT );
                toast.show();
            }
        } );
    }

    private void showLongToastAndFinish ( final String toastText )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                Toast toast = Toast.makeText( UrlActivity.this, toastText, Toast.LENGTH_SHORT );
                toast.show();

                finish();
            }
        } );
    }

    @Override
    protected void onDestroy ()
    {
        super.onDestroy();

        bus.unregister( this );
    }

    @Subscribe
    public void launchURL ( FoundURL event )
    {
        cancelTimer();

        setupAliasActivityIntentFilter( true );

        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( event.getUri() );

        startActivity( intent );

        showLongToast( getString( R.string.resolved_url, event.getUri() ) );

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

                setupAliasActivityIntentFilter( false );

                finish();
            }
        } ).start();
    }

    private void setupAliasActivityIntentFilter ( boolean disabled )
    {
        int state = disabled ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED : PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        ComponentName componentName = new ComponentName( getPackageName(), getPackageName() + ".ProcessActivity" );
        getPackageManager().setComponentEnabledSetting( componentName, state, PackageManager.DONT_KILL_APP );
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

        showLongToastAndFinish( errorString );
    }
}