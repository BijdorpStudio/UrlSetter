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

package com.emartynov.android.app.urlsetter.model.event;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class UrlEvent
{
    private final Uri uri;

    UrlEvent ( Uri uri )
    {
        this.uri = uri;
    }

    public Map<String, String> getLoggingParams ()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "Host", uri.getHost() );
        return params;
    }

    public Uri getUri ()
    {
        return uri;
    }
}
