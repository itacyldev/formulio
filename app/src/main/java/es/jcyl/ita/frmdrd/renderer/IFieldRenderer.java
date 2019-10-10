package es.jcyl.ita.frmdrd.renderer;

import android.content.Context;
import android.view.ViewGroup;

import es.jcyl.ita.frmdrd.ui.form.Field;

interface IFieldRenderer {

    void render(Context context, Field field,
                       ViewGroup parent);
}
