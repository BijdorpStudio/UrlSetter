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

package com.emartynov.android.app.urlsetter.inject;

import javax.inject.Singleton;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import com.emartynov.android.app.urlsetter.android.ui.UrlActivity;
import com.emartynov.android.app.urlsetter.model.URLResolver;
import com.emartynov.android.app.urlsetter.service.mixpanel.MixLogger;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import dagger.Module;
import dagger.Provides;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Module (injects = {UrlActivity.class, URLResolver.class})
public class UrlModule
{
    private Properties config;

    public void init ( Context context ) throws IOException
    {
        AssetFileDescriptor descriptor = context.getAssets().openFd( "service.properties" );
        FileReader reader = new FileReader( descriptor.getFileDescriptor() );

        config = new Properties();
        config.load( reader );

        reader.close();
    }

    @Provides
    @Singleton
    public Bus getBus ()
    {
        return new Bus( ThreadEnforcer.ANY );
    }

    @Provides
    @Singleton
    public MixLogger getLogger ()
    {
        return new MixLogger( config.getProperty( "mixpanel.token" ) );
    }
}
