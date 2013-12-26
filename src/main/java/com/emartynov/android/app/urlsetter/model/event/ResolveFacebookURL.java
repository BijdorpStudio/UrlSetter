package com.emartynov.android.app.urlsetter.model.event;

import android.net.Uri;

public class ResolveFacebookUrl extends UrlEvent
{
    public ResolveFacebookUrl ( Uri uri )
    {
        super( uri );
    }
}
