package es.jcyl.ita.formic.forms.config.builders.controllers;

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.config.builders.BuilderHelper;
import es.jcyl.ita.formic.forms.controllers.UIParam;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.UIAction;

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

}
