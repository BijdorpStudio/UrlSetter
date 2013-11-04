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

import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import com.emartynov.android.app.urlsetter.model.event.ResolveURL;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class UrlActivity extends InjectedActivity
{
    @Inject
    Bus bus;

    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        Uri uri = getIntent().getData();

        bus.post( new ResolveURL( uri ) );

        finish();
    }
}