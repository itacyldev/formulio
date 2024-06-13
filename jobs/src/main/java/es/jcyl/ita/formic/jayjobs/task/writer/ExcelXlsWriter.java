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

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class ExcelXlsWriter extends AbstractExcelWriter {
    @Override
    protected Workbook getWorkBookInstance() {
        return new HSSFWorkbook();
    }

    @Override
    protected Workbook getWorkBookInstance(FileInputStream libroPlantilla)
            throws IOException {
        return new HSSFWorkbook(libroPlantilla);
    }

    @Override
    protected void onBeforeClose(FileOutputStream outputStream) {
        if (this.getAutoResize() == 1) {
            autoResizeColumn();
        }
        if (this.isEvaluateFormulas()) {
            HSSFFormulaEvaluator.evaluateAllFormulaCells(this.getCurrentBookStream());
        }
    }

    /**
     * Ejecuta un autoresize de la hoja que acabamos de escribir
     */
    private void autoResizeColumn() {
        for (int numCol = this.getStartingColumn(); numCol <= this
                .getNumColumns() + this.getStartingColumn(); numCol++) {
            this.getCurrentSheet().setColumnWidth(numCol, 25*256);
        }
    }
}
