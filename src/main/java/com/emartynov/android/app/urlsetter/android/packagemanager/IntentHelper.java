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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import com.emartynov.android.app.urlsetter.R;

import java.util.ArrayList;
import java.util.List;

public class IntentHelper
{
    public void launchUri( Context context, Uri uri )
    {
        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

        context.startActivity( intent );
    }

    public boolean isFilterUri( Context context, Uri uri )
    {
        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );

        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infos = packageManager.queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY );

        for ( ResolveInfo info : infos )
        {
            if ( info.activityInfo.packageName.startsWith( context.getPackageName() ) )
            {
                return true;
            }
        }

        return false;
    }

    public void launchUriWithoutUs( Context context, Uri uri )
    {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( uri );

        List<ResolveInfo> possibleIntents =
            packageManager.queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY );
        ArrayList<Intent> intents = new ArrayList<Intent>( possibleIntents.size() );

        if ( possibleIntents.size() > 0 )
        {
            for ( ResolveInfo resolveInfo : possibleIntents )
            {
                if ( !resolveInfo.activityInfo.packageName.startsWith( context.getPackageName() ) )
                {
                    Intent target = new Intent( intent );
                    target.setData( uri );
                    target.setPackage( resolveInfo.activityInfo.packageName );

                    intents.add( target );
                }
            }
            launchChooser( context, intents );
        }
    }


    private void launchChooser( Context context, final ArrayList<Intent> intents )
    {
        Intent firstIntent = intents.remove( 0 );
        Parcelable[] parcelableIntents = intents.toArray( new Parcelable[intents.size()] );
        String dialogCaption = context.getString( R.string.select_application_for, firstIntent.getData() );

        Intent chooserIntent = Intent.createChooser( firstIntent, dialogCaption );
        chooserIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        chooserIntent.putExtra( Intent.EXTRA_INITIAL_INTENTS, parcelableIntents );
        context.startActivity( chooserIntent );
    }
}
