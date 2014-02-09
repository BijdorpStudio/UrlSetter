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

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.emartynov.android.app.urlsetter.android.UrlApplication;
import com.emartynov.android.app.urlsetter.service.Crashlytics;
import com.emartynov.android.app.urlsetter.service.Mixpanel;

import javax.inject.Inject;

public class InjectedActivity
    extends ActionBarActivity
{
    @Inject
    Crashlytics crashlytics;

    @Inject
    Mixpanel mixpanel;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        ( (UrlApplication) getApplication() ).inject( this );

        crashlytics.start( this );

        mixpanel.trackEvent( getClass().getSimpleName() );
    }

    @Override
    protected void onDestroy()
    {
        mixpanel.flush();

        super.onDestroy();
    }
}
