package com.emartynov.android.app.urlsetter.model;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import com.emartynov.android.app.urlsetter.model.event.DownloadingError;
import com.emartynov.android.app.urlsetter.model.event.FoundURL;
import com.emartynov.android.app.urlsetter.model.event.ResolveURL;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import android.net.Uri;

public class URLResolver
{
    private final Bus bus;
    private ExecutorService executor = Executors.newFixedThreadPool( 2 );

    @Inject
    public URLResolver ( Bus bus )
    {
        this.bus = bus;

        bus.register( this );
    }

    @Subscribe
    public void resolveURL ( ResolveURL event )
    {
        executor.execute( new ResolveUrlRunnable( event.getUri() ) );
    }

    private class ResolveUrlRunnable implements Runnable
    {
        private final Uri uri;

        public ResolveUrlRunnable ( Uri uri )
        {
            this.uri = uri;
        }

        @Override
        public void run ()
        {
            OkHttpClient httpClient = new OkHttpClient();
            HttpURLConnection connection = null;

            try
            {
                connection = httpClient.open( new URL( uri.toString() ) );
                connection.setRequestMethod( "HEAD" );

                int responseCode = connection.getResponseCode();
                if ( responseCode == HttpURLConnection.HTTP_OK )
                    bus.post( new FoundURL( Uri.parse( connection.getURL().toString() ) ) );
            }
            catch ( Exception e )
            {
                bus.post( new DownloadingError( e ) );
            }
            finally
            {
                if ( connection != null )
                {
                    connection.disconnect();
                }
            }
        }
    }
}
