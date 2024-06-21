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

import android.os.Build;
import android.text.Html;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.R;

;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
@Ignore
public class AboutActivityTest {

    private AboutActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(AboutActivity.class);
    }

    @Test
    public void testFormicVersion() throws Exception {

        TextView formicVersion = activity.findViewById(R.id.about_formicversion);
        Assert.assertEquals(Html.fromHtml(activity.getResources().getString(R.string.formicversion,
                        activity.getVersion())).toString(),
                formicVersion.getText().toString());

        TextView androidVersion = activity.findViewById(R.id.about_androidversion);
        Assert.assertEquals(Html.fromHtml(activity.getResources().getString(R.string.androidversion,
                        Build.VERSION.RELEASE, Build.VERSION.SDK_INT)).toString(),
                androidVersion.getText().toString());

        TextView deviceInfo =  activity.findViewById(R.id.about_deviceinfo);
        Assert.assertEquals(Html.fromHtml(activity.getResources().getString(R.string.deviceinfo,
                Build.MANUFACTURER, Build.MODEL, Build.PRODUCT, Build.ID)).toString(),
                deviceInfo.getText().toString());

    }

    @Test
    public void testStats(){
        TextView usedMemory = activity.findViewById(R.id.about_usedmemory);
        TextView maxHeapSize = activity.findViewById(R.id.about_maxheapsize);
        TextView availHeapSize = activity.findViewById(R.id.about_availheapsize);

        Assert.assertTrue(usedMemory.getText().toString().split(":")[0].contains(activity.getResources().getString(R.string.usedmemory).split(":")[0]));
        Assert.assertTrue(maxHeapSize.getText().toString().split(":")[0].contains(activity.getResources().getString(R.string.maxheapsize).split(":")[0]));
        Assert.assertTrue(availHeapSize.getText().toString().split(":")[0].contains(activity.getResources().getString(R.string.availheapsize).split(":")[0]));

    }


}
