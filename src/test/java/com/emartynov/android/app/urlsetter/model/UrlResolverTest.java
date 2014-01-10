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

import com.emartynov.android.app.urlsetter.model.event.ResolveUrl;
import com.squareup.otto.Bus;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UrlResolverTest
{
    private UrlResolver target;
    private Bus bus = mock( Bus.class );
    private HttpClient client = mock( HttpClient.class );
    private ThreadPoolExecutor executor = mock( ThreadPoolExecutor.class );

    @Before
    public void setUp () throws Exception
    {
        target = new UrlResolver( bus, client, executor );
    }

    @Test
    public void registerToBus () throws Exception
    {
        verify( bus ).register( target );
    }

    @Test
    public void whenResolveAskedThenPutTaskInExecutor () throws Exception
    {
        ResolveUrl event = new ResolveUrl( mock( Uri.class ) );

        target.resolveURL( event );

        verify( executor ).execute( any( Runnable.class ) );
    }
}
