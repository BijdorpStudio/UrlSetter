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

package com.emartynov.android.app.urlsetter.android.ui;

import android.content.Intent;
import android.os.Bundle;

import com.emartynov.android.app.urlsetter.android.UrlService;
import com.emartynov.android.app.urlsetter.service.Crashlytics;

import javax.inject.Inject;

public class UrlActivity extends InjectedActivity
{
    @Inject
    Crashlytics crashlytics;

    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        crashlytics.start( this );

        Intent service = new Intent( getIntent() );
        service.setClass( this, UrlService.class );
        startService( service );

        finish();
    }
}