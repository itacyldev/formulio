package es.jcyl.ita.formic.core.jexl;
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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class RandomHelper {
    public static String string(int size) {
        return RandomStringUtils.randomAlphanumeric(size);
    }

    public static String string() {
        return string(10);
    }

    public static Integer integer() {
        return RandomUtils.nextInt();
    }

    public static Integer integer(int min, int max) {
        return RandomUtils.nextInt(min, max);
    }

    public static Float decimal(float min, float max) {
        return RandomUtils.nextFloat(min, max);
    }

    public static Float decimal() {
        return decimal(0, 1);
    }
}
