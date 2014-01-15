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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emartynov.android.app.urlsetter.R;
import com.emartynov.android.app.urlsetter.android.UrlApplication;
import com.emartynov.android.app.urlsetter.model.event.UserInputValue;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class EnterShortenedUrlFragment extends DialogFragment implements TextView.OnEditorActionListener, View.OnClickListener
{
    @Inject
    Bus bus;

    private EditText linkEdit;

    @Override
    public void onAttach ( Activity activity )
    {
        super.onAttach( activity );

        ( (UrlApplication) activity.getApplication() ).inject( this );
    }

    @Override
    public View onCreateView ( LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState )
    {
        View view = inflater.inflate( R.layout.link_enter_dialog, container );
        linkEdit = (EditText) view.findViewById( R.id.link_input );
        Button startButton = (Button) view.findViewById( R.id.start_button );
        startButton.setOnClickListener( this );

        linkEdit.requestFocus();
        getDialog().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        linkEdit.setOnEditorActionListener( this );

        getDialog().setTitle( getString( R.string.unshort_url ) );

        return view;
    }

    @Override
    public boolean onEditorAction ( TextView view, int actionId, KeyEvent event )
    {
        if ( EditorInfo.IME_ACTION_DONE == actionId )
        {
            onClick( null );
            return true;
        }
        return false;
    }

    @Override
    public void onClick ( View v )
    {
        // Return input text to activity
        bus.post( new UserInputValue( linkEdit.getText().toString() ) );
        this.dismiss();
    }
}
