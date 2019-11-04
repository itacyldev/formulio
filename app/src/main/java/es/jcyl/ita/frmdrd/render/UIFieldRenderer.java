package es.jcyl.ita.frmdrd.render;

import android.content.Context;
import android.view.View;

import es.jcyl.ita.frmdrd.ui.form.UIField;

interface UIFieldRenderer {

    View render(Context context, UIField field);

    void update(UIField field);
}
