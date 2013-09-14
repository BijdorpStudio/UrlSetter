package com.emartynov.android.app.urlsetter.model.event;

import android.net.Uri;

public class ResolveURL
{
    private final Uri uri;

    public ResolveURL ( Uri uri )
    {
        this.uri = uri;
    }

    public Uri getUri ()
    {
        return uri;
    }
}
