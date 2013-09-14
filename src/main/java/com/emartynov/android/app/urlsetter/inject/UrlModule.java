package com.emartynov.android.app.urlsetter.inject;

import javax.inject.Singleton;

import com.emartynov.android.app.urlsetter.ui.UrlActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import dagger.Module;
import dagger.Provides;

@Module ( injects = UrlActivity.class )
public class UrlModule
{
    @Provides
    @Singleton
    public Bus getBus ()
    {
        return new Bus( ThreadEnforcer.ANY );
    }
}
