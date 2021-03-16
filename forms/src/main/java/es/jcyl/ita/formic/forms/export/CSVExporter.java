package es.jcyl.ita.formic.forms.export;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.repo.query.FilterHelper;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;

public class CSVExporter {

    private static CSVExporter _instance;

    private static final int OFFSET = 0;
    private static int PAGESIZE = 50;

    public static CSVExporter getInstance() {
        if (_instance == null) {
            _instance = new CSVExporter();
        }
        return _instance;
    }

    public static File exportCSV(Repository repo, Filter filter, String fileName) {
        File exportDir = new File(Config.getInstance().getCurrentProject().getBaseFolder() + "/exports", "");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, fileName.concat(".csv"));

        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Filter f = FilterHelper.clone(repo, filter);

            long total = repo.count(f);
            f.setOffset(OFFSET);
            f.setPageSize(PAGESIZE);

            while (f.getOffset() < total){

                ArrayList<Entity> listData = (ArrayList<Entity>) repo.find(f);
                String strHeaders[] = listData.get(0).getMetadata().getPropertyNames();

                if (f.getOffset() == 0 ) {
                    //Header
                    csvWrite.writeNext(strHeaders);
                }

                //Data
                for (int i = 0; i < listData.size(); i++) {
                    String strData[] = new String[strHeaders.length];
                    Entity entity = listData.get(i);

                    for (int j = 0; j < strHeaders.length;  j++) {
                        Object obj = entity.get(strHeaders[j]);
                        strData[j] = obj != null ? String.valueOf(obj) : "";
                    }
                    csvWrite.writeNext(strData);
                }

                f.setOffset(f.getOffset() + f.getPageSize());
            }

            csvWrite.close();

        } catch (IOException e) {
            DevConsole.error(e.getMessage(), e);
        }
        return file;
    }

}
