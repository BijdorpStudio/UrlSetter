package com.emartynov.android.app.urlsetter.ui;

import android.app.Activity;
import android.os.Bundle;

public class InjectedActivity extends Activity
{
    @Override
    protected void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

//        ((UrlApplication)getApplication()).inject(this);
    }
}
