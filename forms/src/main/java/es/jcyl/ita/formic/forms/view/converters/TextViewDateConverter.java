package es.jcyl.ita.formic.forms.view.converters;
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

import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.jcyl.ita.formic.core.context.ContextException;

import static es.jcyl.ita.formic.forms.components.inputfield.UIField.TYPE.DATE;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Class to
 */
public class TextViewDateConverter implements ViewValueConverter<TextView> {

    private String pattern;
    private String type;
    private String datePattern = "yyyy-MM-dd";
    private String datetimePattern = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Object getValueFromView(TextView view) {
        CharSequence value = view.getText();
        Object valueFromView = null;
        if (!StringUtils.isEmpty(value)) {
            // Convertir la cadena a un objeto Date
            SimpleDateFormat dateFormat = new SimpleDateFormat(type.equals(DATE.name()) ? datePattern : datetimePattern);
            Date parsedDate = null;
            try {
                valueFromView = dateFormat.parse(value.toString());
                if (pattern.equals("unixepoch_s")) {
                    valueFromView = ((Date) valueFromView).getTime() / 1000;
                } else if (pattern.equals("unixepoch_m")) {
                    valueFromView = ((Date) valueFromView).getTime();
                } else{
                    valueFromView = dateFormat.format(valueFromView);
                }
            } catch (ParseException e) {
                throw new ContextException(String.format(
                        "There was and error while trying "
                                + "to create the Date formatter with the specified pattern: [%s]",
                        pattern));
            }
        }
        return valueFromView;
      /*  CharSequence value = view.getText();
        return (StringUtils.isEmpty(value)) ? null : value.toString();*/
    }


    @Override
    public void setViewValue(TextView view, Object value) {
        String textValue = (String) ConvertUtils.convert(value, String.class);
        view.setText(textValue);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
