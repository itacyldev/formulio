package es.jcyl.ita.formic;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApachePOITest {
    @Test
    public void saveXLS() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("es.jcyl.ita.formic.test", appContext.getPackageName());

        Workbook wb = new HSSFWorkbook();

        Sheet sheet = wb.createSheet("Sheet 1");

        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("COLUMNA 1 (sheet 1)");

        cell = row.createCell(1);
        cell.setCellValue("COLUMNA 2 (sheet 1)");

        row = sheet.createRow(1);

        cell = row.createCell(0);
        cell.setCellValue("Dato columna 1 (sheet 1)");

        cell = row.createCell(1);
        cell.setCellValue("Dato columna 2 (sheet 1)");

        sheet = wb.createSheet("Sheet 2");

        row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("COLUMNA 1 (sheet 2)");

        cell = row.createCell(1);
        cell.setCellValue("COLUMNA 2 (sheet 2)");

        row = sheet.createRow(1);

        cell = row.createCell(0);
        cell.setCellValue("Dato columna 1 (sheet 2)");

        cell = row.createCell(1);
        cell.setCellValue("Dato columna 2 (sheet 2)");

        File file = new File(appContext.getExternalFilesDir(null), "Ejemplo.xls");
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void readXLS() throws Exception {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        File file = new File(appContext.getExternalFilesDir(null), "Ejemplo.xls");
        FileInputStream fileInputStream = null;

        fileInputStream = new FileInputStream(file);
        Workbook workbook = new HSSFWorkbook(fileInputStream);

        Sheet sheet = workbook.getSheetAt(1);

        Row row = sheet.getRow(1);

        Cell cell = row.getCell(1);
        assertEquals(cell.getStringCellValue(), "Dato columna 2 (sheet 2)");
    }

}

