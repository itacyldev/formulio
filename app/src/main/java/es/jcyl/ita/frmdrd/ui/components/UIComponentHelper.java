package es.jcyl.ita.frmdrd.ui.components;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class UIComponentHelper {


    public static UIComponent findChild(UIComponent root, String id) {
        if (root.getId().equalsIgnoreCase(id)) {
            return root;
        } else {
            if (!root.hasChildren()) {
                return null;
            } else {
                for (UIComponent kid : root.getChildren()) {
                    UIComponent found = findChild(kid, id);
                    if (found != null) {
                        return found;
                    }
                }
                return null;
            }
        }
    }

    public static List<UIComponent> findByClass(UIComponent root, Class<?> clazz) {
        List<UIComponent> lst = new ArrayList<>();
        _findByClass(root, clazz, lst);
        return lst;
    }

    private static void _findByClass(UIComponent root, Class<?> clazz, List<UIComponent> output) {

        if (clazz.isInstance(root)) {
            output.add(root);
        } else {
            if (!root.hasChildren()) {
                return;
            } else {
                for (UIComponent kid : root.getChildren()) {
                    _findByClass(kid, clazz, output);
                }
                return;
            }
        }
    }

}
