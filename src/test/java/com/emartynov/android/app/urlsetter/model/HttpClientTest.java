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

import org.junit.Test;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.fest.assertions.api.Assertions.assertThat;

public class HttpClientTest
{
    private HttpClient client = new HttpClient();

    @Test
    public void correctlyCreatesConnection()
        throws Exception
    {
        String address = "http://google.com";
        HttpURLConnection connection = client.open( new URL( address ) );

        assertThat( connection.getURL() ).isEqualTo( new URL( address ) );
    }
}
