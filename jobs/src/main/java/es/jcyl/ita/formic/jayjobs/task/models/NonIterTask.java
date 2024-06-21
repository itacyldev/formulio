package es.jcyl.ita.formic.jayjobs.task.models;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collections;
import java.util.List;

import es.jcyl.ita.formic.jayjobs.task.processor.NonIterProcessor;

@JsonIgnoreProperties({"processor"})
public class NonIterTask extends AbstractTask {

    @JsonIgnore
    private List<NonIterProcessor> processors;

    public NonIterTask() {
    }

    public NonIterTask(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<NonIterProcessor> getProcessors() {
        return this.processors;
    }

    public void setProcessors(List<NonIterProcessor> processors) {
        this.processors = processors;
    }

    public void setProcessor(NonIterProcessor processor) {
        if (processor == null) {
            this.processors = Collections.EMPTY_LIST;
        } else {
            this.processors = Collections.singletonList(processor);
        }
    }

}
