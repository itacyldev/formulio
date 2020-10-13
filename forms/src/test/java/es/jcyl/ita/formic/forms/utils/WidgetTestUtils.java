package es.jcyl.ita.formic.forms.utils;
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

import android.graphics.Bitmap;

import org.junit.Assert;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 *
 * Got from https://android.googlesource.com/platform/cts/+/c94170d4ad510feca70f768548ab76ea9dd1a606/tests/src/android/widget/cts/WidgetTestUtils.java
 */
public class WidgetTestUtils {
    /**
     * Assert that two bitmaps are equal.
     *
     * @param b1 the first bitmap which needs to compare.
     * @param b2 the second bitmap which needs to compare.
     */
    public static void assertEquals(Bitmap b1, Bitmap b2) {
        if (b1 == b2) {
            return;
        }
        if (b1 == null || b2 == null) {
            Assert.fail("the bitmaps are not equal");
        }
        // b1 and b2 are all not null.
        if (b1.getWidth() != b2.getWidth() || b1.getHeight() != b2.getHeight()) {
            Assert.fail("the bitmaps are not equal");
        }
        int w = b1.getWidth();
        int h = b1.getHeight();
        int s = w * h;
        int[] pixels1 = new int[s];
        int[] pixels2 = new int[s];
        b1.getPixels(pixels1, 0, w, 0, 0, w, h);
        b2.getPixels(pixels2, 0, w, 0, 0, w, h);
        for (int i = 0; i < s; i++) {
            if (pixels1[i] != pixels2[i]) {
                Assert.fail("the bitmaps are not equal");
            }
        }
    }
}
