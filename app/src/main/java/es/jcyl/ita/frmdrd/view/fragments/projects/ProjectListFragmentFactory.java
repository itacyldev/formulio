package es.jcyl.ita.frmdrd.view.fragments.projects;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;

import es.jcyl.ita.frmdrd.project.ProjectRepository;

/**
 * @author José Ramón Cuevas (joseramon.cuevas@itacyl.es)
 */
public class ProjectListFragmentFactory extends FragmentFactory {

        private ProjectRepository projectRepository;

        public ProjectListFragmentFactory(final ProjectRepository projectRepository){
            this.projectRepository = projectRepository;
        }

        @NonNull
        @Override
        public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
            Class clazz = loadFragmentClass(classLoader, className);

            Fragment fragment = null;
            if (clazz == ProjectListFragment.class) {
                fragment = ProjectListFragment.newInstance(projectRepository);
            } else {
                return super.instantiate(classLoader, className);
            }
            return fragment;
        }
}
