package es.jcyl.ita.formic.forms.components.radio;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.widget.RadioButton;

import es.jcyl.ita.formic.forms.R;
import es.jcyl.ita.formic.forms.components.StyleHolder;

public class RadioButtonStyleHolder implements StyleHolder<RadioButton> {

    private Context viewContext;
    private final int style;
    private final ColorStateList colorStateList;
    public RadioButtonStyleHolder(Context context) {
        style = R.style.Default_Button_font;
        viewContext = context;

        TypedArray ta = viewContext.obtainStyledAttributes(new int[]{R.attr.onSurfaceColor});
        colorStateList = ta.getColorStateList(0);

    }

    @Override
    public void applyStyle(RadioButton btn) {
        btn.setTextAppearance(viewContext, style);
        btn.setButtonTintList(colorStateList);
    }
}