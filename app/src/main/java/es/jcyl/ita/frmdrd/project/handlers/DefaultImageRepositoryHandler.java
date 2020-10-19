package es.jcyl.ita.frmdrd.project.handlers;
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

import java.io.File;

import es.jcyl.ita.crtrepo.Repository;
import es.jcyl.ita.crtrepo.RepositoryFactory;
import es.jcyl.ita.crtrepo.media.FileRepository;
import es.jcyl.ita.frmdrd.project.Project;
import es.jcyl.ita.frmdrd.project.ProjectResource;

/**
 * Handler to create a default image repository for current project
 *
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class DefaultImageRepositoryHandler extends AbstractProjectResourceHandler
        implements ProjectResourceHandler {
    private static final String DEFAULT_PROJECT_IMAGES = "DEFAULT_PROJECT_IMAGES";
    protected RepositoryFactory repoFactory = RepositoryFactory.getInstance();


    @Override
    public Object handle(ProjectResource resource) {
        Project project = resource.project;
        Repository repo = new FileRepository(new File(project.getPicturesFolder()));
        repoFactory.register(DEFAULT_PROJECT_IMAGES, repo);
        return null;
    }
}