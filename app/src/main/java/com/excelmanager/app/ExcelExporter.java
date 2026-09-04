package com.excelmanager.app;

import android.content.Context;
import android.os.Environment;
import com.excelmanager.app.models.Category;
import com.excelmanager.app.models.Entry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {
    private Context context;
    private DatabaseHelper dbHelper;

    public ExcelExporter(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public String exportToExcel(long fileId, String fileName) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        List<Category> categories = dbHelper.getCategories(fileId);

        if (categories.isEmpty()) {
            throw new Exception("لا توجد قوائم لتصديرها، أضف قائمة أولاً");
        }

        XSSFCellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleFont.setColor(IndexedColors.WHITE.getIndex());
        titleStyle.setFont(titleFont);
        titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        XSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);

        for (Category category : categories) {
            XSSFSheet sheet = workbook.createSheet(category.getName());
            sheet.setRightToLeft(true);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(category.getName() + " - " + fileName);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

            Row headerRow = sheet.createRow(1);
            Cell h0 = headerRow.createCell(0);
            h0.setCellValue("التاريخ");
            h0.setCellStyle(headerStyle);

            Cell h1 = headerRow.createCell(1);
            h1.setCellValue("البيان / المبلغ");
            h1.setCellStyle(headerStyle);

            List<Entry> entries = dbHelper.getEntries(category.getId());
            int rowNum = 2;
            for (Entry entry : entries) {
                Row dataRow = sheet.createRow(rowNum++);
                Cell c0 = dataRow.createCell(0);
                c0.setCellValue(entry.getDate());
                c0.setCellStyle(dataStyle);

                Cell c1 = dataRow.createCell(1);
                c1.setCellValue(entry.getContent());
                c1.setCellStyle(dataStyle);
            }

            sheet.setColumnWidth(0, 5000);
            sheet.setColumnWidth(1, 10000);
        }

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ExcelManager");
        if (!dir.exists()) dir.mkdirs();

        String safeFileName = fileName.replaceAll("[^a-zA-Z0-9\u0600-\u06FF ]", "_");
        File file = new File(dir, safeFileName + ".xlsx");

        FileOutputStream fos = new FileOutputStream(file);
        workbook.write(fos);
        fos.close();
        workbook.close();

        return file.getAbsolutePath();
    }
}