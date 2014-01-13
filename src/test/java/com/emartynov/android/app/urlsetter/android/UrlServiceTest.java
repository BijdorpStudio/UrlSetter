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

package com.emartynov.android.app.urlsetter.android;

import com.emartynov.android.app.urlsetter.UrlTestBase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.mockito.Mockito.verify;

@RunWith ( RobolectricTestRunner.class )
public class UrlServiceTest extends UrlTestBase
{
    private UrlService service = new UrlService();

    @Before
    public void setUp () throws Exception
    {
        service.onCreate();
    }

    @Test
    public void crashlyticsStartsAfterCreate () throws Exception
    {
        verify( getCrashlytics() ).start( service );
    }
}
