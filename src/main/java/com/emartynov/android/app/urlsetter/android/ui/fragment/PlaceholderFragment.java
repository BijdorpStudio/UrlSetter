package com.emartynov.android.app.urlsetter.android.ui.fragment;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emartynov.android.app.urlsetter.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment
{

    private int fragmentLayout;

    public PlaceholderFragment ()
    {
    }

    @Override
    public void onInflate ( Activity activity, AttributeSet attrs,
                            Bundle savedInstanceState )
    {
        super.onInflate( activity, attrs, savedInstanceState );

        TypedArray a = activity.obtainStyledAttributes( attrs,
                R.styleable.PlaceholderFragment );
        fragmentLayout = a.getResourceId( R.styleable.PlaceholderFragment_fragment_layout, -1 );
        a.recycle();
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState )
    {
        View rootView = inflater.inflate( fragmentLayout, container, false );
        return rootView;
    }
}
