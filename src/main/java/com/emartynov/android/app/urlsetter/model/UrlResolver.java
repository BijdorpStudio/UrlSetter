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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UrlResolver
{
    public static final String HEAD_METHOD = "HEAD";
    public static final String GET_METHOD = "GET";

    public static final String LOCATION_HEADER = "Location";
    public static final int HTTP_TEMP_REDIRECT = 307;

    private final Bus bus;
    private final HttpClient httpClient;

    public UrlResolver ( Bus bus, HttpClient httpClient )
    {
        this.bus = bus;
        this.httpClient = httpClient;

        bus.register( this );
    }

    @Subscribe
    public void resolveURL ( ResolveUrl event )
    {
        Uri uri = event.getUri();

        try
        {
            Uri resolvedUri = resolveUrl( uri.toString() );

            bus.post( new FoundUrl( uri, resolvedUri ) );
        }
        catch ( Exception e )
        {
            bus.post( new DownloadingError( uri, uri, e ) );
        }
    }

    private Uri resolveUrl ( String startUrl ) throws IOException
    {
        String currentUrl = startUrl;
        String nextUrl = currentUrl;

        do
        {
            currentUrl = nextUrl;
            nextUrl = findNextUrl( currentUrl );
        }
        while ( nextUrl != null && nextUrl.contains( "http" ) );

        return Uri.parse( currentUrl );
    }

    private String findNextUrl ( String url ) throws IOException
    {
        return processHeadUrl( url );
    }

    private String processHeadUrl ( String url ) throws IOException
    {
        return processUrl( url, HEAD_METHOD );
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

            if ( isRedirection( responseCode ) )
            {
                return connection.getHeaderField( LOCATION_HEADER ).replace( " ", "%20" );
            }
            else
            {
                return null;
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
                || responseCode == HttpURLConnection.HTTP_SEE_OTHER
                || responseCode == HTTP_TEMP_REDIRECT;
    }
}
