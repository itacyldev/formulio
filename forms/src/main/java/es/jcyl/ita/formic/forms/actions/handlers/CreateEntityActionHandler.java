package es.jcyl.ita.formic.forms.actions.handlers;
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

import static es.jcyl.ita.formic.forms.config.DevConsole.error;

import org.apache.commons.lang3.StringUtils;
import org.mini2Dx.beanutils.ConvertUtils;
import org.mini2Dx.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.formic.forms.MainController;
import es.jcyl.ita.formic.forms.actions.ActionContext;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.actions.UserActionException;
import es.jcyl.ita.formic.forms.actions.UserActionHelper;
import es.jcyl.ita.formic.forms.router.Router;
import es.jcyl.ita.formic.repo.EditableRepository;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;
import es.jcyl.ita.formic.repo.meta.EntityMeta;
import es.jcyl.ita.formic.repo.meta.PropertyType;

/**
 * Generic action to create an entity using the passed entityType and additional parameters as
 * property values for the new entity.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class CreateEntityActionHandler extends AbstractActionHandler {

    RepositoryFactory repoFactory = RepositoryFactory.getInstance();

    public CreateEntityActionHandler(MainController mc, Router router) {
        super(mc, router);
    }

    @Override
    public void handle(ActionContext actionContext, UserAction action) {
        String repoId = MapUtils.isEmpty(action.getParams()) ? null : (String) action.getParams().get("repo");
        if (StringUtils.isBlank(repoId)) {
            throw new UserActionException(error("The parameter 'repo' is mandatory for the " +
                    "entity creation action."));
        }
        // get repository for the new entity
        Repository repo = repoFactory.getRepo(repoId);
        if (repo == null) {
            throw new UserActionException(error("Invalid repository id: [%s], " +
                    "check the value of 'repo' parameter in the action, make sure there's a " +
                    "repository defined in file data/repos.xml with the same id."));
        }
        if (!(repo instanceof EditableRepository)) {
            throw new UserActionException(error("Can't create and entity using repository %s, " +
                    "only EditableRepositories allow entity creation."));
        }
        // create new Type
        Entity entity = ((EditableRepository) repo).newEntity();
        Map<String, Object> params = new HashMap<>(action.getParams());
        params.remove("repo");
        EntityMeta meta = entity.getMetadata();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            PropertyType property = meta.getPropertyByName(entry.getKey());
            if (property != null) {
                entity.set(property.name, ConvertUtils.convert(entry.getValue(), property.type));
            }
        }
        ((EditableRepository) repo).save(entity);

        // get the new entity Id and store it in the context to use in the navigation
        actionContext.setAttribute("entityId", entity.getId());
    }

    /**
     * Modify the UserAction to add the new Entity id as parameter
     *
     * @param actionContext
     * @param action
     * @return
     */
    @Override
    public UserAction prepareNavigation(ActionContext actionContext, UserAction action) {
        UserAction navAction = UserActionHelper.clone(action);
        // set entityId as parameter
        navAction.addParam("entityId", actionContext.getAttribute("entityId"));
        return navAction;
    }
}
