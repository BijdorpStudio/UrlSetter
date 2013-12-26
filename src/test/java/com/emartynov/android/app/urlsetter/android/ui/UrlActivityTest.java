package com.emartynov.android.app.urlsetter.android.ui;

import com.emartynov.android.app.urlsetter.android.TestUrlApplication;
import com.emartynov.android.app.urlsetter.android.TestUrlModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith (RobolectricTestRunner.class)
public class UrlActivityTest
{
    private UrlActivity activity;

    @Before
    public void setUp () throws Exception
    {
        activity = Robolectric.buildActivity( UrlActivity.class ).create().get();
    }

    @Test
    public void finishesAfterStart () throws Exception
    {
        assertThat( activity.isFinishing() ).isTrue();
    }

    @Test
    public void startsCrashlytics () throws Exception
    {
        TestUrlModule testModule = ( (TestUrlApplication) activity.getApplication() ).getTestModule();

        verify( testModule.getCrashlytics() ).start( activity );
    }
}
