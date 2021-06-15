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
import es.jcyl.ita.formic.forms.components.form.WidgetContextHolder;
import es.jcyl.ita.formic.forms.view.widget.RepoAccessWidget;
import es.jcyl.ita.formic.forms.view.widget.WidgetException;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Repository;

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class GroupWidgetController extends AbstractWidgetController {

    private final WidgetContextHolder[] widgets;
    private final RepoAccessWidget mainWidget;

    public GroupWidgetController(RepoAccessWidget mainWidget, WidgetContextHolder[] widgets) {
        this.mainWidget = mainWidget;
        this.widgets = widgets;
    }

    @Override
    public void load(CompositeContext context) {
    }

    @Override
    public boolean save() {
        EditableRepository repo = getEditableRepo();
        for (WidgetContextHolder widget : widgets) {
            doSave(widget.getWidgetContext(), repo);
        }
        return true;
    }

    @Override
    public EditableRepository getEditableRepo() {
        Repository repo = mainWidget.getRepository();
        if (!(repo instanceof EditableRepository)) {
            throw new WidgetException(error(String.format("The component [%s] uses a repo that " +
                            "doesn't allow modification [%s], configure an EditableRepository instead.",
                    mainWidget.getWidget().getComponent().getAbsoluteId(), repo.getId())));
        }
        return (EditableRepository) repo;
    }

    @Override
    public void delete() {
        EditableRepository repo = getEditableRepo();
        for (WidgetContextHolder widget : widgets) {
            doDelete(widget.getWidgetContext(), repo);
        }
    }

    @Override
    public void updateFromView() {
        for (WidgetContextHolder widget : widgets) {
            doUpdateEntity(widget.getWidgetContext());
        }
    }
}
