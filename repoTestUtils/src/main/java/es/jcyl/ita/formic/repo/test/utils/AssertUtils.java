package es.jcyl.ita.formic.repo.test.utils;
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

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.meta.PropertyType;
import es.jcyl.ita.formic.repo.meta.types.ByteArray;
import es.jcyl.ita.formic.repo.meta.types.Geometry;


/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class AssertUtils {
    public static void assertEquals(Entity entity1, Entity entity2) {
        assertEquals(entity1.getProperties(), entity2.getProperties());
    }

    public static void assertEquals(PropertyType[] props1, PropertyType[] props2) {
        if (props1 == null && props2 == null) {
            return;
        }
        if ((props1 == null && props2 != null) || (props1 != null && props2 == null)) {
            Assert.fail("One of the objects is null");
        }
        int i = 0;
        for (PropertyType p : props1) {
            assertEquals(p, props2[i]);
            i++;
        }
    }


    public static void assertEquals(PropertyType p1, PropertyType p2) {
        Assert.assertEquals("The property names don't match", p1.getName(), p2.getName());
        Assert.assertEquals("The property types don't match", p1.getType(), p2.getType());
        Assert.assertEquals("The property primary Key flag don't match", p1.isPrimaryKey(), p2.isPrimaryKey());
    }

    /**
     * Assert date objects equality excluding milliseconds
     *
     * @param msg
     * @param o1
     * @param o2
     */
    public static void assertEqualsTruncDate(String msg, Date o1, Date o2) {
        Assert.assertEquals(msg, ((Date) o1).getTime() / 1000L, ((Date) o2).getTime() / 1000L);
    }

    public static void assertEquals(Object o1, Object o2) {
        assertEquals("", o1, o2);
    }

    public static void assertAnyOf(Object[] expected, Object actual) {
        boolean found = false;
        for (Object value : expected) {
            if (actual.equals(value)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(String.format("The object [%s]doesn't match with any of " +
                "the possible values: %s.", actual, expected), found);
    }

    public static void assertEquals(String msg, Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            Assert.assertEquals(o1, o2);
        }
        Assert.assertEquals(o1.getClass(), o2.getClass());
        if (o1 instanceof Date) {
            assertEqualsTruncDate(msg, (Date) o1, (Date) o2);
        } else if (o1 instanceof ByteArray) {
            byte[] ba1 = ((ByteArray) o1).getValue();
            byte[] ba2 = ((ByteArray) o2).getValue();
            boolean isEqual = true;
            for (int i = 0; i < ba1.length; i++) {
                isEqual = isEqual && (ba1[i] == ba2[i]);
                if (!isEqual) {
                    Assert.fail(msg + ".ByteArray are not equal: " + o1 + "-" + o2);
                }
            }
        } else if (o1 instanceof Geometry) {
            // TODO: srid and geometry type should be compared too
            Assert.assertEquals(msg, ((Geometry) o1).getValue(), ((Geometry) o2).getValue());
        } else {
            Assert.assertEquals(msg, o1, o2);
        }
    }

    public static void assertEquals(Map<String, Object> props1, Map<String, Object> props2) {
        Object o1, o2;
        for (Map.Entry<String, Object> entry : props1.entrySet()) {
            o1 = entry.getValue();
            o2 = props2.get(entry.getKey());
            String msg = String.format("Values for property [%s] expected to be equal", entry.getKey());
            if (o1 instanceof java.util.Date) {
                // Unix time, compare truncating values
                assertEqualsTruncDate(msg, (Date) o1, (Date) o2);
            } else if (o1 instanceof ByteArray) {
                Assert.assertTrue(Arrays.equals(((ByteArray) o1).getValue(), ((ByteArray) o2).getValue()));
            } else {
                Assert.assertEquals(msg, o1, o2);
            }
        }
    }
}
