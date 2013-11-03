package com.emartynov.android.app.urlsetter.model.event;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class UrlEvent
{
    private final Uri uri;

    UrlEvent ( Uri uri )
    {
        this.uri = uri;
    }

    public Map<String, String> getLoggingParams ()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put( "Host", uri.getHost() );
        return params;
    }

    public Uri getUri ()
    {
        return uri;
    }
}
