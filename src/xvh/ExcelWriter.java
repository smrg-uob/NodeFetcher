package xvh;

import xvh.excel.ExcelSheet;
import xvh.excel.ExcelWorkbook;
import xvh.nodedata.NodeData;

import java.io.IOException;
import java.util.List;

public class ExcelWriter {
    public static boolean writeToExcel(String file, String sheetName, String[] headers, List<NodeData> data) throws IOException, IllegalArgumentException {
        ExcelWorkbook wb = ExcelWorkbook.openWorkbook(file);
        ExcelSheet sheet;
        if (sheetName == null) {
            sheet = wb.createSheet();
        } else {
            sheet = wb.getOrCreateSheet(sheetName);
            sheet.clear();
        }
        //write headers
        for(int i = 0; i < headers.length; i++) {
            sheet.getOrCreateCell(0, i).setCellValue(headers[i]);
        }
        //write data
        for(int row = 1; row <= data.size(); row++) {
            NodeData node = data.get(row - 1);
            sheet.getOrCreateCell(row, 0).setCellValue(node.getId());
            sheet.getOrCreateCell(row, 1).setCellValue(node.getX());
            sheet.getOrCreateCell(row, 2).setCellValue(node.getY());
            sheet.getOrCreateCell(row, 3).setCellValue(node.getZ());
            for(int i = 4; i < headers.length; i++) {
                String tag = headers[i];
                sheet.getOrCreateCell(row, i).setCellValue(node.getData(tag));
            }
        }
        //save workbook
        wb.save();
        //close workbook
        wb.close();
        return true;
    }
}
