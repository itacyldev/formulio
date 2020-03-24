package es.jcyl.ita.frmdrd.dummy;

import java.util.HashMap;
import java.util.Map;

import es.jcyl.ita.frmdrd.ui.components.UIForm;


public class DummyContent {

    public static final Map<String, UIForm> FORM_CONFIGS = new HashMap<String, UIForm>();

    public static final Map<String, FormListItem> ITEM_MAP = new HashMap<String, FormListItem>();

    private static final int COUNT = 25;





    /**
     * A dummy item representing a piece of content.
     */
    public static class FormListItem {
        public final String id;
        public final String name;

        public FormListItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
