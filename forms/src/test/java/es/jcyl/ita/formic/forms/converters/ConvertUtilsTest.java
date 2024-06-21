package es.jcyl.ita.formic.forms.converters;
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mini2Dx.beanutils.ConvertUtils;
import org.mini2Dx.beanutils.converters.LongConverter;

import java.util.Calendar;

import es.jcyl.ita.formic.forms.config.ConfigConverters;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Tests to check commons-converters functionallity
 */
//@RunWith(RobolectricTestRunner.class)
public class ConvertUtilsTest {

    @BeforeClass
    public static void setUp() {
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
    }

    @Test
    public void testLongDefaultValue() {

        ConvertUtils.register(new LongConverter(null), Long.TYPE);
        ConvertUtils.register(new LongConverter(null), Long.class);

        // get converter and try to convert and empty value
        Object value = ConvertUtils.convert("", Long.class);
        Assert.assertNull(value);
    }

    @Test
    public void testStringToDate() {


        CustomDateConverter cdc = new CustomDateConverter();
        cdc.setPatterns(new String[]{"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"});
        ConvertUtils.register(cdc, Calendar.class);

        Object value = ConvertUtils.convert("2022-10-03", Calendar.class);
        Assert.assertNotNull(value);
    }


}
