package com.emartynov.android.app.urlsetter.model.event;

import android.net.Uri;

public class ResolveFacebookURL extends UrlEvent
{
    public ResolveFacebookURL ( Uri uri )
    {
        super( uri );
    }
}
