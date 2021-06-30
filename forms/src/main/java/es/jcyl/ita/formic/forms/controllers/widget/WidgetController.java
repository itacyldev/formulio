package es.jcyl.ita.formic.forms.controllers.widget;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.forms.controllers.operations.WidgetValidator;
import es.jcyl.ita.formic.repo.EditableRepository;


/**
 * Provides load/persistence operations over the entity related to the referenced WidgetContext
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public interface WidgetController {

    void load(CompositeContext context);

    /**
     * Persist changes in current entity gathering data from Widgets
     *
     * @return
     */
    boolean save();

    /**
     * Provides access to current widget repository
     *
     * @return
     */
    EditableRepository getEditableRepo();

    /**
     * Delete current entity
     */
    void delete();


    WidgetValidator getValidator();

    void updateEntityFromView();
}

