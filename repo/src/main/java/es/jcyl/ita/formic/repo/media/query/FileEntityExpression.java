package es.jcyl.ita.formic.repo.media.query;
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

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import es.jcyl.ita.formic.repo.query.Expression;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class FileEntityExpression extends Expression implements FilenameFilter {
    private Pattern pattern;

    public FileEntityExpression(String regex){
            pattern = Pattern.compile(regex);
    }

    @Override
    public boolean accept(File file, String name) {
        return pattern.matcher(new File(name).getName()).matches();
    }
}
