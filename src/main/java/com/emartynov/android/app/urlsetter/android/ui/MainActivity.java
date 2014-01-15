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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.ui.fragment.UrlListFragment;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends InjectedActivity
{
    @Override
    public void onCreate ( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );

        ViewPager pager = (ViewPager) findViewById( R.id.viewpager );

        if ( pager != null )
        {
            List<UrlListFragment> fragments = getFragments();
            FragmentPageAdapter pageAdapter = new FragmentPageAdapter( getSupportFragmentManager(), fragments );
            pager.setAdapter( pageAdapter );
        }
    }

    private List<UrlListFragment> getFragments ()
    {
        return Arrays.asList(
                new UrlListFragment( getString( R.string.services_title ), R.array.services, R.array.service_urls ),
                new UrlListFragment( getString( R.string.applications_title ), R.array.applications, R.array.app_urls )
        );
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

    private class FragmentPageAdapter extends FragmentPagerAdapter
    {
        private final List<UrlListFragment> fragments;

        public FragmentPageAdapter ( FragmentManager fragmentManager, List<UrlListFragment> fragments )
        {
            super( fragmentManager );

            this.fragments = fragments;
        }

        @Override
        public int getCount ()
        {
            return fragments.size();
        }

        @Override
        public Fragment getItem ( int position )
        {
            return fragments.get( position );
        }

        @Override
        public CharSequence getPageTitle ( int position )
        {
            return fragments.get( position ).getTitle();
        }
    }
}
