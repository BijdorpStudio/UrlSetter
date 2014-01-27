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

import android.app.Application;

import com.emartynov.android.app.urlsetter.inject.UrlModule;
import com.emartynov.android.app.urlsetter.service.Crashlytics;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class UrlApplication extends Application
{
    private ObjectGraph objectGraph;

    @Inject
    Crashlytics crashlytics;

    @Override
    public void onCreate ()
    {
        super.onCreate();

        Object urlModule = getUrlModule();

        objectGraph = ObjectGraph.create( urlModule );
        objectGraph.inject( this );

        crashlytics.start( this );
    }

    protected Object getUrlModule ()
    {
        UrlModule urlModule = new UrlModule();
        try
        {
            urlModule.init( this );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "App properties loading failed", e );
        }
        return urlModule;
    }

    public void inject ( Object objectToInject )
    {
        objectGraph.inject( objectToInject );
    }

}
