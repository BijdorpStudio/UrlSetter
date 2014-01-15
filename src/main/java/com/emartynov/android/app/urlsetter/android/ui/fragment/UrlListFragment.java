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

package com.emartynov.android.app.urlsetter.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.ui.adapter.UrlExampleAdapter;

public class UrlListFragment extends ListFragment
{

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.list_fragment, container, false );

        ListView list = (ListView) view.findViewById( android.R.id.list );

        String[] services = getResources().getStringArray( R.array.services );
        String[] urls = getResources().getStringArray( R.array.service_urls );

        list.setAdapter( new UrlExampleAdapter( services, urls, inflater ) );

        return view;
    }
}
