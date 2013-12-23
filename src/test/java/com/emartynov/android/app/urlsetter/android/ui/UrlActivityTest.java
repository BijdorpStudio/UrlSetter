package com.emartynov.android.app.urlsetter.android.ui;

import com.emartynov.android.app.urlsetter.android.RobolectricGradleTestRunner;
import com.emartynov.android.app.urlsetter.android.TestUrlApplication;
import com.emartynov.android.app.urlsetter.inject.UrlModule;
import com.emartynov.android.app.urlsetter.service.Crashlytics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith ( RobolectricGradleTestRunner.class )
public class UrlActivityTest
{
    private Crashlytics crashlytics = mock( Crashlytics.class );
    private UrlActivity activity;

    @Before
    public void setUp () throws Exception
    {
        TestUrlApplication.setTestModule( new TestUrlModule() );

        activity = Robolectric.buildActivity( UrlActivity.class ).create().get();
    }

    @Test
    public void finishesAfterStart () throws Exception
    {
        assertThat( activity.isFinishing() ).isTrue();
    }

    @Module ( overrides = true, includes = UrlModule.class )
    class TestUrlModule
    {
        @Provides
        @Singleton
        public Crashlytics getCrashlytics ()
        {
            return crashlytics;
        }
    }
}
