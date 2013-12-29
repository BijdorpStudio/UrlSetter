package com.emartynov.android.app.urlsetter.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        if ( savedInstanceState == null )
        {
            getSupportFragmentManager().beginTransaction()
                    .add( R.id.container, new PlaceholderFragment() )
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {

        public PlaceholderFragment ()
        {
        }

        @Override
        public View onCreateView ( LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState )
        {
            View rootView = inflater.inflate( R.layout.fragment_about, container, false );
            return rootView;
        }
    }

}
