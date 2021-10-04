package com.example.app;
/*
 * Copyright 2020 Gustavo Río (gustavo.rio@itacyl.es), ITACyL (http://www.itacyl.es).
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

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.jcyl.ita.formic.R;
import es.jcyl.ita.formic.app.MainActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppStartTest {

    public static final String STRING_TO_BE_TYPED = "Espresso";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes. This is a replacement for {@link androidx.test.rule.ActivityTestRule}.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigationDrawer() {

        onView(withContentDescription(R.string.projects))
                .perform(click());
        onView(withContentDescription(R.string.forms))
                .perform(click());
        onView(withContentDescription(R.string.projects))
                .perform(click());
        onView(withContentDescription(R.string.forms))
                .perform(click());

        // check first element in view
        onView(RecyclerViewMatcher.withRecyclerView(es.jcyl.ita.formic.forms.R.id.form_list).atPosition(0))
                .check(matches(hasDescendant(withText("form1-list"))));

    }
}

