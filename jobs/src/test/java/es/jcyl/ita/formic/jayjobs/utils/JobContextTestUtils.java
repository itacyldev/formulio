package es.jcyl.ita.formic.jayjobs.utils;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.core.context.impl.BasicContext;
import es.jcyl.ita.formic.core.context.impl.UnPrefixedCompositeContext;
import es.jcyl.ita.formic.repo.test.utils.TestUtils;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class JobContextTestUtils {

    /**
     * Creates composite context and fill with minimum data to allow job execution
     * @return
     */
    public static CompositeContext createJobExecContext() {
        // find "jobs" folder in test resources
        File jobsFolder = TestUtils.findFile("jobs");
        return createJobExecContext(jobsFolder.getParent());
    }

    public static CompositeContext createJobExecContext(String projectBaseFolder) {
        CompositeContext ctx = new UnPrefixedCompositeContext();
        Context appContext = new BasicContext("app");
        // Create temporary folder under SO tmp
        try {
            Path tmpDir = Files.createTempDirectory("tmpDir");
            appContext.put("tmpFolder", tmpDir.toAbsolutePath());
            ctx.addContext(appContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // create job context and set the folder of job definition files
        Context projectCtx = new BasicContext("job");
        projectCtx.put("projectFolder", projectBaseFolder);//resources folder
        ctx.addContext(projectCtx);
        return ctx;
    }
}
