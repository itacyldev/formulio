package es.jcyl.ita.formic.forms.view.selection;
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

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.repo.Entity;

/**
 * Helper class that intermediates between widgets that let the user select entites and the
 * toolbaars (menu and FABs) that renders user actions depending on the selected entity.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class SelectionManager {

    private List<Entity> clipboard = new ArrayList<Entity>();
    private List<SelectionObserver> observers = new ArrayList<SelectionObserver>();


    public void select(UIComponent component, Entity entity) {
        this.clipboard.add(entity);
        notify(component);
    }

    public void deselect(UIComponent component, Entity entity) {
        this.clipboard.remove(entity);
        notify(component);
    }

    public boolean hasSelection() {
        return this.clipboard.size() > 0;
    }

    private void notify(UIComponent component) {
        for (SelectionObserver obs : this.observers) {
            obs.update(component);
        }
    }

    public void clear() {
        this.clipboard.clear();
    }

    public void addObserver(SelectionObserver observer) {
        this.observers.add(observer);
    }
}
