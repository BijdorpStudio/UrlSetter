package com.emartynov.android.app.urlsetter.android.ui;

import android.os.Bundle;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.service.Crashlytics;

import javax.inject.Inject;

public class AboutActivity extends InjectedActivity
{
    @Inject
    Crashlytics crashlytics;

    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        crashlytics.start( this );

        setContentView( R.layout.activity_about );
    }

}
