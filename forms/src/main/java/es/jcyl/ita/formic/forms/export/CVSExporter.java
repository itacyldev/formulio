package es.jcyl.ita.formic.forms.export;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.opencsv.CSVWriter;

import org.mini2Dx.beanutils.ConvertUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;

import es.jcyl.ita.formic.forms.components.column.UIColumn;
import es.jcyl.ita.formic.forms.config.Config;
import es.jcyl.ita.formic.forms.config.DevConsole;
import es.jcyl.ita.formic.forms.el.JexlUtils;
import es.jcyl.ita.formic.forms.el.ValueExpressionFactory;
import es.jcyl.ita.formic.repo.Entity;
import es.jcyl.ita.formic.repo.Repository;
import es.jcyl.ita.formic.repo.query.Filter;

public class CVSExporter {

    private static CVSExporter _instance;

    private Context context;
    private Repository repo;
    private Filter filter;
    private String fileName;

    ValueExpressionFactory exprFactory = ValueExpressionFactory.getInstance();

    public static CVSExporter getInstance() {
        return _instance;
    }

    private CVSExporter(Context ctx, Repository repo, Filter filter, String fileName) {
        this.repo = repo;
        this.filter = filter;
        this.fileName = fileName;
        this.context = ctx;
    }

    public static CVSExporter init(Context ctx, Repository repo, Filter filter, String fileName) {
        _instance = new CVSExporter(ctx, repo, filter, fileName);
        return _instance;
    }

    public void exportCSV(){
        File exportDir = new File(Config.getInstance().getCurrentProject().getBaseFolder() + "/exports", "");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, this.fileName.concat(".csv"));

        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            ArrayList<Entity> listData = (ArrayList<Entity>) this.repo.find(this.filter);

            //Headers
            String strHeaders[] = listData.get(0).getMetadata().getPropertyNames();
            csvWrite.writeNext(strHeaders);

            //Data
            UIColumn[] columns = getColumns(strHeaders);
            for (int i = 0; i< listData.size(); i++) {
                //strData[i] = listData
                Object[] values = JexlUtils.bulkEval(listData.get(i), columns);
                String strData[] = new String[values.length];
                for (int j = 0; j< values.length; j++) {
                    strData[j] = (String) ConvertUtils.convert(values[j], String.class);
                }
                csvWrite.writeNext(strData);
            }

            csvWrite.close();

            shareFile(exportDir);

        } catch (IOException e) {
            DevConsole.error(e.getMessage(), e);
        }
    }

    private UIColumn[] getColumns(String[] propertyNames) {

        UIColumn[] columns = new UIColumn[propertyNames.length];

        for (int i = 0; i< propertyNames.length; i++) {
            columns[i] = createColumn(propertyNames[i]);
        }
        return columns;
    }

    private UIColumn createColumn(String propertyName) {
        UIColumn col = new UIColumn();
        col.setValueExpression(exprFactory.create("${entity." + propertyName + "}"));
        return col;
    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://"+file.getAbsolutePath()));

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        context.startActivity(Intent.createChooser(intentShareFile, "Share File"));

    }

}
