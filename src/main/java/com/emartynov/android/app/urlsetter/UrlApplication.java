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
