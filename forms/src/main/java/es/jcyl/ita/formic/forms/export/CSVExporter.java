package es.jcyl.ita.formic.forms.export;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;
import es.jcyl.ita.formic.repo.query.FilterRepoUtils;

public class CSVExporter {

    private static CSVExporter _instance;

    private static final int OFFSET = 0;
    private static int PAGESIZE = 200;

    public static CSVExporter getInstance() {
        if (_instance == null) {
            _instance = new CSVExporter();
        }
        return _instance;
    }

    public File export(Repository repo, Filter filter, File exportDir, String fileName, String extension) {
        //File exportDir = new File(Config.getInstance().getCurrentProject().getBaseFolder() + "/exports", "");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, getFileName(fileName, extension));

        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ';', ICSVWriter.DEFAULT_QUOTE_CHARACTER, ICSVWriter.DEFAULT_ESCAPE_CHARACTER, ICSVWriter.DEFAULT_LINE_END);

            Filter f = FilterRepoUtils.clone(repo, filter);

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
                        strData[j] = (obj != null && !strHeaders[j].contains("image")) ? String.valueOf(obj) : "";
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

    private String getFileName(String fileName, String extension){
        //Remove Whitespace From String and add date
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();

        return StringUtils.deleteWhitespace(fileName) + "." +  dateFormat.format(date) + "." + extension;


    }

}
