package es.jcyl.ita.formic.jayjobs.task.utils;
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

import es.jcyl.ita.formic.core.context.CompositeContext;
import es.jcyl.ita.formic.core.context.Context;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ContextAccessor {
    public static final String USER_CTX = "user";
    private static final String APP_CTX = "app";
    private static final String PRJ_CTX = "project";

    public static String userId(CompositeContext ctx) {
        if (ctx == null) {
            throw new RuntimeException("Null context passed!.");
        }
        Context userContext = ctx.getContext(USER_CTX);
        return (userContext == null) ? null : userContext.getString("id");
    }

    public static String projectFolder(CompositeContext ctx) {
        if (ctx == null) {
            throw new RuntimeException("Null context passed!.");
        }
        Context jobContext = ctx.getContext(PRJ_CTX);
        return (jobContext == null) ? null : jobContext.getString("folder");
    }

    public static String jobsFolder(CompositeContext ctx) {
        if (ctx == null) {
            throw new RuntimeException("Null context passed!.");
        }
        Context jobContext = ctx.getContext(PRJ_CTX);
        if (jobContext == null) {
            return null;
        } else if (jobContext.containsKey("jobsFolder")) {
            return jobContext.getString("jobsFolder");
        } else {
            // by default use projectFolder/jobs as base for job definition files
            return String.format("%s/%s", projectFolder(ctx), "jobs");
        }
    }

    /**
     * Returns the temporal folder used by the application to store temporary data.
     *
     * @param ctx
     * @return
     */
    public static String workingFolder(CompositeContext ctx) {
        if (ctx == null) {
            throw new RuntimeException("Null context passed!.");
        }
        Context appContext = ctx.getContext(APP_CTX);
        return (appContext == null) ? null : appContext.getString("workingFolder");
    }
}
