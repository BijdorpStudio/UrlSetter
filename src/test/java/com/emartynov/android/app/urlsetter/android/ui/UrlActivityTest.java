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

package com.emartynov.android.app.urlsetter.android.ui;

import android.content.Intent;

import com.emartynov.android.app.urlsetter.UrlTestBase;
import com.emartynov.android.app.urlsetter.android.UrlService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.robolectric.Robolectric.buildActivity;
import static org.robolectric.Robolectric.shadowOf;

@RunWith (RobolectricTestRunner.class)
public class UrlActivityTest extends UrlTestBase
{
    private UrlActivity activity;

    @Before
    public void setUp () throws Exception
    {
        activity = buildActivity( UrlActivity.class ).withIntent( new Intent() ).create().get();
    }

    @Test
    public void finishesAfterStart () throws Exception
    {
        assertThat( activity.isFinishing() ).isTrue();
    }

    @Test
    public void startsCrashlytics () throws Exception
    {
        verify( getCrashlytics() ).start( activity );
    }

    @Test
    public void startsService () throws Exception
    {
        ShadowIntent startedService = shadowOf( shadowOf( activity ).getNextStartedService() );

        assertThat( startedService.getIntentClass().toString() ).isEqualTo( UrlService.class.toString() );
    }
}
