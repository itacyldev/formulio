package es.jcyl.ita.formic.forms.config.builders.controllers;

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.UIAction;
import es.jcyl.ita.formic.forms.controllers.UIParam;

public class UIActionBuilder extends AbstractComponentBuilder<UIAction> {

    public UIActionBuilder() {
        super("action", UIAction.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UIAction> node) {
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UIAction> node) {
        UIAction element = node.getElement();
        // attach nested options
        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        if (CollectionUtils.isNotEmpty(paramNodes)) {
            UIParam[] params = BuilderHelper.getParams(paramNodes);
            element.setParams(params);
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
}
