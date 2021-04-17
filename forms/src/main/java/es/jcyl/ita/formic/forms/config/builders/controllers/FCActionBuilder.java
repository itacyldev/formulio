package es.jcyl.ita.formic.forms.config.builders.controllers;

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.components.link.UIParam;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.controllers.FCAction;

public class FCActionBuilder extends AbstractComponentBuilder<FCAction> {

    public FCActionBuilder() {
        super("action", FCAction.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<FCAction> node) {

    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<FCAction> node) {
        FCAction element = node.getElement();
        // attach nested options
        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        if (CollectionUtils.isNotEmpty(paramNodes)) {
            UIParam[] params = getParams(paramNodes);
            element.setParams(params);
        }
    }

}
