package com.emartynov.android.app.urlsetter.service.mixpanel;

import android.content.Context;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import org.json.JSONObject;

import java.util.Map;

public class MixLogger
{
    private static final String TOKEN = "5c3c45630dcfc6e626870f0b11055643";

    private MixpanelAPI logger;

    public void init ( Context context )
    {
        logger = MixpanelAPI.getInstance( context, TOKEN );
    }

    public void flush ()
    {
        logger.flush();
    }

    public void trackEvent ( String name, Map<String, String> parameters )
    {
        JSONObject properties = new JSONObject( parameters );
        logger.track( name, properties );
    }

    public void trackEvent ( String name )
    {
        logger.track( name, null );
    }
}
