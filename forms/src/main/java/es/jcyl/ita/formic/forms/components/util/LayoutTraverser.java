package es.jcyl.ita.formic.forms.components.util;
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

import android.view.View;
import android.view.ViewGroup;

import java.util.function.Function;

/**
 * https://stackoverflow.com/questions/4872238/enumerate-iterate-all-views-in-activity
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class LayoutTraverser {

    private final Function<View, Void> function;

    public LayoutTraverser(Function<View, Void> function) {
        this.function = function;
    }

    public static LayoutTraverser build(Function function) {
        return new LayoutTraverser(function);
    }

    public View traverse(ViewGroup root) {
        function.apply(root);

        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            final View child = root.getChildAt(i);

            if (child instanceof ViewGroup) {
                traverse((ViewGroup) child);
            }
        }
        return null;
    }
}
