package es.jcyl.ita.formic.repo;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.ContextAwareRepo;
import es.jcyl.ita.formic.repo.builders.AbstractRepositoryBuilder;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.db.builders.RawSQLiteRepoBuilder;
import es.jcyl.ita.formic.repo.db.builders.SQLiteGreenDAORepoBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.media.builders.FileRepositoryBuilder;
import es.jcyl.ita.formic.repo.media.source.FileEntitySource;
import es.jcyl.ita.formic.repo.source.EntitySource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class RepositoryFactory {

    private final Map<String, Repository> _instances = new HashMap<String, Repository>();
    private final Map<Class<? extends EntitySource>, Class<? extends RepositoryBuilder>> defaultRepoBuildersMap;
    private static RepositoryFactory _instance;
    private Context context;

    public static RepositoryFactory getInstance() {
        if (_instance == null) {
            _instance = new RepositoryFactory();
        }
        return _instance;
    }

    private RepositoryFactory() {
        // default mapping based on entityType
        defaultRepoBuildersMap = new HashMap<Class<? extends EntitySource>, Class<? extends RepositoryBuilder>>();
        readConfig();
    }

    @Deprecated
    /**
     * ("Replace with Dagger dependency injection or use a ContextHolder to access context.")
     */
    public void setContext(Context ctx) {
        this.context = ctx;
    }

    private void readConfig() {
        // TODO: this info must be provided through xml files
        defaultRepoBuildersMap.put(DBTableEntitySource.class, SQLiteGreenDAORepoBuilder.class);
        defaultRepoBuildersMap.put(NativeSQLEntitySource.class, RawSQLiteRepoBuilder.class);
        defaultRepoBuildersMap.put(FileEntitySource.class, FileRepositoryBuilder.class);
    }

    public Repository getRepo(String entityType) {
        return _instances.get(entityType);
    }

    public EditableRepository getEditableRepo(String entityType) {
        Repository repo = getRepo(entityType);
        if (!(repo instanceof EditableRepository)) {
            throw new RepositoryException(String.format("The Repository instance related to " +
                    "entityType [%s] is not editable: [%s].", entityType, repo.getClass().getName()));
        }
        return (EditableRepository) repo;
    }

    /**
     * Registers repository instance related to given entityType
     *
     * @param entityType
     * @param repo
     */
    public void register(String entityType, Repository repo) {
        this._instances.put(entityType, repo);
        if (repo instanceof ContextAwareRepo) {
            ((ContextAwareRepo) repo).setContext(this.context);
        }
    }

    public void unregister(String entityType) {
        this._instances.remove(entityType);
    }

    public RepositoryBuilder getBuilder(EntitySource source) {
        Class builderClass = defaultRepoBuildersMap.get(source.getClass());
        if (builderClass == null) {
            throw new RepositoryException("No repository defined for the EntitySource class: " + source.getClass().getName());
        }
        try {
            Constructor constructor = builderClass.getConstructor(RepositoryFactory.class);
            AbstractRepositoryBuilder builder = (AbstractRepositoryBuilder) constructor.newInstance(this);
            builder.setEntitySource(source);
            return builder;
        } catch (NoSuchMethodException e) {
            throw new RepositoryException(String.format("No constructor found for the builder class [%s], " +
                    "make sure the builder has a constructor that receives the RepositoryFactory " +
                    "as parameter", builderClass.getName()), e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RepositoryException(String.format("An error occurred while trying to instantiate the " +
                    "builder. Check the constructor for the class [%s].", builderClass.getName()), e);
        } catch (Throwable t) {
            throw new RepositoryException(String.format("Unexpected error while trying to instantiate a " +
                    "builder from class [%s].", builderClass.getName()), t);
        }
    }

    public void clear() {
        _instances.clear();
    }

    public Set<String> getRepoIds() {
        return _instances.keySet();
    }

}
