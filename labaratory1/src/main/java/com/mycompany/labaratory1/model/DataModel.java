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
    private List<List<Double>> data;
    private List<String> sampleNames;

    public DataModel() {
        this.data = new ArrayList<>();
        this.sampleNames = new ArrayList<>();
    }

    public void importFromExcel(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            List<String> sheetNames = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheetNames.add(workbook.getSheetName(i));
            }

            String selectedSheetName = (String) JOptionPane.showInputDialog(
                    null,
                    "Выберите лист с данными:",
                    "Выбор листа",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    sheetNames.toArray(),
                    sheetNames.get(0)
            );

            if (selectedSheetName == null) {
                return;
            }

            Sheet sheet = workbook.getSheet(selectedSheetName);

            data.clear();
            sampleNames.clear();

            int firstRowColumns = sheet.getRow(0).getPhysicalNumberOfCells();
            for (Row row : sheet) {
                if (row.getPhysicalNumberOfCells() != firstRowColumns) {
                    throw new IOException("Ошибка структуры файла: не все столбцы имеют одинаковую длину.");
                }
            }

            int numColumns = sheet.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < numColumns; i++) {
                data.add(new ArrayList<>());
                sampleNames.add("Выборка " + (i + 1));
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                for (int c = 0; c < numColumns; c++) {
                    Cell cell = row.getCell(c);
                    double cellValue;

                    if (cell == null || cell.getCellType() == CellType.BLANK) {
                        cellValue = 0.0;
                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        cellValue = cell.getNumericCellValue();
                    } else if (cell.getCellType() == CellType.FORMULA) {
                        cellValue = cell.getNumericCellValue();
                    } else {
                        cellValue = 0.0;
                    }
                    data.get(c).add(cellValue);
                }
            }
        }
    }

    public void exportResults(String filePath, double confidenceLevel) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.createSheet("Результаты");

            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "Показатель", "Выборка 1", "Выборка 2", "..."
            };

            String[] metricNames = {
                    "Среднее геометрическое", "Среднее арифметическое", "Стандартное отклонение",
                    "Размах", "Количество элементов", "Коэффициент вариации", "Минимум", "Максимум",
                    "Дисперсия", "Доверительный интервал"
            };

            headerRow.createCell(0).setCellValue("Показатель");
            for (int i = 0; i < sampleNames.size(); i++) {
                headerRow.createCell(i + 1).setCellValue(sampleNames.get(i));
            }

            int rowIndex = 1;
            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[0]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(calculateGeometricMean(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[1]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(calculateMean(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[2]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(calculateStdDev(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[3]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(calculateRange(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[4]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(getCount(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[5]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(calculateCoefficientOfVariation(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[6]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(getMin(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[7]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(getMax(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[8]);
            for(int i=0; i<data.size(); i++) sheet.getRow(rowIndex).createCell(i+1).setCellValue(calculateVariance(i));
            rowIndex++;

            sheet.createRow(rowIndex).createCell(0).setCellValue(metricNames[9]);
            for(int i=0; i<data.size(); i++) {
                double[] ci = calculateConfidenceInterval(i, confidenceLevel);
                sheet.getRow(rowIndex).createCell(i+1).setCellValue(String.format("[%.2f; %.2f]", ci[0], ci[1]));
            }
            rowIndex++;

            rowIndex++;

            Row covHeader = sheet.createRow(rowIndex++);
            covHeader.createCell(0).setCellValue("Ковариация");
            for (int i = 0; i < data.size(); i++) {
                for (int j = 0; j < data.size(); j++) {
                    Row covRow = sheet.createRow(rowIndex++);
                    covRow.createCell(0).setCellValue(sampleNames.get(i) + " и " + sampleNames.get(j));
                    covRow.createCell(1).setCellValue(calculateCovariance(i, j));
                }
            }

            workbook.write(fos);
        }
    }

    public int getCount(int sampleIndex) {
        return data.get(sampleIndex).size();
    }

    public double getMin(int sampleIndex) {
        if (data.get(sampleIndex).isEmpty()) return 0;
        return Collections.min(data.get(sampleIndex));
    }

    public double getMax(int sampleIndex) {
        if (data.get(sampleIndex).isEmpty()) return 0;
        return Collections.max(data.get(sampleIndex));
    }

    public double calculateRange(int sampleIndex) {
        if (data.get(sampleIndex).isEmpty()) return 0;
        return getMax(sampleIndex) - getMin(sampleIndex);
    }

    public double calculateMean(int sampleIndex) {
        List<Double> sample = data.get(sampleIndex);
        if (sample.isEmpty()) return 0;
        double sum = 0;
        for (double num : sample) {
            sum += num;
        }
        return sum / sample.size();
    }

    public double calculateGeometricMean(int sampleIndex) {
        List<Double> sample = data.get(sampleIndex);
        if (sample.isEmpty()) return 0;
        double product = 1.0;
        for (double num : sample) {
            if (num <= 0) return 0;
            product *= num;
        }
        return Math.pow(product, 1.0 / sample.size());
    }

    public double calculateVariance(int sampleIndex) {
        List<Double> sample = data.get(sampleIndex);
        if (sample.size() < 2) return 0;
        double mean = calculateMean(sampleIndex);
        double temp = 0;
        for (double a : sample) {
            temp += (a - mean) * (a - mean);
        }
        return temp / (sample.size() - 1);
    }

    public double calculateStdDev(int sampleIndex) {
        return Math.sqrt(calculateVariance(sampleIndex));
    }

    public double calculateCoefficientOfVariation(int sampleIndex) {
        double mean = calculateMean(sampleIndex);
        if (mean == 0) return 0;
        return (calculateStdDev(sampleIndex) / mean) * 100.0;
    }

    public double calculateCovariance(int sampleIndex1, int sampleIndex2) {
        List<Double> sample1 = data.get(sampleIndex1);
        List<Double> sample2 = data.get(sampleIndex2);
        if (sample1.size() != sample2.size() || sample1.size() < 2) {
            return 0;
        }
        double mean1 = calculateMean(sampleIndex1);
        double mean2 = calculateMean(sampleIndex2);
        double sum = 0;
        for (int i = 0; i < sample1.size(); i++) {
            sum += (sample1.get(i) - mean1) * (sample2.get(i) - mean2);
        }
        return sum / (sample1.size() - 1);
    }

    public double[] calculateConfidenceInterval(int sampleIndex, double confidenceLevel) {
        List<Double> sample = data.get(sampleIndex);
        if (sample.size() < 2) return new double[]{0, 0};

        double mean = calculateMean(sampleIndex);
        double stdDev = calculateStdDev(sampleIndex);
        int n = sample.size();

        double z = 1.96;
        if (confidenceLevel == 0.99) z = 2.58;
        if (confidenceLevel == 0.90) z = 1.645;

        double marginOfError = z * (stdDev / Math.sqrt(n));

        return new double[]{mean - marginOfError, mean + marginOfError};
    }
public String generateReport(boolean[] options, double confidenceLevel) {
        StringBuilder report = new StringBuilder();
        
        if (options[0]) { // Среднее геометрическое
            report.append("Среднее геометрическое:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(calculateGeometricMean(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[1]) { // Среднее арифметическое
            report.append("Среднее арифметическое:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(calculateMean(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[2]) { // Стандартное отклонение
            report.append("Стандартное отклонение:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(calculateStdDev(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[3]) { // Размах
            report.append("Размах:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(calculateRange(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[4]) { // Количество элементов
            report.append("Количество элементов:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(getCount(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[5]) { // Коэффициент вариации
            report.append("Коэффициент вариации (%):\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(calculateCoefficientOfVariation(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[6]) { // Минимум
            report.append("Минимум:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(getMin(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[7]) { // Максимум
            report.append("Максимум:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(getMax(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[8]) { // Дисперсия
            report.append("Дисперсия:\n");
            for (int i = 0; i < data.size(); i++) {
                report.append(sampleNames.get(i)).append(": ").append(calculateVariance(i)).append("\n");
            }
            report.append("\n");
        }
        
        if (options[9]) { // Доверительный интервал
            report.append("Доверительный интервал (уровень доверия: ").append(confidenceLevel).append("):\n");
            for (int i = 0; i < data.size(); i++) {
                double[] ci = calculateConfidenceInterval(i, confidenceLevel);
                report.append(sampleNames.get(i)).append(": [")
                      .append(String.format("%.2f", ci[0])).append("; ")
                      .append(String.format("%.2f", ci[1])).append("]\n");
            }
            report.append("\n");
        }
        
        return report.toString();
    }
    public List<List<Double>> getData() {
        return data;
    }

    public List<String> getSampleNames() {
        return sampleNames;
    }
}
