package com.emartynov.android.app.urlsetter.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
    @Inject
    Bus bus;

    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        bus = ( (UrlApplication) getApplication() ).getBus();

        bus.register( this );

        Uri uri = getIntent().getData();

        Toast toast = Toast.makeText( this, getString( R.string.resolving_url, uri ), Toast.LENGTH_LONG );
        toast.show();

        bus.post( new ResolveURL( uri ) );
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

    private void launchChooser ( final ArrayList<Intent> intents )
    {
        runOnUiThread( new Runnable()
        {
            @Override
            public void run ()
            {
                Intent firstIntent = intents.remove( 0 );
                Intent chooserIntent = Intent.createChooser( firstIntent, getString( R.string.select_application_for, firstIntent.getData() ) );
                chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, intents.toArray( new Parcelable[ intents.size() ] ) );
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
                 info.activityInfo.name) );
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