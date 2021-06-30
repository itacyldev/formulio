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
import es.jcyl.ita.formic.forms.components.FilterableComponent;
import es.jcyl.ita.formic.forms.view.render.renderer.WidgetContext;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.repo.EditableRepository;


/**
 * Provides load/persistence operations over the entity related to the referenced WidgetContext
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class BasicWidgetController extends AbstractWidgetController {
    private final WidgetContext widgetContext;

    public BasicWidgetController(Widget widget) {
        this.widgetContext = widget.getWidgetContext();
    }

    public void load(CompositeContext context) {
    }

    public boolean save() {
        return doSave(widgetContext, getEditableRepo(widgetContext));
    }

    public EditableRepository getEditableRepo() {
        return getEditableRepo(widgetContext);
    }

    public void delete() {
        doDelete(widgetContext, getEditableRepo());
    }


    public EditableRepository getEditableRepo(WidgetContext widgetContext) {
        // TODO: hay que mejorar esto cuando se refactorice el acceso a la entidad desde el form
        FilterableComponent component = (FilterableComponent) widgetContext.getHolder().getWidget().getComponent();
        EditableRepository repo = (EditableRepository) component.getRepo();
        return repo;
    }

    @Override
    public void updateEntityFromView() {
        doUpdateEntityFromView(widgetContext);
    }

}