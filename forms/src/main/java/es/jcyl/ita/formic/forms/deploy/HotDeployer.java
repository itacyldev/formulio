package es.jcyl.ita.formic.forms.deploy;
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

import android.os.FileObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

import es.jcyl.ita.formic.forms.App;
import es.jcyl.ita.formic.forms.MainController;

/**
 * Detects changes in project configuration files and reload configuration objects. If change affects to current view,
 * asks de MainController to re-render it.
 *
 * @autor Gustavo Río Briones (gustavo.rio@itacyl.es)
 */
public class HotDeployer extends FileObserver {

    private final MainController mc;

    public HotDeployer(String path, MainController mc) {
        super(path);
        this.mc = mc;
    }

    @Override
    public void onEvent(int event, String path) {
        // check whan kind of

        // check if we have to re-render current view
        if(changeRequiresRendering(path)){
            mc.renderBack();
        }


    }

    /**
     * Checks if current file affects to current view so it has to be re-rendered
     * @param path
     * @return
     */
    private boolean changeRequiresRendering(String path) {
        if(path.endsWith("repos.xml") || path.endsWith(".js")){
            return true;
        }
        App.getInstance().getProjectManager().getFormConfigRepo().listAll();
        mc.getViewController().g
    }
}
