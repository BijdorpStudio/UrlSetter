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

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.emartynov.android.app.urlsetter.android.UrlService;
import com.emartynov.android.app.urlsetter.android.ui.UrlActivity;
import com.emartynov.android.app.urlsetter.model.UrlResolver;
import com.emartynov.android.app.urlsetter.service.Mixpanel;
import com.jakewharton.disklrucache.DiskLruCache;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module (injects = { UrlActivity.class, UrlService.class })
public class UrlModule
{
    private String mixpanelToken;
    private File cacheDir;
    private int appVersion;

    public void init ( Context context ) throws PackageManager.NameNotFoundException
    {
        ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo( context.getPackageName(), PackageManager.GET_META_DATA );
        mixpanelToken = appInfo.metaData.getString( "com.mixpanel.ApiToken" );

        appVersion = context.getPackageManager().getPackageInfo( context.getPackageName(), 0 ).versionCode;
        cacheDir = context.getFilesDir();
    }

    @Provides
    @Singleton
    public Bus getBus ()
    {
        return new Bus( ThreadEnforcer.ANY );
    }

    @Provides
    @Singleton
    public Mixpanel getLogger ()
    {
        return new Mixpanel( mixpanelToken );
    }

    @Provides
    @Singleton
    public UrlResolver getURLResolver ( Bus bus )
    {
        return new UrlResolver( bus );
    }

    @Provides
    public DiskLruCache getCache ()
    {
        try
        {
            return DiskLruCache.open( cacheDir, appVersion, 1, 100 * 1024 );
        }
        catch ( IOException e )
        {
            return null;
        }
    }
}
