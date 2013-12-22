package com.emartynov.android.app.urlsetter.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emartynov.android.app.urlsetter.R;

public class UrlExampleAdapter extends BaseAdapter
{
    private final String[] services;
    private final String[] urls;
    private LayoutInflater inflater;

    public UrlExampleAdapter ( String[] services, String[] urls, LayoutInflater inflater )
    {
        if ( services.length != urls.length )
        {
            throw new IllegalArgumentException( "Arrays length don't match" );
        }

        this.services = services;
        this.urls = urls;
        this.inflater = inflater;
    }

    @Override
    public int getCount ()
    {
        return services.length;
    }

    @Override
    public Object getItem ( int position )
    {
        return urls[ position ];
    }

    @Override
    public long getItemId ( int position )
    {
        return position;
    }

    @Override
    public View getView ( int position, View convertView, ViewGroup parent )
    {
        if ( convertView == null )
        {
            convertView = inflater.inflate( R.layout.url_example_item, parent, false );
        }

        ( (TextView) convertView.findViewById( R.id.service_text ) ).setText( services[ position ] );
        ( (TextView) convertView.findViewById( R.id.url_example ) ).setText( urls[ position ] );

        return convertView;
    }
}
