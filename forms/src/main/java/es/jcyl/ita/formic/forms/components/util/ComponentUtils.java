package es.jcyl.ita.formic.forms.components.util;

import android.view.View;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

import es.jcyl.ita.formic.forms.config.DevConsole;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ComponentUtils {
    private static final String DELIM = ",";
    private static final int NUM_100 = 100;

    public static float[] getWeigths(String weights, int length, String idParent, String idChild) {
        // handle cell weigthts
        float[] weigths = null;
        if (StringUtils.isNotBlank(weights)) {
            weigths = getWeigthValues(weights, length, idParent, idChild);
        }else{
            weigths = getEqualWeigthValues(length);
        }
        return weigths;
    }

    public static float[] getWeigthValues(String weights, int length, String idParent, String idChild) {
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
            if (idChild == null)
            DevConsole.error(String.format("An error occurred while trying ot apply 'weigthts' " +
                            "attribute in parent [%s], child [%s].", idParent,
                    idChild));
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

    public static void setLayoutParamsRadio(float[] weigthts, int i, View view) {
        if (weigthts != null && i < weigthts.length) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams();
            lp.weight = 1; //column weight
            lp = new TableRow.LayoutParams(0, MATCH_PARENT, weigthts[i]);
            view.setLayoutParams(lp);
        }
    }
}
