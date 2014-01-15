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

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.ui.adapter.UrlExampleAdapter;

public class UrlListFragment extends ListFragment
{

    public static final String KEYS = "KEYS";
    public static final String VALUES = "VALUES";
    private static final String TITLE = "TITLE";

    private int keyIds;
    private int valuesIds;

    private String title;

    @SuppressWarnings ( "UnusedDeclaration" )
    public UrlListFragment ()
    {
    }

    public UrlListFragment ( String title, int keyIds, int valuesIds )
    {
        this.title = title;
        this.keyIds = keyIds;
        this.valuesIds = valuesIds;
    }

    @Override
    public void onInflate ( Activity activity, AttributeSet attrs, Bundle savedInstanceState )
    {
        super.onInflate( activity, attrs, savedInstanceState );

        TypedArray a = activity.obtainStyledAttributes( attrs,
                R.styleable.PlaceholderFragment );
        int keys = a.getResourceId( R.styleable.PlaceholderFragment_list_fragment_keys, -1 );
        if ( keys != -1 )
        {
            keyIds = keys;
        }
        int values = a.getResourceId( R.styleable.PlaceholderFragment_list_fragment_values, -1 );
        if ( values != -1 )
        {
            valuesIds = values;
        }
        a.recycle();
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        if ( savedInstanceState != null )
        {
            title = savedInstanceState.getString( TITLE );
            keyIds = savedInstanceState.getInt( KEYS );
            valuesIds = savedInstanceState.getInt( VALUES );
        }

        View view = inflater.inflate( R.layout.list_fragment, container, false );

        ListView list = (ListView) view.findViewById( android.R.id.list );

        String[] services = getResources().getStringArray( keyIds );
        String[] urls = getResources().getStringArray( valuesIds );

        list.setAdapter( new UrlExampleAdapter( services, urls, inflater ) );

        return view;
    }

    public CharSequence getTitle ()
    {
        return title;
    }

    @Override
    public void onSaveInstanceState ( Bundle outState )
    {
        super.onSaveInstanceState( outState );

        outState.putInt( KEYS, keyIds );
        outState.putInt( VALUES, valuesIds );
        outState.putString( TITLE, title );
    }
}
