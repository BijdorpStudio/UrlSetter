package com.emartynov.android.app.urlsetter.model.event;

import android.net.Uri;

public class FoundURL
{
    private final Uri uri;

    public FoundURL ( Uri uri )
    {
        this.uri = uri;
    }

    public Uri getUri ()
    {
        return uri;
    }
}
