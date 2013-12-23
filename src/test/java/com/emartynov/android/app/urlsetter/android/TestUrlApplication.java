package com.emartynov.android.app.urlsetter.android;

public class TestUrlApplication extends UrlApplication
{
    private static Object module;

    @Override
    protected Object getUrlModule ()
    {
        return module;
    }

    public static void setTestModule ( Object module )
    {
        TestUrlApplication.module = module;
    }
}
