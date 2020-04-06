package es.jcyl.ita.frmdrd.configuration;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class ContextToRepoBinding {

    private final Map<String, List<String>> deps = new HashMap<>();

    private static ContextToRepoBinding _instance = null;

    public static ContextToRepoBinding getInstance() {
        if (_instance == null) {
            _instance = new ContextToRepoBinding();
        }
        return _instance;
    }

    /**
     * Returns the list of context variables in which the given repository depends on
     *
     * @param repoId
     * @return
     */
    public List<String> getRepoContextDeps(String repoId) {
        return deps.get(repoId);
    }

    public Map<String, List<String>> getRepoContextDeps() {
        return deps;
    }

    public void setRepoContextDeps(String repoId, List<String> deps) {
        this.deps.put(repoId, deps);
    }

}
