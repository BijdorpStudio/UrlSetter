package com.emartynov.android.app.urlsetter.android.ui;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.robolectric.Robolectric.buildActivity;

@Config( emulateSdk = 18, reportSdk = 18 )
@RunWith( RobolectricTestRunner.class )
public class MainActivityTest
{
    private MainActivity activity;

    @Before
    public void setUp()
        throws Exception
    {
        activity = buildActivity( MainActivity.class ).create().get();
    }

    @Test
    @Ignore
    public void finishesAfterStart()
        throws Exception
    {
        assertThat( activity.isFinishing() ).isFalse();
    }
}