package es.jcyl.ita.formic.forms.config.builders.controllers;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

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
        if (ConfigNodeHelper.hasChildrenByTag(node, "action")) {
            String actionType = node.getAttribute("type");
            if (actionType != null && !actionType.equalsIgnoreCase("composite")) {
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
        } else {
            // attach nested options
            List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
            if (CollectionUtils.isNotEmpty(paramNodes)) {
                UIParam[] params = BuilderHelper.getParams(paramNodes);
                element.setParams(params);
            }
        }
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
