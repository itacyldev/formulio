package es.jcyl.ita.formic.repo.source;
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

import es.jcyl.ita.formic.repo.RepositoryException;
import es.jcyl.ita.formic.repo.builders.AbstractEntitySourceBuilder;
import es.jcyl.ita.formic.repo.builders.EntitySourceBuilder;
import es.jcyl.ita.formic.repo.db.source.DBTableEntitySource;
import es.jcyl.ita.formic.repo.db.source.NativeSQLEntitySource;
import es.jcyl.ita.formic.repo.media.source.FileEntitySource;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class EntitySourceFactory {
    public enum SOURCE_TYPE {SQLITE, SQLITE_CURSOR, CSV, MEDIA}

    private Map<SOURCE_TYPE, Class> entitySourceBuildersMap;
    private Map<String, EntitySource> entitySources;
    private Map<String, Source> sources;

    private static EntitySourceFactory _instance;

    public static EntitySourceFactory getInstance() {
        if (_instance == null) {
            _instance = new EntitySourceFactory();
        }
        return _instance;
    }

    public EntitySourceFactory() {
        entitySourceBuildersMap = new HashMap<SOURCE_TYPE, Class>();
        entitySourceBuildersMap.put(SOURCE_TYPE.SQLITE, DBTableEntitySource.class);
        entitySourceBuildersMap.put(SOURCE_TYPE.SQLITE_CURSOR, NativeSQLEntitySource.class);
        entitySourceBuildersMap.put(SOURCE_TYPE.MEDIA, FileEntitySource.class);
        entitySources = new HashMap<String, EntitySource>();
        sources = new HashMap<String, Source>();
    }

    /******************************************/
    /** Registering/Access to Sources */
    /******************************************/
    public void registerSource(Source source) {
        this.sources.put(source.getId(), source);
    }

    public Source getSource(String sourceId) {
        return this.sources.get(sourceId);
    }

    /******************************************/
    /** Registering/Access to EntitySources */
    /******************************************/
    public void register(EntitySource source) {
        entitySources.put(source.getEntityTypeId(), source);
    }

    public EntitySource getEntitySource(String entityTypeId) {
        return entitySources.get(entityTypeId);
    }

    /******************************************/
    /** Access to EntitySource builders      */
    /******************************************/
    public EntitySourceBuilder getBuilder(SOURCE_TYPE sourceType) {
        Class sourceClass = entitySourceBuildersMap.get(sourceType);
        if (sourceClass == null) {
            throw new RepositoryException("No source builder defined for type: " + sourceType);
        }
        // get inner Builder
        Class builderClass = sourceClass.getDeclaredClasses()[0];
        try {
            Constructor constructor = builderClass.getDeclaredConstructor(sourceClass, EntitySourceFactory.class);
            AbstractEntitySourceBuilder builder = (AbstractEntitySourceBuilder) constructor.newInstance(sourceClass.newInstance(), this);
            return builder;
        } catch (NoSuchMethodException e) {
            throw new RepositoryException(String.format("No constructor found for the builder class [%s], " +
                    "make sure the builder has a constructor that receives the EntitySourceFactory " +
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
        entitySources.clear();
        sources.clear();
    }

    public Set<String> getSourceIds() {
        return sources.keySet();
    }

    public Set<String> getEntitySourcesIds() {
        return entitySources.keySet();
    }

}
