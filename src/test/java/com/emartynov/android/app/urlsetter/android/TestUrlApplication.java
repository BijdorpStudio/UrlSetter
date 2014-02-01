package com.emartynov.android.app.urlsetter.android;

public class TestUrlApplication
    extends UrlApplication
{
    private Object module = new TestUrlModule();

    @Override
    protected Object getUrlModule()
    {
        return module;
    }

    public TestUrlModule getTestModule()
    {
        return (TestUrlModule) module;
    }
}
