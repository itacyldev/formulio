package es.jcyl.ita.formic.forms.config.resolvers;

import android.graphics.Color;

import es.jcyl.ita.formic.forms.config.ConfigurationException;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;

public class ColorAttributeResolver extends AbstractAttributeResolver<Integer> {
    @Override
    public Integer resolve(ConfigNode node, String attName) {
        String colorStr = node.getAttribute(attName).toLowerCase();
        Integer colorInt = null;
        try {
            colorInt = Color.parseColor(colorStr);
        } catch (Exception ex) {
            throw new ConfigurationException(DevConsole.error(String.format("The color '%s' inside file " +
                    "'${file}' does not exist.",colorStr)));
        }
        return colorInt;
    }
}
