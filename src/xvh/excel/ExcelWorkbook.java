package xvh.excel;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExcelWorkbook {
    private final File file;
    private final Type type;
    private final Workbook workbook;
    private final FormulaEvaluator evaluator;

    private final Map<Integer, ExcelSheet> sheetCache;

    @SuppressWarnings("unchecked")
    protected ExcelWorkbook(File file) throws IOException, IllegalArgumentException {
        this.file = file;
        this.type = Type.getType(file);
        this.workbook = this.type.openWorkbook(this.file);
        this.evaluator = this.type.createEvaluator(this.workbook);
        this.sheetCache = new HashMap<>();
    }

    public File getFile() {
        return this.file;
    }

    protected Type getType() {
        return this.type;
    }

    protected Workbook getWorkbook() {
        return this.workbook;
    }

    protected FormulaEvaluator getEvaluator() {
        return this.evaluator;
    }

    public int getSheetCount() {
        return this.getWorkbook().getNumberOfSheets();
    }

    public Optional<ExcelSheet> getSheet(int sheet) {
        if(!this.sheetCache.containsKey(sheet)) {
            if(this.getWorkbook().getNumberOfSheets() >= sheet) {
                this.sheetCache.put(sheet, new ExcelSheet(sheet, this, this.getWorkbook().getSheetAt(sheet)));
            }
        }
        return Optional.ofNullable(this.sheetCache.get(sheet));
    }

    public Optional<ExcelSheet> getSheet(String name) {
        ExcelSheet sheet = null;
        for(int i = 0; i < this.getSheetCount() && sheet == null; i++) {
            sheet = this.getSheet(i).filter(s -> s.getSheetName().equalsIgnoreCase(name)).orElse(null);
        }
        return Optional.ofNullable(sheet);
    }

    public ExcelSheet getOrCreateSheet(String name) {
        return this.getSheet(name).orElseGet(() -> {
            ExcelSheet sheet = this.createSheet();
            sheet.setSheetName(name);
            return sheet;
        });
    }

    public ExcelSheet createSheet() {
        ExcelSheet sheet = new ExcelSheet(this.getSheetCount(), this, this.getWorkbook().createSheet());
        this.sheetCache.put(sheet.getSheetIndex(), sheet);
        return sheet;
    }

    public void save() {
        try {
            FileOutputStream outputStream = new FileOutputStream(this.getFile());
            try {
                this.workbook.write(outputStream);
            } catch (IOException e) {
                System.out.println(" -> ERROR: Couldn't save workbook " + this.getFile().getName());
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } catch(FileNotFoundException e) {
            System.out.println(" -> ERROR: Couldn't save workbook " + this.getFile().getName());
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        this.workbook.close();
    }

    public static ExcelWorkbook openWorkbook(String file) throws IOException, IllegalArgumentException {
        return openWorkbook(new File(file));
    }

    public static ExcelWorkbook openWorkbook(File file) throws IOException, IllegalArgumentException {
        return new ExcelWorkbook(file);
    }

    private interface Type<T extends Workbook> {
        T openWorkbook(File file) throws IOException;

        FormulaEvaluator createEvaluator(T workbook);

        static Type getType(File file) throws IllegalArgumentException {
            if(file.getAbsolutePath().endsWith("xlsx")) {
                return XSSF;
            } else if(file.getAbsolutePath().endsWith("xls")) {
                return HSSF;
            }
            throw new IllegalArgumentException("File is not an excel file");
        }

        Type<XSSFWorkbook> XSSF = new Type<XSSFWorkbook>() {
            @Override
            public XSSFWorkbook openWorkbook(File file) throws IOException {
                return file.exists() ? new XSSFWorkbook(new FileInputStream(file)) : new XSSFWorkbook();
            }

            @Override
            public FormulaEvaluator createEvaluator(XSSFWorkbook workbook) {
                return new XSSFFormulaEvaluator(workbook);
            }
        };

        Type<HSSFWorkbook> HSSF = new Type<HSSFWorkbook>() {
            @Override
            public HSSFWorkbook openWorkbook(File file) throws IOException {
                return file.exists() ? new HSSFWorkbook(new FileInputStream(file)) : new HSSFWorkbook();
            }

            @Override
            public FormulaEvaluator createEvaluator(HSSFWorkbook workbook) {
                return new HSSFFormulaEvaluator(workbook);
            }
        };
    }
}
