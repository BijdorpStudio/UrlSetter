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
import com.emartynov.android.app.urlsetter.model.event.UrlEvent;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class UrlResolverTest {
    private UrlResolver target;

    private OkHttpClient client = mock(OkHttpClient.class);
    private Call call = mock(Call.class);

    @Before
    public void setUp()
            throws Exception {
        when(client.newCall(any(Request.class))).thenReturn(call);

        target = new UrlResolver(client);
    }

    @Test
    public void resolveMovePermanent()
            throws Exception {
        String endUrl = "http://test.com";
        redirectTo(endUrl, HttpURLConnection.HTTP_MOVED_PERM);

        FoundUrl result = (FoundUrl) resolveUrl("http://google.com");

        checkUrlFound(result, endUrl);
    }

    private void redirectTo(String endUrl, int responseCode)
            throws IOException {
        Request request = new Request.Builder()
                .url(endUrl)
                .build();
        Response moveResponse = new Response.Builder()
                .code(responseCode)
                .protocol(Protocol.HTTP_1_1)
                .addHeader(UrlResolver.LOCATION_HEADER, endUrl)
                .request(request)
                .build();
        Response okResponse = new Response.Builder()
                .code(HttpURLConnection.HTTP_OK)
                .protocol(Protocol.HTTP_1_1)
                .addHeader(UrlResolver.LOCATION_HEADER, endUrl)
                .request(request)
                .build();

        when(call.execute()).thenReturn(moveResponse, okResponse);
    }

    private UrlEvent resolveUrl(String uriString) {
        ResolveUrl event = new ResolveUrl(Uri.parse(uriString));
        return target.resolveURL(event);
    }

    private void checkUrlFound(FoundUrl result, String endUrl) {
        assertThat(result.getResolvedUri().toString()).isEqualTo(endUrl);
    }

    @Test
    public void nonShortenedUrlReturnsSame()
            throws Exception {
        String endUrl = "http://test.com";
        redirectTo(endUrl, HttpURLConnection.HTTP_OK);

        FoundUrl result = (FoundUrl) resolveUrl(endUrl);

        checkUrlFound(result, endUrl);
    }

    @Test
    public void resolveMovedTemporary()
            throws Exception {
        String endUrl = "http://test.com";
        redirectTo(endUrl, HttpURLConnection.HTTP_MOVED_TEMP);

        FoundUrl result = (FoundUrl) resolveUrl("http://google.com");

        checkUrlFound(result, endUrl);
    }

    @Test
    public void resolveSeeOther()
            throws Exception {
        String endUrl = "http://test.com";
        redirectTo(endUrl, HttpURLConnection.HTTP_SEE_OTHER);

        FoundUrl result = (FoundUrl) resolveUrl("http://google.com");

        checkUrlFound(result, endUrl);
    }

    @Test
    public void resolveTemporaryRedirect()
            throws Exception {
        String endUrl = "http://test.com";
        redirectTo(endUrl, UrlResolver.HTTP_TEMP_REDIRECT);

        FoundUrl result = (FoundUrl) resolveUrl("http://google.com");

        checkUrlFound(result, endUrl);
    }

    private void verifyErrorEventWithException(DownloadingError result, Class<? extends Exception> exceptionType) {
        assertThat(result.getException()).isInstanceOf(exceptionType);
    }

    @Test
    public void whenIOExceptionThenEventFired()
            throws Exception {
        when(call.execute()).thenThrow(new IOException());

        DownloadingError result = (DownloadingError) resolveUrl("http://google.com");

        verifyErrorEventWithException(result, IOException.class);
    }
}
