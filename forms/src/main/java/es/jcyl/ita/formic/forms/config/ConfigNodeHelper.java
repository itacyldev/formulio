package es.jcyl.ita.formic.forms.config;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.validation.Validator;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ConfigNodeHelper {


    /**
     * Returns the nodes that hang from root parameter with the given tagName.
     *
     * @param root
     * @param tagName
     * @return
     */
    public static List<ConfigNode> getChildrenByTag(ConfigNode root, String tagName) {
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        return getChildrenByTag(root, set);
    }

    public static List<ConfigNode> getChildrenByTag(ConfigNode root, Set<String> tagNames) {
        List<ConfigNode> kids = new ArrayList<ConfigNode>();
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (tagNames.contains(n.getName())) {
                    kids.add(n);
                }
            }
        }
        return kids;
    }


    public static boolean hasChildrenByTag(ConfigNode node, String tagName) {
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        return hasChildrenByTag(node, set);
    }

    public static boolean hasChildrenByTag(ConfigNode root, Set<String> tagNames) {
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (tagNames.contains(n.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ConfigNode getFirstChildrenByTag(ConfigNode root, String tagName) {
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        return getFirstChildrenByTag(root, set);

    }

    public static ConfigNode getFirstChildrenByTag(ConfigNode root, Set<String> tagNames) {
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (tagNames.contains(n.getName())) {
                    return n;
                }
            }
        }
        return null;
    }

    /**
     * Walks the tree upwards to return the tree root.
     *
     * @param node
     * @return
     */
    public static ConfigNode getRoot(ConfigNode node) {
        ConfigNode parent = node.getParent();
        if (parent == null) {
            return node;
        }
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        return parent;
    }

    /**
     * Walks the tree upwards looking for a ned with the given tag.
     *
     * @param node
     * @param tagName
     * @return
     */
    public static ConfigNode getAscendantByTag(ConfigNode node, String tagName) {
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        return getAscendantByTag(node, set);
    }

    public static boolean hasDescendantByTag(ConfigNode node, String tagName) {
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        return hasNestedByTag(node, set);
    }

    public static boolean hasDescendantByTag(ConfigNode node, Set<String> tagNames) {
        return hasNestedByTag(node, tagNames);
    }


    /**
     * Walks the tree upwards looking for a ned with the given set of tags.
     *
     * @param node
     * @param tagNames
     * @return
     */
    public static ConfigNode getAscendantByTag(ConfigNode node, Set<String> tagNames) {
        ConfigNode parent = node.getParent();
        if (parent == null) {
            return null;
        } else {
            while (parent != null) {
                if (tagNames.contains(parent.getName())) {
                    return parent;
                }
                parent = parent.getParent();
            }
            return null;
        }
    }

    /**
     * Returns the first ascendant with a value in the the given attribute name
     *
     * @param node
     * @param attName
     * @return
     */
    public static ConfigNode findAscendantWithAttribute(ConfigNode node, String attName) {
        ConfigNode parent = node.getParent();
        if (parent == null) {
            return null;
        } else {
            while (parent != null) {
                if (parent.hasAttribute(attName)) {
                    return parent;
                }
                parent = parent.getParent();
            }
            return null;
        }
    }

    /**
     * Searchs in the nested subtree all the nodes with the given tagName.
     *
     * @param root
     * @param tagName
     * @return
     */
    public static List<ConfigNode> getDescendantByTag(ConfigNode root, String tagName) {
        List<ConfigNode> result = new ArrayList<>();
        Set<String> set = new HashSet<>(Arrays.asList(tagName));
        findNestedByTag(root, set, result);
        return result;
    }

    /**
     * Searchs in the nested subtree all the nodes that have one of the given tagNames.
     *
     * @param root
     * @param tagNames
     * @return
     */
    public static List<ConfigNode> getDescendantByTag(ConfigNode root, Set<String> tagNames) {
        List<ConfigNode> result = new ArrayList<>();
        findNestedByTag(root, tagNames, result);
        return result;
    }

    /**
     * Recursively goes over the component tree storing fields in the given List
     */
    private static void findNestedByTag(ConfigNode root, Set<String> tagNames, List<ConfigNode> found) {
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

    /**
     * Recursively goes over the component tree storing fields in the given List
     */
    private static boolean hasNestedByTag(ConfigNode root, Set<String> tagNames) {
        if (tagNames.contains(root.getName())) {
            return true;
        } else {
            if (root.hasChildren()) {
                List<ConfigNode> children = root.getChildren();
                boolean found;
                for (ConfigNode n : children) {
                    found = hasNestedByTag(n, tagNames);
                    if (found) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

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

    public static Validator[] getValidators(ConfigNode root) {
        List<Validator> kids = new ArrayList<Validator>();
        List<ConfigNode> children = root.getChildren();
        if (children != null) {
            for (ConfigNode n : children) {
                if (n.getElement() instanceof Validator)
                    kids.add((Validator) n.getElement());
            }
        }
        return kids.toArray(new Validator[kids.size()]);
    }

    public static String findAscendantAtt(ConfigNode node, String attName) {
        ConfigNode current = node.getParent();
        while (current != null) {
            if (current.hasAttribute(attName)) {
                return current.getAttribute(attName);
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * Returns the first node in the list that has the expected value in given attribute.
     * Returns null if no node has the expected value.
     *
     * @param nodes
     * @param attName
     * @param expectedValue
     * @return
     */
    public static ConfigNode findNodeByAttValue(List<ConfigNode> nodes, String attName, String expectedValue) {
        for(ConfigNode n: nodes){
            if(n.getAttribute(attName)!=null && n.getAttribute(attName).equalsIgnoreCase(expectedValue)){
                return n;
            }
        }
        return null;
    }

    /**
     * Sets a value in node if the attribute is currently not set
     *
     * @param node
     * @param attName
     * @param value
     */
    public static void setIfNull(ConfigNode node, String attName, String value) {
        if (!node.hasAttribute("attName")) {
            node.setAttribute(attName, value);
        }
    }

    public static void removeFromParent(ConfigNode parent, ConfigNode... children) {
        List lst = parent.getChildren();
        if (lst == null) {
            return;
        }
        for (ConfigNode kid : children) {
            Iterator<ConfigNode> iter = lst.iterator();
            while (iter.hasNext()) {
                if (iter.next().equals(kid)) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Given a list of nodes, creates an Array that holds the linked elements of the nodes.
     *
     * @param nodes
     * @param <T>
     * @return
     */
    public static <T> T[] nodeElementsToArray(Class<T> clazz, List<ConfigNode> nodes) {
        List<T> items = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            ConfigNode node = nodes.get(i);
            try {
                Object element = node.getElement();
                if (element != null) {
                    items.add((T) element);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("Wrong type found when trying to " +
                                "copy elements from node [%s], expected [%s] but found [%s]", node.getId(),
                        clazz.getName(), node.getElement().getClass().getName()), e);
            }
        }
        return items.toArray((T[]) Array.newInstance(clazz, items.size()));
    }


}
