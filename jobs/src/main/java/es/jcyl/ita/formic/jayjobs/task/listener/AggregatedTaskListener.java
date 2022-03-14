package es.jcyl.ita.formic.jayjobs.task.listener;
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

import org.mini2Dx.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import es.jcyl.ita.formic.jayjobs.task.models.Task;

/**
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class AggregatedTaskListener implements TaskExecListener {

    private final List<TaskExecListener> listeners;
    public AggregatedTaskListener() {
        listeners = new ArrayList<>();
    }

    public AggregatedTaskListener(List<TaskExecListener> listeners) {
        this.listeners = listeners;
        if (CollectionUtils.isEmpty(listeners)) {
            throw new IllegalArgumentException("Listener list must not be null!");
        }
    }
    public void addListener(TaskExecListener listener){
        listeners.add(listener);
    }

    @Override
    public void onTaskStart(Task task) {
        for (TaskExecListener listener : listeners) {
            listener.onTaskStart(task);
        }
    }

    @Override
    public void onTaskError(Task task, String message, Throwable t) {
        for (TaskExecListener listener : listeners) {
            listener.onTaskError(task, message, t);
        }
    }

    @Override
    public void onTaskEnd(Task task) {
        for (TaskExecListener listener : listeners) {
            listener.onTaskEnd(task);
        }
    }

    @Override
    public void onProgressUpdate(Task task, int total, float progress, String units) {
        for (TaskExecListener listener : listeners) {
            listener.onProgressUpdate(task, total, progress, units);
        }
    }

    @Override
    public void onMessage(Task task, String message) {
        for (TaskExecListener listener : listeners) {
            listener.onMessage(task, message);
        }
    }
}
