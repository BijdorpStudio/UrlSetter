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
import com.emartynov.android.app.urlsetter.UrlTestBase;
import com.emartynov.android.app.urlsetter.model.event.FoundUrl;
import com.emartynov.android.app.urlsetter.model.event.ResolveUrl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Config( emulateSdk = 18, reportSdk = 18, manifest = "src/main/AndroidManifest.xml" )
@RunWith( RobolectricTestRunner.class )
public class UrlServiceTest
    extends UrlTestBase
{
    private UrlService service = new UrlService();

    @Before
    public void setUp()
        throws Exception
    {
        service.onCreate();
    }

    @Test
    public void startsServicesAfterCreate()
        throws Exception
    {
        verify( getCrashlytics() ).start( service );
        verify( getMixpanel() ).init( service );
    }

    @Test
    public void intentProceedNonSticky()
        throws Exception
    {
        int type = service.onStartCommand( null, 0, 0 );

        assertThat( type ).isEqualTo( Service.START_NOT_STICKY );
    }

    @Test
    public void nullIntentIsIgnored()
        throws Exception
    {
        service.onStartCommand( null, 0, 0 );

        verifyNoMoreInteractions( getExecutor() );
    }

    @Test
    public void whenResolveAskedThenPutTaskInExecutor()
        throws Exception
    {
        String uriString = "http://google.com";

        Intent intent = createIntentWithUri( uriString );

        service.onStartCommand( intent, 0, 0 );

        verify( getExecutor() ).execute( any( Runnable.class ) );
    }

    @Test
    public void askToResolveShortenedUrl()
        throws Exception
    {
        String uriString = "http://google.com";

        Intent intent = createIntentWithUri( uriString );

        service.onStartCommand( intent, 0, 0 );

        checkEventToResolveGenerated( uriString );
    }

    private Intent createIntentWithUri( String uriString )
    {
        Intent intent = new Intent();
        intent.setData( Uri.parse( uriString ) );
        return intent;
    }

    private void checkEventToResolveGenerated( String uriString )
    {
        runExecutor();

        ArgumentCaptor<ResolveUrl> captor = ArgumentCaptor.forClass( ResolveUrl.class );
        verify( getResolver() ).resolveURL( captor.capture() );
        assertThat( captor.getValue().getUri().toString() ).isEqualTo( uriString );
    }

    @Test
    public void askToResolveFacebookUrl()
        throws Exception
    {
        String uriString =
            "http://m.facebook.com/l.php?u=http%3A%2F%2Fmashable.com%2F2014%2F01%2F12%2Fstages-of-snapchat-comic";

        Intent intent = createIntentWithUri( uriString );

        service.onStartCommand( intent, 0, 0 );

        checkEventToResolveGenerated( Uri.parse( uriString ).getQueryParameter( "u" ) );
    }

    @Test
    public void checkCacheFirst()
        throws Exception
    {
        String uriString = "http://google.com";

        Intent intent = createIntentWithUri( uriString );

        service.onStartCommand( intent, 0, 0 );

        runExecutor();
        verify( getCache() ).get( Uri.parse( uriString ) );
    }

    @Test
    public void whenResolverReturnsSameUriThenError()
        throws Exception
    {
        String uriString = "http://t.co/djsdhjshd";

        Uri uri = Uri.parse( uriString );
        when( getResolver().resolveURL( any( ResolveUrl.class ) ) ).thenReturn( new FoundUrl( uri, uri ) );
        when( getIntentHelper().isFilterUri( service, uri ) ).thenReturn( true );

        service.resolveUrl( new ResolveUrl( uri ) );

        verify( getCrashlytics() ).logException( any( RuntimeException.class ) );
    }
}
