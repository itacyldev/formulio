package es.jcyl.ita.formic.forms.config.builders.repo;
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

import org.apache.commons.lang3.StringUtils;

import es.jcyl.ita.formic.forms.config.builders.AbstractComponentBuilder;
import es.jcyl.ita.formic.forms.config.elements.RepoConfig;
import es.jcyl.ita.formic.forms.config.meta.AttributeDef;
import es.jcyl.ita.formic.forms.config.reader.ConfigNode;
import es.jcyl.ita.formic.repo.builders.RepositoryBuilder;
import es.jcyl.ita.formic.repo.memo.MemoRepository;
import es.jcyl.ita.formic.repo.memo.source.MemoSource;

/**
 * Builder to create FileRepository definition from xml.
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class MemoRepoConfigBuilder extends AbstractComponentBuilder<RepoConfig> {

    public MemoRepoConfigBuilder(String tagName) {
        super(tagName, RepoConfig.class);
    }

    @Override
    protected void setupOnSubtreeStarts(ConfigNode<RepoConfig> node) {
        RepositoryBuilder builder = this.getFactory().getRepoFactory().getBuilder(new MemoSource(node.getId()));
        MemoRepository repo = (MemoRepository) builder.build();
        String propsAtt = node.getAttribute(AttributeDef.PROPERTIES.name.toLowerCase());
        if (StringUtils.isNotBlank(propsAtt)) {
            String[] propsNames = propsAtt.split(",");
            repo.setPropertyNames(propsNames);
        }
    }

    @Override
    protected void setupOnSubtreeEnds(ConfigNode<RepoConfig> node) {
    }
}
