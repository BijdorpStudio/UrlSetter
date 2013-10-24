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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import android.text.format.DateUtils;
import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.UrlApplication;
import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundURL;
import com.emartynov.android.app.urlsetter.model.event.ResolveURL;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

public class UrlActivity extends InjectedActivity
{
    public static final long TIMEOUT_IN_SECONDS = 3 * DateUtils.SECOND_IN_MILLIS;
    @Inject
    Bus bus;
    private Timer timer;

    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        bus = ((UrlApplication) getApplication()).getBus();

        bus.register( this );

        Uri uri = getIntent().getData();

        Toast toast = Toast.makeText( this, getString( R.string.resolving_url, uri ), Toast.LENGTH_LONG );
        toast.show();

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
                Toast toast = Toast.makeText( UrlActivity.this, getString( R.string.operation_takes_longer ), Toast.LENGTH_SHORT );
                toast.show();
            }
        }, TIMEOUT_IN_SECONDS );
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

        PackageManager packageManager = getPackageManager();

        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( event.getUri() );

        ResolveInfo defaultInfo = packageManager.resolveActivity( intent, PackageManager.MATCH_DEFAULT_ONLY );

        if ( !"com.android.internal.app.ResolverActivity".equals( defaultInfo.activityInfo.name ) )
        {
            specifyApp( defaultInfo, intent );
            launchApp( intent );

            return;
        }

        List<ResolveInfo> possibleIntents = packageManager.queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY );
        ArrayList<Intent> intents = new ArrayList<Intent>( possibleIntents.size() );

        if ( possibleIntents.size() > 0 )
        {
            for ( ResolveInfo resolveInfo : possibleIntents )
            {
                if ( !resolveInfo.activityInfo.packageName.startsWith( getBaseContext().getPackageName() ) )
                {
                    Intent target = new Intent( intent );
                    target.setData( event.getUri() );
                    specifyApp( resolveInfo, target );

                    intents.add( target );
                }

            }
            if ( intents.size() == 0 )
            {
                informNoAppToShow();
            }
            else if ( intents.size() == 1 )
            {
                launchApp( intents.get( 0 ) );
            }
            else
            {
                launchChooser( intents );
            }
        }
        else
        {
            informNoAppToShow();
        }
    }

    private void cancelTimer ()
    {
        if ( timer != null )
        {
            timer.cancel();
        }
    }

    private void launchChooser ( final ArrayList<Intent> intents )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                Intent firstIntent = intents.remove( 0 );
                Intent chooserIntent = Intent.createChooser( firstIntent, getString( R.string.select_application_for, firstIntent.getData() ) );
                chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, intents.toArray( new Parcelable[intents.size()] ) );
                startActivity( chooserIntent );

                finish();
            }
        } );
    }

    private void informNoAppToShow ()
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                String errorString = getString( R.string.could_not_launch_corresponded_app );
                Toast.makeText( UrlActivity.this, errorString, Toast.LENGTH_LONG ).show();

                finish();
            }
        } );
    }

    private void launchApp ( final Intent intent )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                startActivity( intent );

                finish();
            }
        } );
    }

    private void specifyApp ( ResolveInfo info, Intent intent )
    {
        intent.setPackage( info.activityInfo.applicationInfo.packageName );
        intent.setComponent( ComponentName.unflattenFromString( info.activityInfo.applicationInfo.packageName + "/" +
                info.activityInfo.name ) );
    }

    @Subscribe
    public void downloadError ( final DownloadingError event )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                String errorString = getString( R.string.error_while_resolving_url, event.getException() );
                Toast.makeText( UrlActivity.this, errorString, Toast.LENGTH_LONG ).show();

                finish();
            }
        } );
    }
}