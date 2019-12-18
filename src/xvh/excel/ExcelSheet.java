package xvh.excel;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExcelSheet {
    private final int index;
    private final ExcelWorkbook workbook;
    private final Sheet sheet;

    private final Map<Integer, ExcelRow> rowCache;

    protected ExcelSheet(int index, ExcelWorkbook workbook, Sheet sheet) {
        this.index = index;
        this.workbook = workbook;
        this.sheet = sheet;
        this.rowCache = new HashMap<>();
    }

    protected Sheet getSheet() {
        return this.sheet;
    }

    public ExcelWorkbook getExcelWorkbook() {
        return this.workbook;
    }

    public int getSheetIndex() {
        return this.index;
    }

    public String getSheetName() {
        return this.getSheet().getSheetName();
    }

    public ExcelSheet setSheetName(String name) {
        this.getExcelWorkbook().getWorkbook().setSheetName(this.index, name);
        return this;
    }

    public ExcelSheet autoformatColumnWidth(int column) {
        this.getSheet().autoSizeColumn(column);
        return this;
    }

    public int getFirstRowIndex() {
        return this.getSheet().getFirstRowNum();
    }

    public int getLastRowIndex() {
        return this.getSheet().getLastRowNum();
    }

    public Optional<ExcelRow> getRow(int row) {
        if(!rowCache.containsKey(row)) {
            Row r = this.sheet.getRow(row);
            if(r != null) {
                this.rowCache.put(row, new ExcelRow(this, r));
            }
        }
        return Optional.ofNullable(this.rowCache.get(row));
    }

    public Optional<ExcelCell> getCell(int row, int column) {
        return this.getRow(row).flatMap(r -> r.getCell(column));
    }

    public ExcelRow createRow(int row) {
        if(!this.rowCache.containsKey(row)) {
            this.rowCache.put(row, new ExcelRow(this, this.getSheet().createRow(row)));
        }
        return this.rowCache.get(row);
    }

    public ExcelCell createCell(int row, int column) {
        return this.createRow(row).createCell(column);
    }

    public ExcelRow getOrCreateRow(int row) {
        return this.getRow(row).orElse(this.createRow(row));
    }

    public ExcelCell getOrCreateCell(int row, int column) {
        return this.getOrCreateRow(row).getOrCreateCell(column);
    }

    public void removeRow(int row) {
        this.getRow(row).ifPresent(r -> {
            this.getSheet().removeRow(r.getRow());
            this.rowCache.remove(row);
        });
    }

    public void removeCell(int row, int column) {
        this.getCell(row, column).ifPresent(ExcelCell::clear);
    }

    public void clear() {
        for(int i = this.getFirstRowIndex(); i >= 0 && i <= this.getLastRowIndex(); i++) {
            this.removeRow(i);
        }
    }

    public void save() {
        this.getExcelWorkbook().save();
    }
}
