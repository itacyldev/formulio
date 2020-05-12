package es.jcyl.ita.frmdrd.config;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ConfigNodeHelper {


    /**
     * Returns the nodes that hang from root parameter with the given tagName.
     * @param root
     * @param tagName
     * @return
     */
    public static List<ConfigNode> getChildrenByTag(ConfigNode root, String tagName) {
        List<ConfigNode> kids = new ArrayList<ConfigNode>();
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (n.getName().equals(tagName)) {
                    kids.add(n);
                }
            }
        }
        return kids;
    }

    /**
     * Searchs in the nested subtree all the nodes with the given tagName.
     * @param root
     * @param tagName
     * @return
     */
    public static List<ConfigNode> getNestedByTag(ConfigNode root, String tagName) {
        List<ConfigNode> result = new ArrayList<>();
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        findNestedByTag(root, set, result);
        return result;
    }

    /**
     * Searchs in the nested subtree all the nodes that have one of the given tagNames.
     * @param root
     * @param tagNames
     * @return
     */
    public static  List<ConfigNode> getNestedByTag(ConfigNode root, Set<String> tagNames) {
        List<ConfigNode> result = new ArrayList<>();
        findNestedByTag(root, tagNames, result);
        return result;
    }

    /**
     * Recursively goes over the component tree storing fields in the given List
     */
    private static  void findNestedByTag(ConfigNode root, Set<String> tagNames, List<ConfigNode> found) {
        if (tagNames.contains(root.getName())) {
            found.add(root);
        } else {
            if (root.hasChildren()) {
                List<ConfigNode> children = root.getChildren();
                for (ConfigNode n : children) {
                    findNestedByTag(n, tagNames, found);
                }
            }
        }
    }
//    private find

    public static UIComponent[] getUIChildren(ConfigNode root) {
        List<UIComponent> kids = new ArrayList<UIComponent>();
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (n.getElement() instanceof UIComponent)
                    kids.add((UIComponent) n.getElement());
            }
        }
        return kids.toArray(new UIComponent[kids.size()]);
    }
}
