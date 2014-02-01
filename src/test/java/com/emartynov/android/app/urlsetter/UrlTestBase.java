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

package com.emartynov.android.app.urlsetter;

import com.emartynov.android.app.urlsetter.android.TestUrlApplication;
import com.emartynov.android.app.urlsetter.android.TestUrlModule;
import com.emartynov.android.app.urlsetter.android.packagemanager.IntentHelper;
import com.emartynov.android.app.urlsetter.model.UrlDiskLruCache;
import com.emartynov.android.app.urlsetter.model.UrlResolver;
import com.emartynov.android.app.urlsetter.service.Crashlytics;
import com.emartynov.android.app.urlsetter.service.Mixpanel;
import com.squareup.otto.Bus;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;

import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Mockito.verify;

public class UrlTestBase
{
    private TestUrlModule getTestModule()
    {
        return ( (TestUrlApplication) Robolectric.application ).getTestModule();
    }

    public Crashlytics getCrashlytics()
    {
        return getTestModule().getCrashlytics();
    }

    public Bus getBus()
    {
        return getTestModule().getBus();
    }

    public Mixpanel getMixpanel()
    {
        return getTestModule().getLogger();
    }

    public UrlDiskLruCache getCache()
    {
        return getTestModule().getCache();
    }

    public ThreadPoolExecutor getExecutor()
    {
        return getTestModule().getExecutor();
    }

    public UrlResolver getResolver()
    {
        return getTestModule().getURLResolver( getTestModule().getHttpClient() );
    }

    public void runExecutor()
    {
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass( Runnable.class );
        verify( getExecutor() ).execute( captor.capture() );
        captor.getValue().run();
    }

    public IntentHelper getIntentHelper()
    {
        return getTestModule().getIntentHelper();
    }

}
