package xvh.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExcelRow {
    private final ExcelSheet sheet;
    private final Row row;

    private final Map<Integer, ExcelCell> cellCache;

    protected ExcelRow(ExcelSheet sheet, Row row) {
        this.sheet = sheet;
        this.row = row;
        this.cellCache = new HashMap<>();
    }

    public int getRowIndex() {
        return this.row.getRowNum();
    }

    protected Row getRow() {
        return this.row;
    }

    public ExcelSheet getExcelSheet() {
        return this.sheet;
    }

    public ExcelWorkbook getExcelWorkbook() {
        return this.getExcelSheet().getExcelWorkbook();
    }

    public int getFirstColumnIndex() {
        return this.getRow().getFirstCellNum();
    }

    public int getLastColumnIndex() {
        return this.getRow().getLastCellNum();
    }

    public Optional<ExcelCell> getCell(int column) {
        if(!cellCache.containsKey(column)) {
            Cell cell = this.getRow().getCell(column);
            if(cell != null) {
                this.cellCache.put(column, new ExcelCell(this, cell, column));
            }
        }
        return Optional.ofNullable(this.cellCache.get(column));
    }

    public ExcelCell createCell(int column) {
        if(!this.cellCache.containsKey(column)) {
            this.cellCache.put(column, new ExcelCell(this, this.getRow().createCell(column), column));
        }
        return this.cellCache.get(column);
    }

    public ExcelCell getOrCreateCell(int column) {
        return this.getCell(column).orElse(this.createCell(column));
    }

    public void removeCell(int column) {
        this.getCell(column).ifPresent(cell -> {
            this.getRow().removeCell(cell.getCell());
            this.cellCache.remove(column);
        });
    }

    public void clear() {
        for(int i = this.getFirstColumnIndex(); i >= 0 && i <= this.getLastColumnIndex(); i++) {
            this.removeCell(i);
        }
        this.getExcelSheet().removeRow(this.getRowIndex());
    }
}
