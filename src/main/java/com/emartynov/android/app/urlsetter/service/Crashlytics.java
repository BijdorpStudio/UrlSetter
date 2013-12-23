package com.emartynov.android.app.urlsetter.service;

import android.content.Context;

public class Crashlytics
{
    public void start ( Context context )
    {
        com.crashlytics.android.Crashlytics.start( context );
    }
}
