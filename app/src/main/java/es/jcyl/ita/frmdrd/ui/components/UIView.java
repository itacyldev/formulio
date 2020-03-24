package es.jcyl.ita.frmdrd.ui.components;
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

import java.io.IOException;

import es.jcyl.ita.frmdrd.interceptors.OnChangeFieldInterceptor;
import es.jcyl.ita.frmdrd.render.Renderer;
import es.jcyl.ita.frmdrd.render.RendererFactory;
import es.jcyl.ita.frmdrd.view.ViewConfigException;

/**
 * @author Gustavo Río (gustavo.rio@itacyl.es)
 */

public class UIView extends UIComponent {


    @Override
    public boolean isRenderChildren() {
        return true;
    }
}
