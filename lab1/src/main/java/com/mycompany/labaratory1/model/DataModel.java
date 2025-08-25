/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.labaratory1.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class DataModel {
    private final List<List<Double>> data;      
    private final List<String> sampleNames;     
    private final Statistics statistics;        

    public DataModel() {
        this.data = new ArrayList<>();
        this.sampleNames = new ArrayList<>();
        this.statistics = new Statistics(this);
    }


    public List<List<Double>> getData() {
        return data;
    }

    public List<String> getSampleNames() {
        return sampleNames;
    }

    public Statistics getStatistics() {
        return statistics;
    }


    public void importFromExcel(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }

            String selectedSheet = (String) JOptionPane.showInputDialog(
                null, "Выберите страницу:", "Выбор", 
                JOptionPane.QUESTION_MESSAGE, null, 
                sheetNames.toArray(), sheetNames.get(0)
            );

            if (selectedSheet == null) return;

            Sheet sheet = workbook.getSheet(selectedSheet);
            clearData(); 

            int numColumns = sheet.getRow(0).getPhysicalNumberOfCells();
            validateSheetStructure(sheet, numColumns);

            for (int i = 0; i < numColumns; i++) {
                addSample(new ArrayList<>(), "Выборка " + (i + 1));
            }

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) { 
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue; 
                for (int i = 0; i < numColumns; i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    double value = parseCellValue(cell, evaluator);
                    data.get(i).add(value);
                   }
                }

        } catch (IllegalArgumentException e) {
            throw new IOException("Ошибка структуры файла: " + e.getMessage());
        }
    }

    public void exportResults(String filePath, double confidenceLevel) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Результаты");

          
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Выборка", "Среднее геом.", "Среднее арифм.", 
                "Ст. отклонение", "Размах", "Количество", 
                "Коэф. вариации", "Минимум", "Максимум", 
                "Дисперсия", "Доверительный интервал"
            };
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            List<StatisticsResult> results = statistics.calculateAll();
            int rowIndex = 1;
            for (StatisticsResult result : results) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(result.getSampleName());
                row.createCell(1).setCellValue(result.getGeometricMean());
                row.createCell(2).setCellValue(result.getMean());
                row.createCell(3).setCellValue(result.getStdDev());
                row.createCell(4).setCellValue(result.getRange());
                row.createCell(5).setCellValue(result.getCount());
                row.createCell(6).setCellValue(result.getCoefficientOfVariation());
                row.createCell(7).setCellValue(result.getMin());
                row.createCell(8).setCellValue(result.getMax());
                row.createCell(9).setCellValue(result.getVariance());
                double[] ci = statistics.calculateConfidenceInterval(confidenceLevel, results.indexOf(result));
                row.createCell(10).setCellValue(String.format("[%.2f, %.2f]", ci[0], ci[1]));
            }

            
            Row covHeader = sheet.createRow(rowIndex++);
            covHeader.createCell(0).setCellValue("Ковариации");
            List<Double> covariances = statistics.calculateCovariance();
            int covIndex = 0;
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(
                        sampleNames.get(i) + " и " + sampleNames.get(j)
                    );
                    row.createCell(1).setCellValue(covariances.get(covIndex++));
                }
            }

            workbook.write(fos);
        }
    }

    private void validateSheetStructure(Sheet sheet, int expectedColumns) {
        for (Row row : sheet) {
            if (row.getPhysicalNumberOfCells() != expectedColumns) {
                throw new IllegalArgumentException("Столбцы имеют разную длину.");
            }
        }
    }

    private double parseCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.FORMULA) {
            return evaluator.evaluate(cell).getNumberValue();
        }
        return 0.0; 
    }

    public void addSample(List<Double> sample, String name) {
        data.add(sample);
        sampleNames.add(name);
    }

    public void clearData() {
        data.clear();
        sampleNames.clear();
    }
}
