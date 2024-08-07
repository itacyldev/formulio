package es.jcyl.ita.formic.forms.components.view;
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

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import org.mini2Dx.collections.MapUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.forms.controllers.widget.WidgetController;
import es.jcyl.ita.formic.forms.view.widget.ControllableWidget;
import es.jcyl.ita.formic.forms.view.widget.Widget;
import es.jcyl.ita.formic.forms.view.widget.WidgetContextHolder;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */
public class ViewWidget extends Widget<UIView> implements WidgetContextHolder {

    private Map<String, WidgetContextHolder> contextHolders;
    private Map<String, ControllableWidget> widgetControllers = new HashMap<>();

    public ViewWidget(Context context) {
        super(context);
    }

    public ViewWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void registerContextHolder(WidgetContextHolder widget) {
        if (this.contextHolders == null) {
            this.contextHolders = new HashMap<>();
        }
        this.contextHolders.put(widget.getHolderId(), widget);
    }

    public Collection<WidgetContextHolder> getContextHolders() {
        return contextHolders.values();
    }
    public Map<String, WidgetContextHolder> getContextHoldersMap() {
        return MapUtils.unmodifiableMap(contextHolders);
    }


    @Override
    public String getHolderId() {
        return this.getComponentId();
    }

    @Override
    public Widget getWidget() {
        return this;
    }

    public WidgetController getWidgetController(String widgetId) {
        ControllableWidget controllableWidget = this.widgetControllers.get(widgetId);
        return (controllableWidget == null) ? null : controllableWidget.getController();
    }

    public Collection<ControllableWidget> getControllableWidgets() {
        return this.widgetControllers.values();
    }

    public void registerControllableWidget(ControllableWidget widget) {
        String id = widget.getWidget().getComponent().getId();
        this.widgetControllers.put(id, widget);
    }
}
