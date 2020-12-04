package es.jcyl.ita.formic.forms.components.inputfield;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.widget.Button;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.StyleHolder;

public class ButtonStyleHolder implements StyleHolder<Button> {

    private Context viewContext;
    private final ColorStateList colorStateList;
    public ButtonStyleHolder(Context context) {
        viewContext = context;

        TypedArray ta = viewContext.obtainStyledAttributes(new int[]{R.attr.surfaceColor});
        colorStateList = ta.getColorStateList(0);

    }

    @Override
    public void applyStyle(Button btn) {
        btn.setBackgroundTintList(colorStateList);
    }
}