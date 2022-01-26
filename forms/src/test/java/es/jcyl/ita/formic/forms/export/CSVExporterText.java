package es.jcyl.ita.formic.forms.export;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.ConfigConverters;
import es.jcyl.ita.formic.forms.utils.RepositoryUtils;

@RunWith(RobolectricTestRunner.class)
public class CSVExporterText {

    @BeforeClass
    public static void setUp() {
        Config.init("");
        ConfigConverters confConverter = new ConfigConverters();
        confConverter.init();
        // register repos
        RepositoryUtils.registerMock("contacts");
    }

    private static final String XML_TEST = "<datatable repo=\"defColsRepo\" properties=\"col1,col3\">" +
            "<column id=\"mycol\"/>" +
            "</datatable>";

    @Test
    public void testExportCSV() {

        /*String xml = XmlConfigUtils.createMainList(XML_TEST);

        // set properties col1 y col2
        EntityMetaDataBuilder metaBuilder = new EntityMetaDataBuilder();
        EntityMeta meta = metaBuilder.withNumProps(1)
                .addProperties(new String[]{"col1", "col2", "col3"},
                        new Class[]{String.class, String.class, String.class})
                .build();

        RepositoryUtils.registerMock("defColsRepo", meta);

        FormConfig formConfig = XmlConfigUtils.readFormConfig(xml);
        List<UIDatatable> datatables = UIComponentHelper.findByClass(formConfig.getList().getView(), UIDatatable.class);
        UIDatatable datatable = datatables.get(0);

        File file = CSVExporter.exportCSV(datatable.getRepo(), datatable.getFilter(), new File("./"), "prueba");*/





    }


}
