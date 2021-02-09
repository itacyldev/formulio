package es.jcyl.ita.formic.forms.components.table;

import android.view.View;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.StringTokenizer;

import es.jcyl.ita.formic.forms.config.DevConsole;

public class TableUtils {
    private static final String DELIM = ",";
    private static final int NUM_100 = 100;

    public static float[] getWeigths(String weights, int length, String idTable, String idRow) {
        // handle cell weigthts
        float[] weigthts = null;
        if (StringUtils.isNotBlank(weights)) {
            weigthts = getWeigthValues(weights, length, idTable, idRow);
        }else{
            weigthts = getEqualWeigthValues(length);
        }
        return weigthts;
    }

    public static Integer[] getColspanValues(UIRow component) {
        Integer[] colspans = null;
        try {

            String[] splits = component.getColspans().split(",");
            colspans = new Integer[splits.length];
            int i = 0;
            for (String cs : splits) {
                colspans[i] = Integer.parseInt(cs);
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

    public static float[] getWeigthValues(String weights, int length, String idTable, String idRow) {
        float[] weigthts = null;
        try {
            StringTokenizer stringTokenizer = new StringTokenizer(weights, DELIM);
            int i = 0;
            float total = 0;
            weigthts = new float[length];
            while (stringTokenizer.hasMoreElements() && i<length) {
                String token = stringTokenizer.nextToken();
                float weight = Float.parseFloat(token);
                weigthts[i] = weight;
                total = total + weight;
                i++;
            }

            if (total < 100 && length > i){
                for (int index = i; index< length; index++){
                    weigthts[index] = (NUM_100 - total)/(length - i);
                }
            }

            i = 0;
            for (float w : weigthts) {
                weigthts[i] = w/(total>100?total:100);
                i++;
            }
        } catch (Exception e) {
            DevConsole.error(String.format("An error occurred while trying ot apply 'weigthts' " +
                            "attribute in table [%s], row [%s].", idTable,
                    idRow));
            // ignore error and continue with the rendering without colspans
            return getEqualWeigthValues(length);
        }
        return weigthts;
    }

    public static float[] getEqualWeigthValues(int length) {
        int i=0;
        float[] weigthts = new float[length];
        for (float w : weigthts) {
            weigthts[i] = 1 / (float) length;
            i++;
        }
        return weigthts;
    }

    @NotNull
    public static void setLayoutParams(float[] weigthts, int i, View view) {
        if (weigthts != null && i < weigthts.length) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams();
            lp.weight = 1; //column weight
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, weigthts[i]);
            view.setLayoutParams(lp);
        }
    }
}
