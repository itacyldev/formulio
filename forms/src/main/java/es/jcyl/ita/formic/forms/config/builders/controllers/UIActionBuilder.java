package es.jcyl.ita.formic.forms.config.builders.controllers;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIActionGroup;
import es.jcyl.ita.formic.forms.controllers.UIParam;

public class UIActionBuilder extends AbstractComponentBuilder<UIAction> {

    public UIActionBuilder() {
        super("action", UIAction.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIAction> node) {
        // Do nothing
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIAction> node) {
        UIAction element = node.getElement();
        String actionType = node.getAttribute("type");
        if (ConfigNodeHelper.hasChildrenByTag(node, "action")) {
            processCompositeAction(node, actionType);
        } else { // simple action
            processSimpleAction(node, element, actionType);
        }
    }

    private void processSimpleAction(ConfigNode<UIAction> node, UIAction element, String actionType) {
        if (StringUtils.isEmpty(actionType)) {
            // Normalize action tag. Set tag alias (save, cancel, ..) to tagName and current name as action type
            actionType = node.getName();
            node.getElement().setType(actionType);
            node.setAttribute("type", actionType);
            node.setName("action");
        }

        // attach nested options
        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        // if current action is a js action and "method" attribute is defined, convert it into a "param" node
        if (actionType.equalsIgnoreCase("js")) {
            configureJsAction(node, paramNodes);
        }
        if (CollectionUtils.isNotEmpty(paramNodes)) {
            UIParam[] params = BuilderHelper.getParams(paramNodes);
            element.setParams(params);
        }
    }

    private void processCompositeAction(ConfigNode<UIAction> node, String actionType) {
        // composite action
        if (actionType != null && !actionType.equals("composite")) {
            throw new ConfigurationException(error(String.format("The action [%s] has nested " +
                    "actions but its type is [%s]. If you nest actions inside another " +
                    "action, the top-level action can only be of type 'composite'. " +
                    "Check file ${file}.", node.getId(), actionType)));
        }
        // the action has nested action elements, this is a compositeAction.
        // Attach nested actions and
        List<ConfigNode> actionNodes = ConfigNodeHelper.getChildrenByTag(node, "action");
        UIAction[] nestedActions = ConfigNodeHelper.nodeElementsToArray(UIAction.class, actionNodes);
        UIActionGroup group = (UIActionGroup) node.getElement();
        group.setType("composite");
        group.setActions(nestedActions);
    }

    /**
     * Configures js action
     *
     * @param node
     * @param paramNodes
     */
    private void configureJsAction(ConfigNode<UIAction> node, List<ConfigNode> paramNodes) {
        String method = node.getAttribute("method");
        if (StringUtils.isEmpty(method)) {
            throw new ConfigurationException(error(String.format("Attribute 'method' is mandatory in js actions, " +
                    "check action [%s] if file ${file}.", node.getId())));
        }
        // add method as a parameter node
        ConfigNode methodNode = new ConfigNode("param");
        methodNode.setAttribute("name", "method");
        methodNode.setAttribute("value", method);
        paramNodes.add(methodNode);
    }

    @Override
    protected Object getDefaultAttributeValue(UIAction element, ConfigNode node, String attName) {
        if (attName.toLowerCase().equals("popHistory")) {
            // by default save and cancel actions remove previous navigation from nav history
            String name = node.getName();
            if (needsHistoryPop(name) || needsHistoryPop(attName)) {
                return 1;
            }
        }
        return super.getDefaultAttributeValue(element, node, attName);
    }

    private boolean needsHistoryPop(String value) {
        value = value.toLowerCase();
        return value.equals("save") || value.equals("delete") || value.equals("cancel");
    }

    @Override
    protected UIAction instantiate(ConfigNode<UIAction> node) {
        if (!ConfigNodeHelper.hasChildrenByTag(node, "action")) {
            return super.instantiate(node);
        } else {
            return new UIActionGroup();
        }
    }
}
