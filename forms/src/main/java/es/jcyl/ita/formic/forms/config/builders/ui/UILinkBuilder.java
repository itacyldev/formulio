package es.jcyl.ita.formic.forms.config.builders.ui;

import org.mini2Dx.collections.CollectionUtils;

import java.util.List;

import es.jcyl.ita.formic.forms.components.link.UILink;
import es.jcyl.ita.formic.forms.components.link.UIParam;
import es.jcyl.ita.formic.forms.config.ConfigNodeHelper;
import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

public class UILinkBuilder extends AbstractComponentBuilder<UILink> {

    public UILinkBuilder() {
        super("link", UILink.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<UILink> node) {
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<UILink> node) {
        //BuilderHelper.setUpValueExpressionType/(node);

        UILink element = node.getElement();
        // attach nested options
        List<ConfigNode> paramNodes = ConfigNodeHelper.getDescendantByTag(node, "param");
        if (CollectionUtils.isNotEmpty(paramNodes)) {
            UIParam[] params = getParams(paramNodes);
            element.setParams(params);
        }
    }

    private UIParam[] getParams(List<ConfigNode> paramNodes) {
        UIParam[] params = new UIParam[paramNodes.size()];
        for (int i = 0; i < paramNodes.size(); i++) {
            UIParam uiParam = new UIParam();
            ConfigNode paramNode = paramNodes.get(i);
            if (paramNode.hasAttribute("name")) {
                uiParam.setName(paramNode.getAttribute("name"));
            }
            if (paramNode.hasAttribute("value")) {
                ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();
                uiParam.setValue(exprFactory.create(paramNodes.get(i).getAttribute("value")));
            }
            params[i] = uiParam;
        }
        return params;
    }

}
