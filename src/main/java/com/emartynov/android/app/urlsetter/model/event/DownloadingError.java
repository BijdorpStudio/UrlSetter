package com.emartynov.android.app.urlsetter.model.event;

public class DownloadingError
{
    private Exception exception;

    public DownloadingError ( Exception e )
    {
        exception = e;
    }

    public Exception getException ()
    {
        return exception;
    }
}
