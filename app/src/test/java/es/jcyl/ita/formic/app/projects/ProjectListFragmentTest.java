package es.jcyl.ita.formic.app.projects;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.project.Project;
import es.jcyl.ita.formic.forms.project.ProjectRepository;


/**
 * @author Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
@RunWith(RobolectricTestRunner.class)
@Ignore
public class ProjectListFragmentTest {

    private static String NAME_PROJECT_1 = "project1";
    private static String DESCRIPTION_PROJECT_1 = "Description of project1";
    private static String NAME_PROJECT_2 = "project2";
    private static String DESCRIPTION_PROJECT_2 = "Description of project2";
    private static List<Project> projectList;

    @BeforeClass
    public static void initialization() {
        Project mock_project1 = Mockito.mock(Project.class);
        Mockito.when(mock_project1.getName()).thenReturn(NAME_PROJECT_1);
        Mockito.when(mock_project1.getDescription()).thenReturn(DESCRIPTION_PROJECT_1);
        Project mock_project2 = Mockito.mock(Project.class);
        Mockito.when(mock_project2.getName()).thenReturn(NAME_PROJECT_2);
        Mockito.when(mock_project2.getDescription()).thenReturn(DESCRIPTION_PROJECT_2);
        projectList = new ArrayList<>();
        projectList.add(mock_project1);
        projectList.add(mock_project2);
    }

    @Test
    @Ignore
    public void noneProject() {
        ArrayList<Project> projectArrayList = new ArrayList<>();

        ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
        Mockito.when(projectRepository.listAll()).thenReturn(projectArrayList);

        ProjectListFragmentFactory fragmentFactory =
                new ProjectListFragmentFactory(projectRepository);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectListFragment.PROJECT_LIST
                , new ArrayList<>(projectRepository.listAll()));

        FragmentScenario<ProjectListFragment> scenario =
                FragmentScenario.launchInContainer(ProjectListFragment.class, bundle
                        , R.style.FormudruidLight_NoActionBar, fragmentFactory);

        onView(withRecyclerView(R.id.rvProjects).atPosition(0)).check(doesNotExist());
    }

    @Test
    @Ignore
    public void oneProject() {
        ArrayList<Project> projectArrayList = new ArrayList<>();
        projectArrayList.add(projectList.get(0));

        ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
        Mockito.when(projectRepository.listAll()).thenReturn(projectArrayList);

        ProjectListFragmentFactory fragmentFactory =
                new ProjectListFragmentFactory(projectRepository);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectListFragment.PROJECT_LIST
                , new ArrayList<>(projectRepository.listAll()));

        FragmentScenario<ProjectListFragment> scenario =
                FragmentScenario.launchInContainer(ProjectListFragment.class, bundle
                        , R.style.FormudruidLight_NoActionBar, fragmentFactory);

        onView(withRecyclerView(R.id.rvProjects).atPosition(0))
                .check(matches(hasDescendant(withText(NAME_PROJECT_1))))
                .check(matches(hasDescendant(withText(DESCRIPTION_PROJECT_1))));

        onView(withRecyclerView(R.id.rvProjects).atPosition(1)).check(doesNotExist());
    }

    @Test
    public void twoProject() {
        ArrayList<Project> projectArrayList = new ArrayList<>();
        projectArrayList.add(projectList.get(0));
        projectArrayList.add(projectList.get(1));

        ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
        Mockito.when(projectRepository.listAll()).thenReturn(projectArrayList);

        ProjectListFragmentFactory fragmentFactory =
                new ProjectListFragmentFactory(projectRepository);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectListFragment.PROJECT_LIST
                , new ArrayList<>(projectRepository.listAll()));

        FragmentScenario<ProjectListFragment> scenario =
                FragmentScenario.launchInContainer(ProjectListFragment.class, bundle
                        , R.style.FormudruidLight_NoActionBar, fragmentFactory);

        onView(withRecyclerView(R.id.rvProjects).atPosition(0))
                .check(matches(hasDescendant(withText(NAME_PROJECT_1))))
                .check(matches(hasDescendant(withText(DESCRIPTION_PROJECT_1))));

        onView(withRecyclerView(R.id.rvProjects).atPosition(1))
                .check(matches(hasDescendant(withText(NAME_PROJECT_2))))
                .check(matches(hasDescendant(withText(DESCRIPTION_PROJECT_2))));

        onView(withRecyclerView(R.id.rvProjects).atPosition(2)).check(doesNotExist());
    }

    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
