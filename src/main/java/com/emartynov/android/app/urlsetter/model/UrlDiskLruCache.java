/*
 * Copyright 2013-2014 @BijdorpStudio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.emartynov.android.app.urlsetter.model;

import android.net.Uri;
import com.emartynov.android.app.urlsetter.service.Crashlytics;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UrlDiskLruCache
{
    private final File directory;

    private final int appVersion;

    private Crashlytics crashlytics;

    private DiskLruCache lruCache;

    public UrlDiskLruCache( File directory, int appVersion, Crashlytics crashlytics )
    {
        this.directory = directory;
        this.appVersion = appVersion;
        this.crashlytics = crashlytics;
    }

    public synchronized Uri get( Uri keyUri )
    {
        return checkIfCacheInitialized() ? getCachedUriFromLruDiskCache( keyUri ) : null;
    }

    private Uri getCachedUriFromLruDiskCache( Uri keyUri )
    {
        try
        {
            String key = getUriKey( keyUri );

            DiskLruCache.Snapshot snapshot = lruCache.get( key );

            return snapshot != null ? Uri.parse( snapshot.getString( 0 ) ) : null;
        }
        catch ( IOException e )
        {
            crashlytics.logException( e );
            return null;
        }
    }

    private boolean checkIfCacheInitialized()
    {
        if ( lruCache == null )
        {
            try
            {
                lruCache = DiskLruCache.open( directory, appVersion, 1, 100 * 1024 );
            }
            catch ( IOException e )
            {
                crashlytics.logException( e );
                return false;
            }
        }

        return true;
    }

    private String getUriKey( Uri uri )
    {
        String key;
        try
        {
            key = getMD5( uri );
        }
        catch ( NoSuchAlgorithmException e )
        {
            key = String.valueOf( Math.abs( uri.hashCode() ) );
        }
        return key.length() > 64 ? key.substring( 0, 64 ) : key;
    }

    private String getMD5( Uri uri )
        throws NoSuchAlgorithmException
    {
        MessageDigest digest = MessageDigest.getInstance( "MD5" );
        digest.update( uri.toString().getBytes() );
        byte messageDigest[] = digest.digest();

        // Create Hex String
        StringBuilder hexString = new StringBuilder();
        for ( byte b : messageDigest )
        {
            String h = Integer.toHexString( 0xFF & b );
            while ( h.length() < 2 )
            {
                h = "0" + h;
            }
            hexString.append( h );
        }

        return hexString.toString();
    }

    public synchronized void save( Uri keyUri, Uri valueUri )
    {
        if ( checkIfCacheInitialized() )
        {
            saveToDiskLruCache( keyUri, valueUri );
        }
    }

    private void saveToDiskLruCache( Uri keyUri, Uri valueUri )
    {
        try
        {
            DiskLruCache.Editor editor = lruCache.edit( getUriKey( keyUri ) );
            editor.set( 0, valueUri.toString() );
            editor.commit();
        }
        catch ( IOException e )
        {
            crashlytics.logException( e );
        }
    }

    public synchronized boolean isLoaded()
    {
        return lruCache != null;
    }
}
