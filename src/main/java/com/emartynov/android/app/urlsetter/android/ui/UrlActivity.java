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

package com.emartynov.android.app.urlsetter.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.UrlApplication;
import com.emartynov.android.app.urlsetter.android.UrlService;
import com.emartynov.android.app.urlsetter.service.Crashlytics;
import com.emartynov.android.app.urlsetter.service.Mixpanel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class UrlActivity extends Activity
{
    @Inject
    Crashlytics crashlytics;
    @Inject
    Mixpanel mixpanel;

    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        ( (UrlApplication) getApplication() ).inject( this );

        crashlytics.start( this );
        mixpanel.init( this );

        Intent serviceIntent;
        if ( Intent.ACTION_SEND.equals( getIntent().getAction() ) )
        {
            serviceIntent = getServiceIntentForSharedUrl();
        }
        else
        {
            serviceIntent = getIntent();
        }

        if ( serviceIntent != null )
        {
            Intent service = new Intent( serviceIntent );
            service.setClass( this, UrlService.class );
            startService( service );
        }

        finish();
    }

    private Intent getServiceIntentForSharedUrl ()
    {
        Intent serviceIntent = null;

        String sharedText = getLinkFromSharedIntent();

        try
        {
            URL url = new URL( sharedText );

            serviceIntent = new Intent();
            serviceIntent.setData( Uri.parse( sharedText ) );

            logPassedUrl( url );
        }
        catch ( MalformedURLException e )
        {
            mixpanel.trackEvent( Mixpanel.PASSED_BAD_URL_EVENT );

            showPassedBadUrlError( sharedText );
        }

        return serviceIntent;
    }

    private void showPassedBadUrlError ( String sharedText )
    {
        Toast toast = Toast.makeText( this, getString( R.string.not_a_link_error_text, sharedText ), Toast.LENGTH_LONG );
        toast.show();
    }

    private String getLinkFromSharedIntent ()
    {
        String sharedText = getIntent().getStringExtra( Intent.EXTRA_TEXT );

        return sharedText != null && sharedText.startsWith( "http" ) ? sharedText : "http://" + sharedText;
    }

    private void logPassedUrl ( URL url )
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "host", url.getHost() );

        mixpanel.trackEvent( Mixpanel.PASSED_URL_EVENT, params );
    }

    @Override
    protected void onDestroy ()
    {
        super.onDestroy();

        mixpanel.flush();
    }
}