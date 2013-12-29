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
