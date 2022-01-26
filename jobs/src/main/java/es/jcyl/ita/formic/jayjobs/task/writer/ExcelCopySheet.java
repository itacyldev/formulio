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

/**
 * @autor Rosa María Muñiz (mungarro@itacyl.es)
 */
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class ExcelCopySheet {

    public static void copy(Sheet sourceSheet, Sheet destSheet,
                            CellStyle newStyle) {
        Row row;
        Cell cell;

        for (int rowIndex = 0; rowIndex < sourceSheet
                .getPhysicalNumberOfRows(); rowIndex++) {
            row = destSheet.createRow(rowIndex);

            for (int colIndex = 0; colIndex < sourceSheet.getRow(rowIndex)
                    .getPhysicalNumberOfCells(); colIndex++) {
                cell = row.createCell(colIndex);
                Cell c = sourceSheet.getRow(rowIndex).getCell(colIndex,
                        Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                // get cell from old/original WB's sheet and when cell is null,
                // return it as blank cells. And
                // Blank cell will be returned as Blank cells. That will not
                // change.
                if (c.getCellTypeEnum() == CellType.BLANK) {
                    System.out.println(
                            "This is BLANK " + ((XSSFCell) c).getReference());
                } else { // Below is where all the copying is happening.
                    // First It copies the styles of each cell and
                    // then it copies the content.
                    CellStyle origStyle = c.getCellStyle();
                    newStyle.cloneStyleFrom(origStyle);
                    cell.setCellStyle(newStyle);

                    switch (c.getCellTypeEnum()) {
                        case STRING:
                            cell.setCellValue(
                                    c.getRichStringCellValue().getString());
                            break;
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                cell.setCellValue(c.getDateCellValue());
                            } else {
                                cell.setCellValue(c.getNumericCellValue());
                            }
                            break;
                        case BOOLEAN:
                            cell.setCellValue(c.getBooleanCellValue());
                            break;
                        case FORMULA:
                            cell.setCellValue(c.getCellFormula());
                            break;
                        case BLANK:
                            cell.setCellValue(" ");
                            break;
                        default:
                            // pass
                    }
                }
            }
        }

    }
}