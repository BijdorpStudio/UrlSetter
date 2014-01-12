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

import com.emartynov.android.app.urlsetter.model.event.FoundUrl;
import com.emartynov.android.app.urlsetter.model.event.ResolveUrl;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ThreadPoolExecutor;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith ( RobolectricTestRunner.class )
public class UrlResolverTest
{
    private UrlResolver target;

    private Bus bus = mock( Bus.class );
    private HttpClient client = mock( HttpClient.class );
    private ThreadPoolExecutor executor = mock( ThreadPoolExecutor.class );
    private HttpURLConnection connection = mock( HttpURLConnection.class );

    @Before
    public void setUp () throws Exception
    {
        when( client.open( any( URL.class ) ) ).thenReturn( connection );

        target = new UrlResolver( bus, client, executor );
    }

    @Test
    public void registerToBus () throws Exception
    {
        verify( bus ).register( target );
    }

    @Test
    public void whenResolveAskedThenPutTaskInExecutor () throws Exception
    {
        ResolveUrl event = new ResolveUrl( mock( Uri.class ) );

        target.resolveURL( event );

        verify( executor ).execute( any( Runnable.class ) );
    }

    @Test
    public void whenTasksInQueueThenNonIdle () throws Exception
    {
        long completedCount = 3L;
        when( executor.getCompletedTaskCount() ).thenReturn( completedCount );
        when( executor.getTaskCount() ).thenReturn( completedCount + 1 );

        assertThat( target.isIdle() ).isFalse();
    }

    @Test
    public void whenAllTaskCompletedThenIdle () throws Exception
    {
        long completedCount = 3L;
        when( executor.getCompletedTaskCount() ).thenReturn( completedCount );
        when( executor.getTaskCount() ).thenReturn( completedCount );

        assertThat( target.isIdle() ).isTrue();
    }

    @Test
    public void resolveMovePermanent () throws Exception
    {
        String endUrl = "http://test.com";
        redirectTo( endUrl, HttpURLConnection.HTTP_MOVED_PERM );

        resolveUrl( "http://google.com" );

        runExecutor();

        checkUrlFound( endUrl );
    }

    private void redirectTo ( String endUrl, int moveResponseCode ) throws IOException
    {
        when( connection.getResponseCode() ).thenReturn( moveResponseCode, HttpURLConnection.HTTP_OK );
        when( connection.getHeaderField( UrlResolver.LOCATION_HEADER ) ).thenReturn( endUrl );
    }

    private void resolveUrl ( String uriString )
    {
        ResolveUrl event = new ResolveUrl( Uri.parse( uriString ) );
        target.resolveURL( event );
    }

    private void checkUrlFound ( String endUrl )
    {
        ArgumentCaptor<FoundUrl> eventCaptor = ArgumentCaptor.forClass( FoundUrl.class );
        verify( bus ).post( eventCaptor.capture() );
        assertThat( eventCaptor.getValue().getResolvedUri().toString() ).isEqualTo( endUrl );
    }

    private void runExecutor ()
    {
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass( Runnable.class );
        verify( executor ).execute( captor.capture() );
        captor.getValue().run();
    }

    @Test
    public void nonShortenedUrlReturnsSame () throws Exception
    {
        String endUrl = "http://test.com";
        when( connection.getResponseCode() ).thenReturn( HttpURLConnection.HTTP_OK );

        resolveUrl( endUrl );

        runExecutor();

        checkUrlFound( endUrl );
    }

    @Test
    public void resolveMovedTemporary () throws Exception
    {
        String endUrl = "http://test.com";
        redirectTo( endUrl, HttpURLConnection.HTTP_MOVED_TEMP );

        resolveUrl( "http://google.com" );

        runExecutor();

        checkUrlFound( endUrl );
    }

    @Test
    public void resolveSeeOther () throws Exception
    {
        String endUrl = "http://test.com";
        redirectTo( endUrl, HttpURLConnection.HTTP_SEE_OTHER );

        resolveUrl( "http://google.com" );

        runExecutor();

        checkUrlFound( endUrl );
    }

    @Test
    public void resolveTemporaryRedirect () throws Exception
    {
        String endUrl = "http://test.com";
        redirectTo( endUrl, UrlResolver.HTTP_TEMP_REDIRECT );

        resolveUrl( "http://google.com" );

        runExecutor();

        checkUrlFound( endUrl );
    }
}
