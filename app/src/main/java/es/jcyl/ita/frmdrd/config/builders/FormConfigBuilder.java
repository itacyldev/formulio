package es.jcyl.ita.frmdrd.config.builders;
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

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.frmdrd.config.Config;
import es.jcyl.ita.frmdrd.config.ConfigNodeHelper;
import es.jcyl.ita.frmdrd.config.ConfigurationException;
import es.jcyl.ita.frmdrd.config.FormConfig;
import es.jcyl.ita.frmdrd.config.reader.ConfigNode;
import es.jcyl.ita.frmdrd.forms.FormEditController;
import es.jcyl.ita.frmdrd.forms.FormListController;

import static es.jcyl.ita.frmdrd.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FormConfigBuilder extends AbstractComponentBuilder<FormConfig> {

    public FormConfigBuilder(String tagName) {
        super(tagName, FormConfig.class);
    }

    @Override
    protected void doWithAttribute(FormConfig element, String name, String value) {
    }


    @Override
    protected void setupOnSubtreeStarts(ConfigNode<FormConfig> node) {
        FormConfig formConfig = node.getElement();
        List<ConfigNode> list = ConfigNodeHelper.getChildrenByTag(node, "list");
        if (list.size() == 0) {
            // if no nested List, create one node and attach to current node
            addDefaultListNode(formConfig, node);
        }
        List<ConfigNode> edits = ConfigNodeHelper.getChildrenByTag(node, "edit");
        if (edits.size() == 0) {
            // if no nested Edit, create one node and attach to current node
            addDefaultEditNode(formConfig, node);
        }
        UIBuilderHelper.addDefaultRepoNode(node);
    }


    @Override
    protected void setupOnSubtreeEnds(ConfigNode<FormConfig> node) {
        // attach list and edit controllers to current formConfig
        setUpListController(node);
        setUpEditControllers(node);
    }

    private void addDefaultListNode(FormConfig formConfig, ConfigNode node) {
        // add list node
        ConfigNode listNode = addDefaultNode(formConfig, node, "list");

//        // find edit views
//        List<ConfigNode> edits = ConfigNodeHelper.getChildrenByTag(node, "edit");
//        String editId;
//        if (edits.size() == 0) {
//            // it will be created later with this id
//            editId = formConfig.getId() + "#list";
//        } else if (edits.size() == 1) {
//            editId = edits.get(0).getId();
//        } else {
//            throw new ConfigurationException(error("List-view with more that one edit-view " +
//                    "and with no actions defined!. When you have more that one <edit/> in a form, " +
//                    "you have to use <actions/> element in the <list/> to define which view will " +
//                    "be navigated from the list."));
//        }
    }

    private void addDefaultEditNode(FormConfig formConfig, ConfigNode node) {
        // add list node
        ConfigNode editNode = addDefaultNode(formConfig, node, "edit");
        // get list node, it will be previously created
//        List<ConfigNode> list = ConfigNodeHelper.getChildrenByTag(node, "list");
//        String fcListId = list.get(0).getId();
//
//        editNode.addChild(createActionNode("save", fcListId + "#save", "Save", fcListId));
//        editNode.addChild(createActionNode("cancel", fcListId + "#cancel", "Cancel", "back"));
    }

    private ConfigNode createActionNode(String action, String id, String label, String route) {
        ConfigNode node = new ConfigNode(action);
        node.setId(id);
        node.setAttribute("label", label);
        node.setAttribute("route", route);
        return node;
    }

    private ConfigNode addDefaultNode(FormConfig formConfig, ConfigNode node, String tag) {
        ConfigNode newNode = node.copy();
        String id = formConfig.getId() + "#" + tag;
        newNode.setName(tag);
        newNode.setId(id);
        node.addChild(newNode);
        return newNode;
    }

    /**
     * Finds nested list Element and sets to FormConfig
     *
     * @param node
     */
    private void setUpListController(ConfigNode<FormConfig> node) {
        List<ConfigNode> list = ConfigNodeHelper.getChildrenByTag(node, "list");
        if (CollectionUtils.isEmpty(list)) {
            throw new ConfigurationException(error("Each form file must contain just " +
                    "one 'list' element"));
        }
        node.getElement().setList((FormListController) list.get(0).getElement());
    }


    private void setUpEditControllers(ConfigNode<FormConfig> node) {
        List<ConfigNode> list = ConfigNodeHelper.getChildrenByTag(node, "edit");
        if (CollectionUtils.isEmpty(list)) {
            throw new ConfigurationException(error("Each form file must contain at least " +
                    "one 'edit' element"));
        }
        List<FormEditController> edits = new ArrayList<>();
        for (ConfigNode editNode : list) {
            edits.add((FormEditController) editNode.getElement());
        }
        node.getElement().setEdits(edits);
    }


}
