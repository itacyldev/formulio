package es.jcyl.ita.formic.forms.project.handlers;

import es.jcyl.ita.formic.forms.config.reader.ReadingProcessListener;

/*
 * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

/**
 * @author Javier Ramos (javier.ramos@itacyl.es)
 */

public abstract class AbstractProjectResourceHandler<T> implements ProjectResourceHandler<T> {

    protected ReadingProcessListener listener;

    public void setListener(ReadingProcessListener listener) {
        this.listener = listener;
    }

}
