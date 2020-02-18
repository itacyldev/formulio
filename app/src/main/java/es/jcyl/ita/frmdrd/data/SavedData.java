package es.jcyl.ita.frmdrd.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SavedData {

    public static Map<String, List<KeyValuePair>> getForm(String idForm) {
        if (data.containsKey(idForm)) {
            return data.get(idForm);
        } else {
            return null;
        }
    }

    public static void saveForm(String idForm, Map<String,
            List<KeyValuePair>> form){
        data.put(idForm, form);
    }

    private static Map<String, Map<String, List<KeyValuePair>>> data =
            new HashMap<>();


}
