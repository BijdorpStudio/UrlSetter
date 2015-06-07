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

import com.emartynov.android.app.urlsetter.android.packagemanager.IntentHelper;
import com.emartynov.android.app.urlsetter.android.ui.UrlActivity;
import com.emartynov.android.app.urlsetter.inject.UrlModule;
import com.emartynov.android.app.urlsetter.model.UrlDiskLruCache;
import com.emartynov.android.app.urlsetter.model.UrlResolver;
import com.emartynov.android.app.urlsetter.service.Crashlytics;
import com.emartynov.android.app.urlsetter.service.Mixpanel;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Mockito.mock;

@Module(overrides = true, includes = UrlModule.class,
        injects = { UrlActivity.class, TestUrlApplication.class, UrlService.class })
public class TestUrlModule
{
    private Crashlytics crashlytics = mock( Crashlytics.class );

    private Bus bus = mock( Bus.class );

    private Mixpanel logger = mock( Mixpanel.class );

    private UrlDiskLruCache cache = mock( UrlDiskLruCache.class );

    private UrlResolver resolver = mock( UrlResolver.class );

    private ThreadPoolExecutor executor = mock( ThreadPoolExecutor.class );

    private OkHttpClient httpClient = mock( OkHttpClient.class );

    private IntentHelper intentHelper = mock( IntentHelper.class );

    @Provides
    @Singleton
    public Crashlytics getCrashlytics()
    {
        return crashlytics;
    }

    @Provides
    @Singleton
    public Bus getBus()
    {
        return bus;
    }

    @Provides
    @Singleton
    public Mixpanel getLogger()
    {
        return logger;
    }

    @Provides
    public UrlDiskLruCache getCache()
    {
        return cache;
    }

    @Provides
    public ThreadPoolExecutor getExecutor()
    {
        return executor;
    }

    @Provides
    @Singleton
    public UrlResolver getURLResolver( OkHttpClient httpClient )
    {
        return resolver;
    }

    @Provides
    @Singleton
    public OkHttpClient getHttpClient()
    {
        return httpClient;
    }

    @Provides
    public IntentHelper getIntentHelper()
    {
        return intentHelper;
    }
}
