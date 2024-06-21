package es.jcyl.ita.formic.core.format;

/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Date;

public class ValueFormatterFactoryTest {

    @Test
    public void testCreateValueFormatter_Date_UnixEpochFormat_ReturnsUnixEpochDateValueFormatter() {
        // Arrange
        ValueFormatterFactory factory = ValueFormatterFactory.getInstance();
        Class<?> type = Date.class;
        String format = "seconds";

        // Act
        ValueFormatter formatter = factory.getFormatter(type, format);

        // Assert
        assertNotNull(formatter);
        assertTrue(formatter instanceof UnixEpochDateValueFormatter);
    }

    @Test
    public void testCreateValueFormatter_Date_DefaultFormat_ReturnsDateValueFormatter() {
        // Arrange
        ValueFormatterFactory factory = ValueFormatterFactory.getInstance();
        Class<?> type = Date.class;
        String format = "default";

        // Act
        ValueFormatter formatter = factory.getFormatter(type, format, null);

        // Assert
        assertNotNull(formatter);
        assertTrue(formatter instanceof DateValueFormatter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateValueFormatter_NonDateType_ReturnsDefaultFormatter() {
        // Arrange
        ValueFormatterFactory factory = ValueFormatterFactory.getInstance();
        Class<?> type = String.class;
        String format = "default";

        // Act and fail
        factory.getFormatter(type, format, null);
    }
}
