package es.jcyl.ita.formic.forms.config.builders.ui;

import org.mini2Dx.beanutils.BeanUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.components.image.UIImageGalleryItem;
import es.jcyl.ita.formic.forms.config.AttributeResolver;
import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.builders.proxy.ExpressionMethodRef;
import es.jcyl.ita.formic.forms.config.meta.Attribute;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.config.meta.TagDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.forms.el.ValueBindingExpression;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

public class UIIMageGalleryItemBuilder extends BaseUIComponentBuilder<UIImageGalleryItem> {

    private ValueExpressionFactory expressionFactory = ValueExpressionFactory.getInstance();

    public UIIMageGalleryItemBuilder(String tagName) {
        super(tagName, UIImageGalleryItem.class);
    }

    @Override
    protected void setAttributes(UIImageGalleryItem element, ConfigNode node, List<ExpressionMethodRef> expressionAtts) {
        Map<String, Attribute> attributes = TagDef.getDefinition(node.getName());

        if (attributes == null) {
            throw new ConfigurationException(error("Invalid tagName found: " + node.getName()));
        }
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            String attName = entry.getKey();
            Attribute attribute = entry.getValue();
            String attValue = node.getAttribute(attName);
            try {
                if (isExpression(attValue) && !AttributeDef.VALUE.equals(attribute)
                        && attribute.allowsExpression
                        && (element instanceof UIComponent)) // exclude configuration elements
                {
                    // add expressions to map to resolve then through component proxy
                    ValueBindingExpression expression = expressionFactory.create(attValue);
                    ExpressionMethodRef ref = new ExpressionMethodRef("get" + attribute.name.toLowerCase(), expression);
                    expressionAtts.add(ref);
                    continue;
                }
                if (attribute.assignable) {
                    Object value;
                    String setter = (attribute.setter == null) ? attribute.name : attribute.setter;
                    if (!node.hasAttribute(attName)) {
                        // att hasn't been set
                        value = getDefaultAttributeValue(element, node, attName);
                    } else {
                        if (attribute.resolver == null) {
                            // convert value if needed
                            value = (attribute.type == null) ? attValue :
                                    ConvertUtils.convert(attValue, attribute.type);
                        } else {
                            // use resolver to set attribute
                            AttributeResolver resolver = getAttributeResolver(attribute.resolver);
                            value = resolver.resolve(node, attribute.name);
                        }
                    }
                    if (value != null) {
                        BeanUtils.setProperty(element, setter, value);
                    }
                } else {
                    //TODO: create strategies per attribute?
                    // for now let the builder assume this responsibility and reuse with helpers
                    doWithAttribute(element, attName, attValue);
                }
            } catch (Exception e) {
                throw new ConfigurationException(error(String.format("Error while trying to set " +
                        "attribute '%s' on element <%s/>.", attName, node.getName()), e), e);
            }
        }
    }
}
