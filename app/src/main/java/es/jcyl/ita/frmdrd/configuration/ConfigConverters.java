package es.jcyl.ita.frmdrd.configuration;
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

import org.mini2Dx.beanutils.ConvertUtils;
import org.mini2Dx.beanutils.converters.DoubleConverter;
import org.mini2Dx.beanutils.converters.FloatConverter;
import org.mini2Dx.beanutils.converters.IntegerConverter;
import org.mini2Dx.beanutils.converters.LongConverter;

import java.util.Date;

import es.jcyl.ita.crtrepo.types.ByteArray;
import es.jcyl.ita.crtrepo.types.Geometry;
import es.jcyl.ita.frmdrd.converters.ByteArrayConverter;
import es.jcyl.ita.frmdrd.converters.CustomBooleanConverter;
import es.jcyl.ita.frmdrd.converters.CustomDateConverter;
import es.jcyl.ita.frmdrd.converters.GeometryConverter;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ConfigConverters {

    public void init() {
        // customize data type converters
        ConvertUtils.register(new CustomDateConverter(), Date.class);
        ConvertUtils.register(new ByteArrayConverter(), ByteArray.class);
        ConvertUtils.register(new GeometryConverter(), Geometry.class);
        String[] trueStrings = {"true", "t", "yes", "y", "on", "1", "s", "si", "sí"};
        String[] falseStrings = {"", "false", "no", "n", "off", "0", "f"};
        CustomBooleanConverter boolConverter = new CustomBooleanConverter(trueStrings, falseStrings, false);
        ConvertUtils.register(boolConverter, Boolean.class);
        // set null as default value for number converters
        ConvertUtils.register(new IntegerConverter(null), Integer.TYPE);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        ConvertUtils.register(new LongConverter(null), Long.TYPE);
        ConvertUtils.register(new LongConverter(null), Long.class);
        ConvertUtils.register(new FloatConverter(null), Double.TYPE);
        ConvertUtils.register(new FloatConverter(null), Double.class);
        ConvertUtils.register(new DoubleConverter(null), Double.TYPE);
        ConvertUtils.register(new DoubleConverter(null), Double.class);

//        ConvertUtils.lookup()
    }
}
