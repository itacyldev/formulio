package es.jcyl.ita.formic.forms.components.table;

import android.view.View;
import android.widget.TableRow;

import es.jcyl.ita.formic.forms.config.DevConsole;

public class TableUtils {
    private static final String DELIM = ",";
    private static final int NUM_100 = 100;

    public static Integer[] getColspanValues(UIRow component) {
        Integer[] colspans = null;
        try {

            String[] splits = component.getColspans().split(DELIM);
            colspans = new Integer[splits.length];
            int i = 0;
            for (String cs : splits) {
                colspans[i] = Integer.parseInt(cs.trim());
                i++;
            }
        } catch (Exception e) {
            DevConsole.error(String.format("An error occurred while trying ot apply 'colspans' " +
                            "attribute in table [%s], row [%s].", component.getParent().getId(),
                    component.getId()));
            // ignore error and continue with the rendering without colspans
            return null;
        }
        return colspans;
    }

    public static void setLayoutParams(float[] weigthts, int i, View view) {
        if (weigthts != null && i < weigthts.length) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams();
            lp.weight = 1; //column weight
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, weigthts[i]);
            view.setLayoutParams(lp);
        }
    }


}
