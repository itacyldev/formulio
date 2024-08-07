package es.jcyl.ita.formic.app.jobs;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobContextTestUtils {

    /**
     * Creates composite context and fill with minimum data to allow job execution
     *
     * @return
     */
    public static CompositeContext createJobExecContext() {
        // find "jobs" folder in test resources
        File jobsFolder = TestUtils.getResourceAsFile("jobs");
        return createJobExecContext(jobsFolder.getParent());
    }

    /**
     * For instrumentation test, creates a temporary folder as project base and
     * a "jobs" folder inside. Returns project folder
     *
     * @return
     */
    public static File createJobFolderInstrTest() {
        // create temporal folder (cannot use Files.createTempDirectory() in android)
        File projectFolder = TestUtils.createTempDirectory();
        File jobsFolder = new File(projectFolder, "jobs");
        jobsFolder.mkdir();
        return jobsFolder;
    }

    public static CompositeContext createJobExecContext(String projectBaseFolder) {
        CompositeContext ctx = new UnPrefixedCompositeContext();
        Context appContext = new BasicContext("app");

        // Create temporary folder under SO tmp
        File tmpDir = TestUtils.createTempDirectory();
        appContext.put("workingFolder", tmpDir.getAbsolutePath());
        ctx.addContext(appContext);

        // create job context and set the folder of job definition files
        Context projectCtx = new BasicContext("project");
        projectCtx.put("folder", projectBaseFolder);//resources folder
        ctx.addContext(projectCtx);
        return ctx;
    }
}
