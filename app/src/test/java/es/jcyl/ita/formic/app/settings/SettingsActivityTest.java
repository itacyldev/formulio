package es.jcyl.ita.formic.app.settings;/*
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

import android.widget.EditText;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.R;



/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
@Ignore
public class SettingsActivityTest {

    private SettingsActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.setupActivity(SettingsActivity.class);
    }

    @Test
    public void testPathEditText() throws Exception {

        EditText pathEditText = activity.findViewById(R.id.path_text);
        Assert.assertEquals(activity.getCurrentWorkspace(),
                pathEditText.getText().toString());

        final String path = "/storage/emulated/0/projects2";
        pathEditText.setText(activity.setCurrentWorkspace(path));
        Assert.assertEquals(activity.getCurrentWorkspace(),
                path);

    }

}
