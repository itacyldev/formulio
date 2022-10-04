package es.jcyl.ita.formic.jayjobs.task.utils;/*
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

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
public class ExcelHelper {

    private static final DataFormatter defaultDataformatter = new DataFormatter();

    private static final char A_CHAR = 'A';

    public static int getExcelColumnNumber(String column) {
        // me quedo sólo con la parte String
        String[] cellStr = column.split("([0-9]+)");

        int result = 0;
        for (int i = 0; i < cellStr[0].length(); i++) {
            result *= 26;
            result += cellStr[0].charAt(i) - A_CHAR + 1;
        }
        return result;
    }

    /**
     * Parsea una celda excel y separa la parte de texto de la numérica. Ej:
     * AB123 -> AB, 123
     *
     * @param cell
     * @return
     */
    public static String[] parseCell(String cell) {
        int numberStart = -1;
        for (int i = 0; i < cell.length(); i++) {
            if (Character.isDigit(cell.charAt(i))) {
                numberStart = i;
                break;
            }
        }
        String chars = cell.substring(0, numberStart);
        String number = cell.substring(numberStart);
        return new String[] { chars, number };
    }

    public static boolean isBlankRow(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row
                .getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK
                    && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlankRow(Row row, FormulaEvaluator evaluator,
                                     DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        String value = "";
        for (int cellNum = row.getFirstCellNum(); cellNum < row
                .getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK
                    && StringUtils.isNotBlank(cell.toString())) {

                if (cell.getCellTypeEnum() == CellType.FORMULA) {
                    value = formatter.formatCellValue(cell, evaluator);
                    return StringUtils.isBlank(value);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isBlankRow(Row row, FormulaEvaluator evaluator) {
        return isBlankRow(row, evaluator, defaultDataformatter);
    }
}