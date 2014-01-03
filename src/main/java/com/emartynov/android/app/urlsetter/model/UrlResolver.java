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

package com.emartynov.android.app.urlsetter.model;

import android.net.Uri;

import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundUrl;
import com.emartynov.android.app.urlsetter.model.event.ResolveUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class UrlResolver
{
    public static final String HEAD_METHOD = "HEAD";
    public static final String GET_METHOD = "GET";

    private final Bus bus;
    private final OkHttpClient httpClient = new OkHttpClient();
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool( 2 );

    public UrlResolver ( Bus bus )
    {
        this.bus = bus;

        bus.register( this );
    }

    @Subscribe
    public void resolveURL ( ResolveUrl event )
    {
        executor.execute( new ResolveUrlRunnable( event.getUri() ) );
    }

    public boolean isIdle ()
    {
        return executor.getTaskCount() - executor.getCompletedTaskCount() == 0;
    }

    private class ResolveUrlRunnable implements Runnable
    {
        private final Uri uri;

        public ResolveUrlRunnable ( Uri uri )
        {
            this.uri = uri;
        }

        @Override
        public void run ()
        {
            try
            {
                Uri resolvedUri = findRedirect( uri );

                bus.post( new FoundUrl( uri, resolvedUri ) );
            }
            catch ( Exception e )
            {
                bus.post( new DownloadingError( uri, e ) );
            }
        }
    }

    private Uri findRedirect ( Uri sourceUri ) throws IOException
    {
        String currentUrl;
        String nextUrl = sourceUri.toString();

        do
        {
            currentUrl = nextUrl;
            nextUrl = processHeadUrl( currentUrl );
        }
        while ( nextUrl != null );

        return Uri.parse( currentUrl );
    }

    private String processHeadUrl ( String url ) throws IOException
    {
        return processUrl( url, HEAD_METHOD );
    }

    private String processGetUrl ( String url ) throws IOException
    {
        return processUrl( url, GET_METHOD );
    }

    private String processUrl ( String url, String method ) throws IOException
    {
        HttpURLConnection connection = null;

        try
        {
            connection = httpClient.open( new URL( url ) );
            connection.setRequestMethod( method );
            connection.setRequestProperty( "Accept-Encoding", "" );
            connection.setInstanceFollowRedirects( false );
            connection.setConnectTimeout( 30000 );

            int responseCode = connection.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK )
            {
                return null;
            }
            else if ( isRedirection( responseCode ) )
            {
                return connection.getHeaderField( "Location" ).replace( " ", "%20" );
            }
            else if ( HEAD_METHOD.equals( method ) && responseCode == HttpURLConnection.HTTP_BAD_METHOD ) //method is not supported
            {
                return processGetUrl( url );
            }
            else
            {
                throw new ProtocolException( "Bad response: " + responseCode + "; with url: " + url );
            }
        }
        finally
        {
            if ( connection != null )
            {
                connection.disconnect();
            }
        }
    }

    private boolean isRedirection ( int responseCode )
    {
        return responseCode == HttpURLConnection.HTTP_MOVED_PERM
                || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                || responseCode == HttpURLConnection.HTTP_MULT_CHOICE
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER
                || responseCode == 307;
    }
}
