package com.emartynov.android.app.urlsetter.android.ui;

import com.emartynov.android.app.urlsetter.android.TestUrlApplication;
import com.emartynov.android.app.urlsetter.android.TestUrlModule;
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
public class UrlActivityTest
{
    private UrlActivity activity;

    @Before
    public void setUp () throws Exception
    {
        activity = buildActivity( UrlActivity.class ).create().get();
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

    @Test
    public void startsService () throws Exception
    {
        ShadowIntent startedService = shadowOf( shadowOf( activity ).getNextStartedService() );

        assertThat( startedService.getIntentClass().toString() ).isEqualTo( UrlService.class.toString() );
    }
}
