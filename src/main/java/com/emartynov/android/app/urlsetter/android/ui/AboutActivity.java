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

package com.emartynov.android.app.urlsetter.android.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;
import com.emartynov.android.app.urlsetter.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AboutActivity
    extends InjectedActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_about );

        linkifyTwitterName();

        updateApplicationVersion();
    }

    private void updateApplicationVersion()
    {
        try
        {
            PackageInfo pInfo = getPackageManager().getPackageInfo( getPackageName(), 0 );
            String versionText = String.format( "v%s", pInfo.versionName );
            TextView versionView = (TextView) findViewById( R.id.version );
            versionView.setText( versionText );
        }
        catch ( PackageManager.NameNotFoundException ignored )
        {
        }
    }

    private void linkifyTwitterName()
    {
        TextView tweetView = (TextView) findViewById( R.id.twitter_name );

        Pattern atMentionPattern = Pattern.compile( "@([A-Za-z0-9_]+)" );
        String atMentionScheme = "http://twitter.com/";

        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter()
        {
            //skip the first character to filter out '@'
            @Override
            public String transformUrl( final Matcher match, String url )
            {
                return match.group( 1 );
            }
        };

        Linkify.addLinks( tweetView, atMentionPattern, atMentionScheme, null, transformFilter );
    }

}
