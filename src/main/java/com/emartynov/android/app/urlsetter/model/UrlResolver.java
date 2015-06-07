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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

public class UrlResolver {
    public static final String HEAD_METHOD = "HEAD";

    public static final String LOCATION_HEADER = "Location";

    public static final int HTTP_TEMP_REDIRECT = 307;

    private final OkHttpClient httpClient;

    public UrlResolver(OkHttpClient httpClient) {
        this.httpClient = httpClient;
        httpClient.setFollowRedirects(false);
        httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
    }

    public UrlEvent resolveURL(ResolveUrl event) {
        Uri uri = event.getUri();

        try {
            Uri resolvedUri = resolveUrl(uri.toString());

            return new FoundUrl(uri, resolvedUri);
        } catch (Exception e) {
            return new DownloadingError(uri, uri, e);
        }
    }

    private Uri resolveUrl(String startUrl)
            throws IOException {
        String currentUrl = startUrl;
        String nextUrl = currentUrl;

        do {
            currentUrl = nextUrl;
            nextUrl = findNextUrl(currentUrl);
        }
        while (nextUrl != null && nextUrl.contains("http"));

        return Uri.parse(currentUrl);
    }

    private String findNextUrl(String url)
            throws IOException {
        return processUrl(url);
    }

    private String processUrl(String url)
            throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding", "")
                .head()
                .build();

        Response response = httpClient.newCall(request).execute();

        if (isRedirection(response.code())) {
            return response.header(LOCATION_HEADER).replace(" ", "%20");
        } else {
            return null;
        }
    }

    private boolean isRedirection(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                responseCode == HTTP_TEMP_REDIRECT;
    }
}
