package com.emartynov.android.app.urlsetter.android.packagemanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

public class ComponentSwitcher
{
    public static void disableComponent ( Context context, String packageName, String className )
    {
        setComponentState( context, packageName, className, PackageManager.COMPONENT_ENABLED_STATE_DISABLED );
    }

    private static void setComponentState ( Context context, String packageName, String className, int state )
    {
        ComponentName componentName = new ComponentName( packageName, className );
        context.getPackageManager().setComponentEnabledSetting( componentName, state, PackageManager.DONT_KILL_APP );
    }

    public static void enableComponent ( Context context, String packageName, String className )
    {
        setComponentState( context, packageName, className, PackageManager.COMPONENT_ENABLED_STATE_ENABLED );
    }
}
