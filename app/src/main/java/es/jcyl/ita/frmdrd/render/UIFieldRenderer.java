package es.jcyl.ita.frmdrd.render;

import android.view.View;

import es.jcyl.ita.frmdrd.ui.form.UIField;

interface UIFieldRenderer {

    View render(UIField field);

    void update(UIField field);
}
