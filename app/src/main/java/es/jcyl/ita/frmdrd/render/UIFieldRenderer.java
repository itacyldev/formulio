package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.ViewGroup;

import es.jcyl.ita.frmdrd.ui.form.UIField;

interface UIFieldRenderer {

    void render(Context context, UIField field,
                ViewGroup parent);
}
