package es.jcyl.ita.formic.forms.components;
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

import es.jcyl.ita.formic.forms.components.form.UIForm;
import es.jcyl.ita.formic.forms.repo.EntityRelation;

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

    public static <T> List<T> findByClass(UIComponent root, Class<T> clazz) {
        List<T> lst = new ArrayList<>();
        _findByClass(root, clazz, lst);
        return lst;
    }


    private static <T> void _findByClass(UIComponent root, Class<T> clazz, List<T> output) {
        if (clazz.isInstance(root)) {
            output.add((T) root);
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

    public static <T> T findFirstByClass(UIComponent root, Class<T> clazz) {
        if (clazz.isInstance(root)) {
            return (T) root;
        } else {
            if (!root.hasChildren()) {
                return null;
            } else {
                for (UIComponent kid : root.getChildren()) {
                    T found = findFirstByClass(kid, clazz);
                    if (found != null) {
                        return found;
                    }
                }
                return null;
            }
        }
    }

    /**
     * Iterates over nested elements looking for components with entityRelation definitions.
     *
     * @param element
     * @return
     */
    public static List<EntityRelation> findEntityRelations(UIForm element) {
        List<EntityRelation> rels = new ArrayList<EntityRelation>();
        _findEntityRelations(element, rels);
        return rels;
    }

    private static void _findEntityRelations(UIComponent root, List<EntityRelation> output) {
        if (root.getEntityRelation() != null) {
            output.add(root.getEntityRelation());
        }
        if (!root.hasChildren()) {
            return;
        } else {
            for (UIComponent kid : root.getChildren()) {
                _findEntityRelations(kid, output);
            }
            return;
        }
    }
}
