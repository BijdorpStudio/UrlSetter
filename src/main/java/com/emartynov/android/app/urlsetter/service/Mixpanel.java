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

package com.emartynov.android.app.urlsetter.service;

import android.content.Context;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import org.json.JSONObject;

import java.util.Map;

public class Mixpanel
{
    public static final String PASSED_BAD_URL_EVENT = "Passed bad url";

    public static final String PASSED_URL_EVENT = "Passed url";

    public static final String RESOLVING_ERROR_EVENT = "Error";

    public static final String RESOLVED_URL_EVENT = "Resolved";

    public static final String RESOLVING_STARTED_EVENT = "Started";

    private final String token;

    private MixpanelAPI logger;

    public Mixpanel( String apiToken )
    {
        this.token = apiToken;
    }

    public void init( Context context )
    {
        logger = MixpanelAPI.getInstance( context, token );
    }

    public void flush()
    {
        logger.flush();
    }

    public void trackEvent( String name, Map<String, String> parameters )
    {
        JSONObject properties = new JSONObject( parameters );
        logger.track( name, properties );
    }

    public void trackEvent( String name )
    {
        logger.track( name, null );
    }
}
