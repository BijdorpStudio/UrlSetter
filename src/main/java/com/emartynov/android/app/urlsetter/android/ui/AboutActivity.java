package com.emartynov.android.app.urlsetter.android.ui;

import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.service.Crashlytics;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        linkifyTwitter();
    }

    private void linkifyTwitter ()
    {
        TextView tweetView = (TextView) findViewById( R.id.twitter_name );

        Pattern atMentionPattern = Pattern.compile( "@([A-Za-z0-9_]+)" );
        String atMentionScheme = "http://twitter.com/";

        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter()
        {
            //skip the first character to filter out '@'
            public String transformUrl ( final Matcher match, String url )
            {
                String group = match.group( 1 );
                return group;
            }
        };

        Linkify.addLinks( tweetView, atMentionPattern, atMentionScheme, null, transformFilter );
    }

}
