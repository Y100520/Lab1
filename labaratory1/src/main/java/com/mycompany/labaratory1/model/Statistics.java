/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.labaratory1.model;

import java.util.*;

public class Statistics {
    private final DataModel dataModel;

    public Statistics(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public double calculateGeometricMean(List<Double> sample) {
        if (sample == null || sample.isEmpty()) {
            return 0.0;
        }
        double product = 1.0;
        for (double value : sample) {
            if (value <= 0) {
                return 0.0;
            }
            product *= value;
        }
        return Math.pow(product, 1.0 / sample.size());
    }

    public double calculateMean(List<Double> sample) {
        if (sample == null || sample.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (double value : sample) {
            sum += value;
        }
        return sum / sample.size();
    }

    public double calculateVariance(List<Double> sample) {
        if (sample == null || sample.size() < 2) {
            return 0.0;
        }
        double mean = calculateMean(sample);
        double sumOfSquares = 0.0;
        for (double value : sample) {
            sumOfSquares += Math.pow(value - mean, 2);
        }
        return sumOfSquares / (sample.size() - 1);
    }

    public double calculateStdDev(List<Double> sample) {
        return Math.sqrt(calculateVariance(sample));
    }

    public double getMin(List<Double> sample) {
        if (sample == null || sample.isEmpty()) return 0.0;
        return Collections.min(sample);
    }

    public double getMax(List<Double> sample) {
        if (sample == null || sample.isEmpty()) return 0.0;
        return Collections.max(sample);
    }

    public double calculateRange(List<Double> sample) {
        if (sample == null || sample.isEmpty()) return 0.0;
        return getMax(sample) - getMin(sample);
    }

    public double calculateCovariance(List<Double> sample1, List<Double> sample2) {
        if (sample1 == null || sample2 == null || sample1.size() != sample2.size() || sample1.size() < 2) {
            return 0.0;
        }

        double mean1 = calculateMean(sample1);
        double mean2 = calculateMean(sample2);
        double sum = 0.0;

        for (int i = 0; i < sample1.size(); i++) {
            sum += (sample1.get(i) - mean1) * (sample2.get(i) - mean2);
        }

        return sum / (sample1.size() - 1);
    }

    public long getCount(List<Double> sample) {
        if (sample == null) return 0;
        return sample.size();
    }

    public double calculateCoefficientOfVariation(List<Double> sample) {
        double mean = calculateMean(sample);
        if (mean == 0) {
            return 0.0;
        }
        double stdDev = calculateStdDev(sample);
        return (stdDev / Math.abs(mean)) * 100.0;
    }

    public double[] calculateConfidenceInterval(List<Double> sample, double confidenceLevel) {
        if (sample == null || sample.size() < 2) {
            return new double[]{0, 0};
        }

        double mean = calculateMean(sample);
        double stdDev = calculateStdDev(sample);
        long n = getCount(sample);

        double z_score;
        if (confidenceLevel == 0.95) {
            z_score = 1.96;
        } else if (confidenceLevel == 0.99) {
            z_score = 2.576;
        } else {
            z_score = 1.645;
        }

        double marginOfError = z_score * (stdDev / Math.sqrt(n));

        return new double[]{mean - marginOfError, mean + marginOfError};
    }

    public String generateReport(boolean[] options, double confidenceLevel) {
        StringBuilder report = new StringBuilder();
        List<List<Double>> allData = dataModel.getData();
        List<String> sampleNames = dataModel.getSampleNames();

        for (int i = 0; i < allData.size(); i++) {
            List<Double> currentSample = allData.get(i);
            report.append("Выборка: ").append(sampleNames.get(i));

            if (options[0]) report.append(String.format("Среднее геометрическое: %.4f\n", calculateGeometricMean(currentSample)));
            if (options[1]) report.append(String.format("Среднее арифметическое: %.4f\n", calculateMean(currentSample)));
            if (options[2]) report.append(String.format("Стандартное отклонение: %.4f\n", calculateStdDev(currentSample)));
            if (options[3]) report.append(String.format("Размах: %.4f\n", calculateRange(currentSample)));
            if (options[4]) report.append(String.format("Количество элементов: %d\n", getCount(currentSample)));
            if (options[5]) report.append(String.format("Коэффициент вариации: %.2f%%\n", calculateCoefficientOfVariation(currentSample)));
            if (options[6]) report.append(String.format("Минимум: %.4f\n", getMin(currentSample)));
            if (options[7]) report.append(String.format("Максимум: %.4f\n", getMax(currentSample)));
            if (options[8]) report.append(String.format("Дисперсия: %.4f\n", calculateVariance(currentSample)));
            if (options[9]) {
                double[] ci = calculateConfidenceInterval(currentSample, confidenceLevel);
                report.append(String.format("Доверительный интервал (%.0f%%): [%.4f, %.4f]\n", confidenceLevel * 100, ci[0], ci[1]));
            }
            report.append("\n");
        }

        if (options[10]) {
            report.append("Ковариации:\n");
            for (int i = 0; i < allData.size(); i++) {
                for (int j = i; j < allData.size(); j++) {
                    double cov = calculateCovariance(allData.get(i), allData.get(j));
                    report.append(String.format("Cov(%s, %s): %.4f\n", sampleNames.get(i), sampleNames.get(j), cov));
                }
            }
        }

        return report.toString();
    }
}