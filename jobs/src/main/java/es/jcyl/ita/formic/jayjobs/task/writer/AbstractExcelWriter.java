package es.jcyl.ita.formic.jayjobs.task.writer;/*
 * Copyright 2020 Gustavo Río (mungarro@itacyl.es), ITACyL (http://www.itacyl.es).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.jcyl.ita.formic.jayjobs.task.exception.TaskException;
import es.jcyl.ita.formic.jayjobs.task.models.RecordPage;
import es.jcyl.ita.formic.jayjobs.task.utils.ExcelHelper;
import es.jcyl.ita.formic.jayjobs.task.utils.TaskResourceAccessor;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public abstract class AbstractExcelWriter extends AbstractWriter{
    private static final Log LOGGER = LogFactory.getLog(AbstractExcelWriter.class);

    protected static final String CELDA_FORMATO_FECHA = "dd/mm/yyyy hh:mm";
    public static final String OLD_EXTENSION = ".xls";
    public static final String DEFAULT_EXTENSION = ".xlsx";

    protected CellStyle estiloNumeroFecha = null;

    /**
     * Parámetros directamente configurables
     */
    private int sheetIndex = 0;
    private String cellStart;
    private boolean writeHeader = true;
    private int autoResize = 1;
    private boolean evaluateFormulas = true;
    private String outputFile;
    private String templateFile;
    private Map<String, String> columnDataFormat;
    /**
     * Nombre de la hoja en la que se van a copiar los datos recibidos por el
     * Reader. Si la hoja no existe, se crea "vacía".
     */
    private String sheetName;
    /**
     * Si el parámetro sheetTemplateIdx está fijado, en lugar de crear una hoja
     * vacía, copiar la hoja plantilla con el nombre indicado.
     */
    private Integer sheetTemplateIdx;

    /**
     * Variables de estado interno
     */
    private boolean isFirstRow = true;
    private int filaInicio = 0;
    private int columnaInicio = 0;
    private int numColumns = 0;
    private Workbook streamLibro = null;
    private Sheet hoja = null;
    private File fileTemplate = null;
    private int count = 0;

    @Override
    public void open()throws TaskException {
        configureTemplateFile();
        configureOutputFile();
        configureStartingPoint();
    }

    @Override
    public void write(RecordPage page) throws TaskException {
        List<Map<String, Object>> results = page.getResults();
        generaExcel(results, fileTemplate);
    }

    /**
     * Configura el nombre definitivo del fichero de plantilla
     *
     */
    private void configureTemplateFile() throws TaskException{
        String templateAbsolutePath = null;
        if (templateFile != null) {
            File fileTemplateAux = new File(templateFile);
            if (fileTemplateAux.isAbsolute()) {
                templateAbsolutePath = templateFile;
                fileTemplate = new File(templateAbsolutePath);
                if (!fileTemplate.exists()) {
                    throw new TaskException(String.format(String.format(
                            "The file does not exist [%s]. ",
                            templateAbsolutePath)));
                }
            } else {
                String projectFilePath = TaskResourceAccessor.getProjectFile(getGlobalContext(), templateFile);
                fileTemplate = new File(projectFilePath);
                if (!fileTemplate.exists()){
                    String workingFilePath =  TaskResourceAccessor.getWorkingFile(getGlobalContext(), templateFile);
                    fileTemplate = new File(workingFilePath);
                    if (!fileTemplate.exists()){
                        throw new TaskException(String.format(
                                "The file does not exist [%s]. This file must be in the projects folder: [%s] or in the working folder [%s]" ,
                                projectFilePath,
                                workingFilePath));
                    }
                }
            }
        }
    }

    /**
     * Determina el nombre del fichero de salida definitivo.
     */
    private void configureOutputFile() {

        if (StringUtils.isBlank(outputFile)) {
            // si no se proporciona fichero de entrada, generar un nombre
            // aleatorio
            String extension = (this instanceof ExcelWriter)?DEFAULT_EXTENSION:OLD_EXTENSION;
            outputFile = String.format("%s_%s%s", RandomStringUtils.randomAlphanumeric(10),
                    System.currentTimeMillis(), extension);
            LOGGER.info(String
                    .format("No se ha especificado el atributo outputFile para el ExcelWriter, "
                            + "generado un nombre de fichero aleatorio [%s].", outputFile));
        }

        outputFile = TaskResourceAccessor.getWorkingFile(getGlobalContext(), outputFile);
    }

    /**
     * Configura el punto de inicio de escritura del excel
     */
    private void configureStartingPoint() throws TaskException {
        if (StringUtils.isNotBlank(cellStart)) {
            String[] values = ExcelHelper.parseCell(cellStart);
            columnaInicio = ExcelHelper.getExcelColumnNumber(values[0]) - 1;
            filaInicio = Integer.valueOf(values[1]) - 1;

            if (filaInicio < 0) {
                throw new TaskException("El campo cellStart no está bien configurado, "
                        + "se espera un expresión del tipo AB12, C12, etc... "
                        + "El índice de columna debe empezar en 1 -> A1 es la primera celda no A0.");
            }
            LOGGER.info(String.format(
                    "Interpretación de la celda de inicio: [%s] -> fila,columna = (%s,%s) (0-based, para índices internos)",
                    cellStart, filaInicio, columnaInicio));
        } else {
            filaInicio = 0;
            columnaInicio = 0;
        }
    }

    protected void generaExcel(List<Map<String, Object>> resultados, File plantilla)
            throws TaskException {
        if (resultados == null) {
            return;
        }
        try {
            count += resultados.size();
            if (isFirstRow) {
                // calcular número de columnas a partir del primer registro
                numColumns = resultados.get(0).keySet().size();

                // configurar y enlazar objetos de libro/hoja Excel
                configureExcelSheet(plantilla);

                // escribir cabecera
                if (writeHeader) {
                    Row rowCabecera = hoja.getRow(filaInicio);
                    if (rowCabecera == null) {
                        rowCabecera = hoja.createRow(filaInicio);
                    }
                    filaInicio++;
                    escribeCabecerasExcel(resultados, rowCabecera, columnaInicio);
                }
                isFirstRow = false;
            }

            // escribir cuerpo de datos
            escribeDatosExcel(resultados, hoja, filaInicio, columnaInicio);
        } catch (Exception t) {
            throw new TaskException("Error inesperado al escribir el excel", t);
        }
    }

    /**
     * Crear objetos de libro y hoja de excel
     *
     * @param plantilla
     */
    protected void configureExcelSheet(File plantilla) throws TaskException {
        FileInputStream libroPlantilla;
        if (plantilla != null) {
            try {
                libroPlantilla = new FileInputStream(plantilla);
                streamLibro = getWorkBookInstance(libroPlantilla);
                if (StringUtils.isNotBlank(sheetName)) {
                    hoja = streamLibro.getSheet(sheetName);
                    if (hoja == null) {
                        if (sheetTemplateIdx != null) {
                            verifySheetTemplateIdx();
                            hoja = streamLibro.cloneSheet(sheetTemplateIdx);
                            streamLibro.setSheetName(streamLibro.getNumberOfSheets() - 1,
                                    sheetName);
                        } else {
                            hoja = streamLibro.createSheet(sheetName);
                        }
                    } else {
                        if (sheetTemplateIdx != null) {
                            verifySheetTemplateIdx();
                            ExcelCopySheet.copy(streamLibro.getSheetAt(sheetTemplateIdx), hoja,
                                    streamLibro.createCellStyle());
                        }
                    }
                } else {
                    // comprobar que no sobrepasamos el número de hojas
                    // existentes
                    if (sheetIndex < 0 || sheetIndex + 1 > streamLibro.getNumberOfSheets()) {
                        // sheetIndex es 0-based
                        throw new TaskException(String.format(
                                "Se ha indicado el sheetIndex [%s], "
                                        + "pero el libro sólo tiene [%s] hojas. Recuerda que "
                                        + "el valor de sheetIndex es 0-based.",
                                sheetIndex, streamLibro.getNumberOfSheets()));
                    }
                    hoja = streamLibro.getSheetAt(sheetIndex);
                    if (sheetTemplateIdx != null) {
                        verifySheetTemplateIdx();
                        ExcelCopySheet.copy(streamLibro.getSheetAt(sheetTemplateIdx), hoja,
                                streamLibro.createCellStyle());
                    }
                }
            } catch (IOException e) {
                String msj = String.format(
                        "No se ha podido abrir la plantilla %s, se creará a partir de una nueva hoja.",
                        plantilla.getAbsolutePath());
                throw new TaskException(msj, e);
            }
        } else {
            streamLibro = getWorkBookInstance();
            if (StringUtils.isNotBlank(sheetName)) {
                hoja = streamLibro.createSheet(sheetName);
            } else {
                hoja = streamLibro.createSheet("Hoja1");
            }
        }
    }

    private void verifySheetTemplateIdx() throws TaskException {
        if (sheetTemplateIdx < 0 || sheetTemplateIdx + 1 > streamLibro.getNumberOfSheets()) {
            // sheetTemplateIdx es 0-based
            throw new TaskException(String.format(
                    "Se ha indicado el sheetTemplateIdx [%s], "
                            + "pero el libro sólo tiene [%s] hojas. Recuerda que "
                            + "el valor de sheetTemplateIdx es 0-based.",
                    sheetTemplateIdx, streamLibro.getNumberOfSheets()));
        }
    }

    @Override
    public void close() throws TaskException {
        if (count == 0) {
            // si el reader devolvió 0 filas se escribirá un fichero excel vacío
            configureExcelSheet(fileTemplate);
        }
        FileOutputStream outputStream = null;
        File fileOutputExcel = new File(outputFile);
        try {
            outputStream = new FileOutputStream(fileOutputExcel);
        } catch (FileNotFoundException e) {
            String mensaje = String.format("Error al crear el archivo %s", outputFile);
            LOGGER.error(mensaje);
            throw new TaskException(mensaje, e);
        }

        onBeforeClose(outputStream);

        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(String.format("Escribiendo fichero de salida [%s]", outputFile));
            }
            streamLibro.write(outputStream);
            streamLibro.close();

            outputStream.flush();
            outputStream.close();

            // insertar el fichero resultado en el contexto actual
            getTaskContext().put("outputFile", outputFile);
            getTaskContext().put("count", count);

        } catch (Exception t) {
            throw new TaskException(String.format("Error al cerrar el archivo %s", outputFile), t);
        }

    }

    protected int cellStringToNumber(String s) {
        s = s.toLowerCase();
        int result = 0;

        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            // le resto 97 para que me asigne a la a, el 0
            result += ch - 97;
        }

        return result;
    }

    /**
     * Escribe la fila de cabeceras excel
     *
     * @param resultados  List<Map<String, Object>>
     * @param cabecera    Fila excel
     * @param celdaInicio Celda de inicio de escritura
     */
    private void escribeCabecerasExcel(List<Map<String, Object>> resultados, Row cabecera,
                                       int celdaInicio) {
        try {
            if (!resultados.isEmpty()) {
                Map<String, Object> primerReg = resultados.get(0);
                Object[] arrayCabeceras = primerReg.keySet().toArray();

                for (int numCol = 0; numCol < arrayCabeceras.length; numCol++) {
                    Cell celda = cabecera.getCell(celdaInicio + numCol);
                    if (celda == null) {
                        celda = cabecera.createCell(celdaInicio + numCol);
                    }
                    celda.setCellType(CellType.STRING);
                    celda.setCellValue(String.valueOf(arrayCabeceras[numCol]));
                }
            }

        } catch (Exception e) {
            LOGGER.error("Error al escribir las cabeceras", e);
            throw e;
        }
    }

    /**
     * Escribe la matriz de datos excel
     *
     * @param resultados    List<Map<String, Object>>
     * @param hoja          Hoja excel
     * @param filaInicio    Fila desde la que se empezará a escribir en la hoja
     * @param celdaInicio   Celda desde la que se empezará a escribir en la hoja
     * @throws IOException
     */
    private void escribeDatosExcel(List<Map<String, Object>> resultados, Sheet hoja, int filaInicio,
                                   int celdaInicio) {
        int numFila = filaInicio;
        Iterator<Map<String, Object>> iterator = resultados.iterator();
        Map<String, Object> valores = null;

        while (iterator.hasNext()) {
            valores = iterator.next();
            Row fila = hoja.getRow(numFila);
            if (fila == null) {
                fila = hoja.createRow(numFila);
            }
            numFila++;

            Object[] arrayCabeceras = valores.keySet().toArray();

            for (int numCol = 0; numCol < numColumns; numCol++) {
                Cell celda = fila.getCell(celdaInicio + numCol);
                if (celda == null) {
                    celda = fila.createCell(celdaInicio + numCol);
                }
                Object o = valores.get(String.valueOf(arrayCabeceras[numCol]));

                if (o != null) {
                    String valor = o.toString();
                    if (StringUtils.isNotEmpty(valor)) {
                        CellStyle estiloCelda = getEstiloCelda(hoja.getWorkbook(), arrayCabeceras,
                                numCol);
                        if (o instanceof Boolean) {
                            celda.setCellValue((boolean) o);
                        } else if (o instanceof Short) {
                            Short b = (Short) o;
                            celda.setCellValue(b);
                        } else if (o instanceof Integer) {
                            Integer b = (Integer) o;
                            celda.setCellValue(b);
                        } else if (o instanceof Long) {
                            Long b = (Long) o;
                            celda.setCellValue(b);
                        } else if (o instanceof Float) {
                            Float b = (Float) o;
                            celda.setCellValue(b.doubleValue());
                        } else if (o instanceof Double) {
                            Double b = (Double) o;
                            celda.setCellValue(b.doubleValue());
                        } else if (o instanceof BigDecimal) {
                            BigDecimal b = (BigDecimal) o;
                            celda.setCellValue(b.doubleValue());
                        } else if (o instanceof Date) {
                            escribeCeldaFecha(hoja.getWorkbook(), celda, (Date) o);
                        } else if (o instanceof Calendar) {
                            Calendar cal = (Calendar) o;
                            escribeCeldaFecha(hoja.getWorkbook(), celda, cal.getTime());
                        } else {
                            celda.setCellValue(valor);
                        }
                        if (estiloCelda != null) {
                            celda.setCellStyle(estiloCelda);
                        }
                    }
                } else {
                    celda.setCellValue(" ");
                }
            }
        }
        // actualizar la fila de inicio para la siguiente página
        this.filaInicio = numFila;
    }

    private CellStyle getEstiloCelda(Workbook wb, Object[] arrayCabeceras, int numCol) {
        CellStyle estiloCelda = null;
        if (columnDataFormat != null) {
            String formato = columnDataFormat.get(arrayCabeceras[numCol]);
            if (StringUtils.isNotBlank(formato)) {
                estiloCelda = wb.createCellStyle();
                CreationHelper createHelper = wb.getCreationHelper();
                short formatoIndex = createHelper.createDataFormat().getFormat(formato);
                estiloCelda.setDataFormat(formatoIndex);
            }
        }
        return estiloCelda;
    }

    /**
     * Escribe una celda fecha
     *
     * @param wb    Libro
     * @param celda Celda
     * @param fecha fecha
     */
    private void escribeCeldaFecha(Workbook wb, Cell celda, Date fecha) {
        CreationHelper createHelper = wb.getCreationHelper();

        if (estiloNumeroFecha == null) {
            estiloNumeroFecha = wb.createCellStyle();
            estiloNumeroFecha
                    .setDataFormat(createHelper.createDataFormat().getFormat(CELDA_FORMATO_FECHA));
        }
        celda.setCellValue(fecha);
        celda.setCellStyle(estiloNumeroFecha);
    }

    /********************/
    /** PUNTOS DE EXTENSIÓN PARA SUBCLASES **/
    /**********************/
    protected abstract void onBeforeClose(FileOutputStream outputStream);

    protected abstract Workbook getWorkBookInstance();

    protected abstract Workbook getWorkBookInstance(FileInputStream libroPlantilla)
            throws IOException;

    /********************/
    /** MÉTODOS ACCESO A PROPIEDADES INTERNAS **/
    /**********************/
    protected Workbook getCurrentBookStream() {
        return streamLibro;
    }

    protected Sheet getCurrentSheet() {
        return hoja;
    }

    protected int getNumColumns() {
        return numColumns;
    }

    protected int getStartingColumn() {
        return columnaInicio;
    }

    /*********************/
    /** GETTERS/SETTERS **/
    /*********************/

    public int getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public String getCellStart() {
        return cellStart;
    }

    public void setCellStart(String cellStart) {
        this.cellStart = cellStart;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public int getAutoResize() {
        return autoResize;
    }

    public void setAutoResize(int autoResize) {
        this.autoResize = autoResize;
    }

    public boolean isWriteHeader() {
        return writeHeader;
    }

    public void setWriteHeader(boolean writeHeader) {
        this.writeHeader = writeHeader;
    }

    public boolean isEvaluateFormulas() {
        return evaluateFormulas;
    }

    public void setEvaluateFormulas(boolean evaluateFormulas) {
        this.evaluateFormulas = evaluateFormulas;
    }

    public Map<String, String> getColumnDataFormat() {
        return columnDataFormat;
    }

    public void setColumnDataFormat(Map<String, String> columnDataFormat) {
        this.columnDataFormat = columnDataFormat;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getSheetTemplateIdx() {
        return sheetTemplateIdx;
    }

    public void setSheetTemplateIdx(Integer sheetTemplateIdx) {
        this.sheetTemplateIdx = sheetTemplateIdx;
    }

}
