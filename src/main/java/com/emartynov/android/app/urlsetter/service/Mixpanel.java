package com.emartynov.android.app.urlsetter.service;

import android.content.Context;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Map;
import java.util.Properties;

public class Mixpanel
{
    private final String token;
    private MixpanelAPI logger;

    public Mixpanel ( String apiToken )
    {
        this.token = apiToken;
    }

    public void init ( Context context )
    {
        logger = MixpanelAPI.getInstance( context, token );
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
