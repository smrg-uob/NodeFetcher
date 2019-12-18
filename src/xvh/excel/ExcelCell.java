package xvh.excel;

import org.apache.poi.ss.usermodel.*;

import java.util.Calendar;
import java.util.Date;

public class ExcelCell {
    private static final DataFormatter DEFAULT_FORMATTER = new DataFormatter();

    private final ExcelRow row;
    private final Cell cell;
    private final int column;

    private DataFormatter formatter;

    public ExcelCell(ExcelRow row, Cell cell, int column) {
        this.row = row;
        this.cell = cell;
        this.column = column;
        this.formatter = DEFAULT_FORMATTER;
    }

    protected Cell getCell() {
        return this.cell;
    }

    public int getColumnIndex() {
        return this.column;
    }

    public ExcelRow getRow() {
        return this.row;
    }

    public ExcelSheet getExcelSheet() {
        return this.getRow().getExcelSheet();
    }

    public ExcelWorkbook getExcelWorkbook() {
        return this.getRow().getExcelWorkbook();
    }

    public String getValue() {
        FormulaEvaluator evaluator = this.getExcelWorkbook().getEvaluator();
        evaluator.evaluate(this.getCell());
        return this.formatter.formatCellValue(this.getCell(), evaluator);
    }

    public ExcelCell setCellValue(double value) {
        this.getCell().setCellValue(value);
        return this;
    }

    public ExcelCell setCellValue(boolean value) {
        this.getCell().setCellValue(value);
        return this;
    }

    public ExcelCell setCellValue(String value) {
        this.getCell().setCellValue(value);
        return this;
    }

    public ExcelCell setCellValue(Date value) {
        this.getCell().setCellValue(value);
        return this;
    }

    public ExcelCell setCellValue(Calendar value) {
        this.getCell().setCellValue(value);
        return this;
    }

    public ExcelCell setCellValue(RichTextString value) {
        this.getCell().setCellValue(value);
        return this;
    }

    public void clear() {
        this.getRow().removeCell(this.getColumnIndex());
    }

    public ExcelCell setComment(String value, int width, int height, String author) {
        CreationHelper factory = this.getExcelWorkbook().getWorkbook().getCreationHelper();
        Drawing drawing = this.getExcelSheet().getSheet().createDrawingPatriarch();
        // When the comment box is visible, have it show in a 5x3 space
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(this.getCell().getColumnIndex());
        anchor.setCol2(this.getCell().getColumnIndex() + width);
        anchor.setRow1(this.getCell().getRowIndex());
        anchor.setRow2(this.getCell().getRowIndex() + height);
        //Create the comment and set the text + author
        Comment comment = drawing.createCellComment(anchor);
        comment.setString(factory.createRichTextString(value));
        comment.setAuthor(author);
        //Assign the comment to the cell
        this.getCell().setCellComment(comment);
        //return this
        return this;
    }
}
