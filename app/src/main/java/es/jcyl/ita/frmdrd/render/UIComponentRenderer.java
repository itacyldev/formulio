package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.ViewGroup;

import es.jcyl.ita.frmdrd.ui.form.UIComponent;

interface UIComponentRenderer {

    void render(Context context, UIComponent component,
                ViewGroup parent);
}
