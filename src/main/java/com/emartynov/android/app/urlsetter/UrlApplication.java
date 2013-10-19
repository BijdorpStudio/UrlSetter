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

package com.emartynov.android.app.urlsetter;

import com.emartynov.android.app.urlsetter.model.URLResolver;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import android.app.Application;

public class UrlApplication extends Application
{
    private Bus bus;
    private URLResolver urlResolver;

    @Override
    public void onCreate ()
    {
        super.onCreate();

        bus = new Bus( ThreadEnforcer.ANY );
        urlResolver = new URLResolver( bus );
    }

    public Bus getBus ()
    {
        return bus;
    }
}
