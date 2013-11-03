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

package com.emartynov.android.app.urlsetter.android;

import com.emartynov.android.app.urlsetter.inject.UrlModule;
import com.emartynov.android.app.urlsetter.model.URLResolver;
import com.emartynov.android.app.urlsetter.android.ui.InjectedActivity;

import android.app.Application;
import dagger.ObjectGraph;

import java.io.IOException;

public class UrlApplication extends Application
{
    private URLResolver urlResolver;
    private ObjectGraph objectGraph;

    @Override
    public void onCreate ()
    {
        super.onCreate();

        UrlModule urlModule = getUrlModule();
        objectGraph = ObjectGraph.create( urlModule );

        urlResolver = objectGraph.get( URLResolver.class );
    }

    private UrlModule getUrlModule ()
    {
        UrlModule urlModule = new UrlModule();
        try
        {
            urlModule.init( this );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "App properties loading failed", e );
        }
        return urlModule;
    }

    public void inject ( InjectedActivity injectedActivity )
    {
        objectGraph.inject( injectedActivity );
    }
}
