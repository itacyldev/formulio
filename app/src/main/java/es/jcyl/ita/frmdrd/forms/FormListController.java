package es.jcyl.ita.frmdrd.forms;
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

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.query.Filter;
import es.jcyl.ita.frmdrd.ui.components.EntityAccessor;
import es.jcyl.ita.frmdrd.ui.components.UIComponent;
import es.jcyl.ita.frmdrd.ui.components.form.UIForm;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 * <p>
 * Implements entity edition actions using a edit-form.
 */
public class FormListController extends FormController {
    // form used to refer to the repository used to count entities
    private Repository repo;
    private Filter filter;
    private String mainForm;
    private String mainEntityAccessor;
    private String editRoute;

    public FormListController(String id, String name) {
        super(id, name);
    }

    /****************************/
    /**  >>> Form List methods **/
    /****************************/

    /**
     * Number of entities obtained with the main repository. Used in the form list to give
     * information of the number of "form instances (table records)" registered
     *
     * @return
     */
    public int count() {
        Repository repo = findRepo();
        return 0;
//       this.repo.count()
    }

    private Repository findRepo() {
        // if it set, use this instance
        if (this.repo != null) {
            return this.repo;
        }

        // if not, get it from the mainForm, and the filter too
        UIForm form = view.getForm(this.mainForm);
        if (form != null && form.getRepo() != null) {
            this.repo = form.getRepo();
            this.filter = form.getFilter();
        }
        // still not found, try with the list accessor
        UIComponent component = view.findChild(this.mainEntityAccessor);
        if (component != null) {
            if (component instanceof EntityAccessor) {
                throw new FormException(String.format("Invalid configuration on form [%s] the parameter" +
                                " 'mainEntityAccessor' must reference an UIComponent that implements " +
                                "the EntityAccessor interfaces (Ej: Datatable, DataList, etc). But [%s] found.",
                        this.getId(), this.mainEntityAccessor, component.getClass().getName()));
            }
            EntityAccessor entityAccessor = (EntityAccessor) component;
            if (entityAccessor.getRepo() != null) {
                this.repo = entityAccessor.getRepo();
                this.filter = entityAccessor.getFilter();
            }
        }
        throw new FormException(String.format("No repository found for the form [%s]. User the parameter" +
                "'repoId' or configure the parameters 'mainForm' or 'mainEntityAccessor', and make" +
                "sure the referenced UIComponent has a repository.", this.getId()));
    }

    /****************************/
    /** GETTER/SETTERS **/
    /****************************/

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getMainForm() {
        return mainForm;
    }

    public void setMainForm(String mainForm) {
        this.mainForm = mainForm;
    }

    public String getMainEntityAccessor() {
        return mainEntityAccessor;
    }

    public void setMainEntityAccessor(String mainEntityAccessor) {
        this.mainEntityAccessor = mainEntityAccessor;
    }

    public String getEditRoute() {
        return editRoute;
    }

    public void setEditRoute(String editRoute) {
        this.editRoute = editRoute;
    }
}
