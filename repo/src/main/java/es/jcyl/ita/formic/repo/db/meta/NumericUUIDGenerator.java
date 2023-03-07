package es.jcyl.ita.formic.repo.db.meta;
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

import java.sql.Timestamp;

import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.db.sqlite.greendao.EntityDao;

/**
 * @author mungarro
 * <p>
 * Generates a new key for the entity returning the current System timestamp and 5 random digits
 */
public class NumericUUIDGenerator extends KeyGeneratorStrategy {
    private static final Class[] SUPPORTED_TYPES = {Long.class};

    public NumericUUIDGenerator() {
        super(TYPE.NUMERICUUID);
    }

    private NumericUUIDGenerator(TYPE type) {
        super(type);
    }

    @Override
    public boolean supports(Class type) {
        return (type == Long.class);
    }

    @Override
    protected <T> T doGetKey(EntityDao dao, Entity entity, Class<T> expectedType) {
        //Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Timestamp timestamp = getDateTimeStamp();
        int randomInt = getRandomInt(10000, 99999);
        Object value = timestamp.getTime()*100000+randomInt;
        T key = (T) ConvertUtils.convert(value, expectedType);

        return key;
    }

    private static Timestamp getDateTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    protected Class[] getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    public static int getRandomInt(int lo, int hi) {
        int ret = 0;
        ret = (int)(Math.random() * (hi - lo + 1));
        ret += lo;
        return ret;
    }

}
