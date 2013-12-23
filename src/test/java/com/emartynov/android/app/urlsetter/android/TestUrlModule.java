package com.emartynov.android.app.urlsetter.android;

import com.emartynov.android.app.urlsetter.android.ui.UrlActivity;
import com.emartynov.android.app.urlsetter.inject.UrlModule;
import com.emartynov.android.app.urlsetter.service.Crashlytics;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module ( overrides = true, includes = UrlModule.class, injects = { UrlActivity.class, TestUrlApplication.class } )
public class TestUrlModule
{
    private Crashlytics crashlytics = mock( Crashlytics.class );

    @Provides
    @Singleton
    public Crashlytics getCrashlytics ()
    {
        return crashlytics;
    }
}
