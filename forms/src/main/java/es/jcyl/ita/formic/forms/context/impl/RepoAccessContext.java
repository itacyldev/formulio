package es.jcyl.ita.formic.forms.context.impl;
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


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.RepositoryFactory;

/**
 * Simple bean to access repositories from context
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class RepoAccessContext implements Map<String, Repository> {
    private RepositoryFactory repoFactory = RepositoryFactory.getInstance();


    public Collection<Repository> getRepos() {
        return values();
    }

    public Set<String> getRepoIds() {
        return keySet();
    }

    /*************************/
    /** Map interface implementation */
    /************************/
    @Override
    public int size() {
        Set<String> repoIds = repoFactory.getRepoIds();
        return (repoIds == null) ? 0 : repoIds.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }


    @Override
    public boolean containsKey(Object key) {
        return repoFactory.getRepo(String.valueOf(key)) == null;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not implemented!");
    }


    @Override
    public Repository get(Object key) {
        return repoFactory.getRepo(String.valueOf(key));
    }


    @Override
    public Repository put(String key, Repository value) {
        throw new UnsupportedOperationException("Not implemented!");
    }


    @Override
    public Repository remove(Object key) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void putAll(@NonNull Map<? extends String, ? extends Repository> m) {

    }


    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not implemented!");
    }


    @Override
    public Set<String> keySet() {
        return repoFactory.getRepoIds();
    }


    @Override
    public Collection<Repository> values() {
        Collection<Repository> lst = new ArrayList<Repository>();
        for (String repoId : keySet()) {
            lst.add(repoFactory.getRepo(repoId));
        }
        return lst;
    }


    @Override
    public Set<Entry<String, Repository>> entrySet() {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
