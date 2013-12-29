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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.ui.adapter.UrlExampleAdapter;
import com.emartynov.android.app.urlsetter.service.Crashlytics;

import javax.inject.Inject;

public class MainActivity extends InjectedActivity
{
    @Inject
    Crashlytics crashlytics;

    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        crashlytics.start( this );

        setContentView( R.layout.activity_main );

        initList();
    }

    private void initList ()
    {
        ListView view = (ListView) findViewById( android.R.id.list );

        String[] services = getResources().getStringArray( R.array.services );
        String[] urls = getResources().getStringArray( R.array.urls );

        view.setAdapter( new UrlExampleAdapter( services, urls, getLayoutInflater() ) );
    }

    @Override
    public boolean onCreateOptionsMenu ( Menu menu )
    {
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected ( MenuItem item )
    {
        if ( item.getItemId() == R.id.action_about )
        {
            startActivity( new Intent( this, AboutActivity.class ) );
            return true;
        }
        else
        {
            return super.onOptionsItemSelected( item );
        }
    }
}
