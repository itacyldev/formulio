package es.jcyl.ita.formic.jayjobs.jobs.exec;
/*
 * Copyright 2022 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @autor Javier Ramos (javier.ramos@itacyl.es)
 */
public abstract class AbstractJobExecRepo implements Serializable, JobExecRepo {
    protected static final Map<Long, List<String>> messages = new HashMap<>();

    public void addMessage(Long execId, String msg) {
        List<String> msgList;
        if (messages.containsKey(execId)) {
            msgList = messages.get(execId);
        } else {
            msgList = new ArrayList<>();
            messages.put(execId, msgList);
        }
        msgList.add(msg);
    }

    public List<String> getMessages(Long jobExecId) {
        List<String> jobMessages = messages.get(jobExecId);
        return jobMessages;
    }
}
