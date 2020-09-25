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

import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
            addDefaultNode(formConfig, node, "list");
        } else {
            // setup form id
            setupChildrenIds(formConfig, "list", list);
        }
        List<ConfigNode> edits = ConfigNodeHelper.getChildrenByTag(node, "edit");
        if (edits.size() == 0) {
            // if no nested Edit, create one node and attach to current node
            addDefaultNode(formConfig, node, "edit");
        }else{
            setupChildrenIds(formConfig, "edit", edits);
        }
        UIBuilderHelper.addDefaultRepoNode(node);
    }

    private ConfigNode addDefaultNode(FormConfig formConfig, ConfigNode node, String tag) {
        ConfigNode newNode = node.copy();
        String id = formConfig.getId() + "-" + tag;
        newNode.setName(tag);
        newNode.setId(id);
        if(node.hasAttribute("repo")){
            newNode.setAttribute("repo", node.getAttribute("repo"));
        }
        node.addChild(newNode);
        return newNode;
    }

    private void setupChildrenIds(FormConfig formConfig, String tag, List<ConfigNode> nodes){
        for(ConfigNode n: nodes){
            String id = formConfig.getId() + "-" + n.getId();
            n.setId(id);
        }
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<FormConfig> node) {
        // attach list and edit controllers to current formConfig
        setUpListController(node);
        setUpEditControllers(node);
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
