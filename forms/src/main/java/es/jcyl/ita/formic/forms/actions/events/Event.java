package es.jcyl.ita.formic.forms.actions.events;
/*
 *
 *  * Copyright 2020 Javier Ramos (javier.ramos@itacyl.es), ITACyL (http://www.itacyl.es).
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import es.jcyl.ita.formic.core.context.Context;
import es.jcyl.ita.formic.forms.actions.UserAction;
import es.jcyl.ita.formic.forms.components.UIComponent;
import es.jcyl.ita.formic.forms.view.widget.Widget;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class Event {

    public enum EventType {CHANGE, CLICK}

    private final EventType type;
    private Widget source;
    private UserAction handler;
    private Context context;

    public Event(EventType type, Widget source) {
        this(type, source, null);
    }

    /**
     * Represents an user interaction event
     *
     * @param type:         event type
     * @param source:       origin of the event
     * @param eventHandler: specific user action to execute as responser for the event.
     */
    public Event(EventType type, Widget source, UserAction eventHandler) {
        this.type = type;
        this.source = source;
        this.handler = eventHandler;
    }

    public EventType getType() {
        return type;
    }

    public UserAction getHandler() {
        return handler;
    }

    void setHandler(UserAction handler) {
        this.handler = handler;
    }

    public Widget getSource() {
        return source;
    }

    public static Event inputChange(Widget component) {
        return new Event(EventType.CHANGE, component);
    }
    public static Event click(Widget component) {
        return new Event(EventType.CLICK, component);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
