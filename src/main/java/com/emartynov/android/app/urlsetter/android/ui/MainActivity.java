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

package com.emartynov.android.app.urlsetter.android.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;
import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.ui.adapter.UrlExampleAdapter;

public class MainActivity extends ActionBarActivity
{
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        Crashlytics.start( this );

        setContentView( R.layout.main );

        initList();
    }

    private void initList ()
    {
        ListView view = (ListView) findViewById( android.R.id.list );
        String[] services = getResources().getStringArray( R.array.services );
        String[] urls = getResources().getStringArray( R.array.urls );
        view.setAdapter( new UrlExampleAdapter( services, urls, getLayoutInflater() ) );
    }
}
