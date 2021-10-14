package es.jcyl.ita.formic.app.about;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.forms.view.activities.BaseActivity;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void doOnCreate() {
        setContentView(R.layout.activity_about);
        setToolbar(getString(R.string.action_about));
        setVersion();
        setButtonUpdates();
        setStats();

    }

    @SuppressLint("StringFormatMatches")
    private void setVersion() {
        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            long lastUpdateTime = pInfo.lastUpdateTime;
            String datePattern = "dd-MMM-yyyy";
            String dateFormat = new SimpleDateFormat(datePattern).format(new Date(lastUpdateTime));

            version += " v" + pInfo.versionName + "_" + pInfo.versionCode + " ("+dateFormat+")";
        } catch (Exception e) {
            version += " vXXX";
        }

        TextView formicVersion = (TextView) findViewById(R.id.about_formicversion);
        TextView androidVersion = (TextView) findViewById(R.id.about_androidversion);
        TextView deviceInfo = (TextView) findViewById(R.id.about_deviceinfo);

        formicVersion.setText(Html.fromHtml(
                getResources().getString(R.string.formicversion,
                        version)));
        androidVersion.setText(Html.fromHtml(
                getResources().getString(R.string.androidversion,
                        Build.VERSION.RELEASE, Build.VERSION.SDK_INT)));
        deviceInfo.setText(Html.fromHtml(
                getResources().getString(R.string.deviceinfo,
                        Build.MANUFACTURER, Build.MODEL, Build.PRODUCT, Build.ID)));
    }

    private void setButtonUpdates(){
        Button updates = (Button) findViewById(R.id.about_updates);
        updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final String appPackageName = AboutActivity.this.getPackageName();
                try {
                    AboutActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    AboutActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    @SuppressLint("StringFormatMatches")
    private void setStats() {
        TextView usedMemory = (TextView) findViewById(R.id.about_usedmemory);
        TextView maxHeapSize = (TextView) findViewById(R.id.about_maxheapsize);
        TextView availHeapSize = (TextView) findViewById(R.id.about_availheapsize);

        final Runtime runtime = Runtime.getRuntime();
        final long usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        final long maxHeapSizeInMB = runtime.maxMemory() / 1048576L;
        final long availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB;

        usedMemory.setText(Html.fromHtml(
                getResources().getString(R.string.usedmemory,
                        usedMemInMB)));
        maxHeapSize.setText(Html.fromHtml(
                getResources().getString(R.string.maxheapsize,
                        maxHeapSizeInMB)));
        availHeapSize.setText(Html.fromHtml(
                getResources().getString(R.string.availheapsize,
                        availHeapSizeInMB)));
    }

}
