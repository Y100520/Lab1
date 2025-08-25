/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.labaratory1.model;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.util.*;

public class Statistics {
    private final DataModel dataModel;

    public Statistics(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public List<StatisticsResult> calculateAll() {
        List<StatisticsResult> results = new ArrayList<>();
        List<List<Double>> data = dataModel.getData();
        List<String> sampleNames = dataModel.getSampleNames();

        for (int i = 0; i < data.size(); i++) {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            data.get(i).forEach(stats::addValue);

            double geometricMean = stats.getGeometricMean();
            double mean = stats.getMean();
            double stdDev = stats.getStandardDeviation();
            double range = stats.getMax() - stats.getMin();
            double count = stats.getN();
            double coefficientOfVariation = (stdDev / mean);
            double min = stats.getMin();
            double max = stats.getMax();
            double variance = stats.getVariance();

            results.add(new StatisticsResult(
                sampleNames.get(i), geometricMean, mean, stdDev, range, count, 
                coefficientOfVariation, min, max, variance
            ));
        }
        return results;
    }

    public List<Double> calculateCovariance() {
        Covariance cov = new Covariance();
        List<Double> covariances = new ArrayList<>();
        List<List<Double>> data = dataModel.getData();

        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.size(); j++) {
                double[] arr1 = data.get(i).stream().mapToDouble(Double::doubleValue).toArray();
                double[] arr2 = data.get(j).stream().mapToDouble(Double::doubleValue).toArray();
                covariances.add(cov.covariance(arr1, arr2));
            }
        }
        return covariances;
    }

    public double[] calculateConfidenceInterval(double confidenceLevel, int sampleIndex) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        dataModel.getData().get(sampleIndex).forEach(stats::addValue);

        double mean = stats.getMean();
        double stdDev = stats.getStandardDeviation();
        double n = stats.getN();
        TDistribution tDist = new TDistribution(n - 1);
        double t = tDist.inverseCumulativeProbability(1 - (1 - confidenceLevel) / 2);
        double margin = t * (stdDev / Math.sqrt(n));

        return new double[]{mean - margin, mean + margin};
    }

    public String generateReport(boolean[] options, double confidenceLevel) {
        List<StatisticsResult> results = calculateAll();
        List<Double> covariances = calculateCovariance();
        StringBuilder report = new StringBuilder();

        for (StatisticsResult result : results) {
            report.append("Выборка: ").append(result.getSampleName()).append("\n");
            if (options[0]) report.append("Среднее геом.: ").append(result.getGeometricMean()).append("\n");
            if (options[1]) report.append("Среднее арифм.: ").append(result.getMean()).append("\n");
            if (options[2]) report.append("Стандартное отклонение: ").append(result.getStdDev()).append("\n");
            if (options[3]) report.append("Размах: ").append(result.getRange()).append("\n");
            if (options[4]) report.append("Количество элементов: ").append(result.getCount()).append("\n");
            if (options[5]) report.append("Коэффициент вариации: ").append(result.getCoefficientOfVariation()).append("%\n");
            if (options[6]) report.append("Минимум: ").append(result.getMin()).append("\n");
            if (options[7]) report.append("Максимум: ").append(result.getMax()).append("\n");
            if (options[8]) report.append("Дисперсия: ").append(result.getVariance()).append("\n");
            if (options[9]) {
                double[] ci = calculateConfidenceInterval(confidenceLevel, results.indexOf(result));
                report.append("Доверительный интервал: [").append(ci[0]).append(", ").append(ci[1]).append("]\n");
            }
            report.append("\n");
        }

        if (options[10]) {
            report.append("Ковариации:\n");
            int covIndex = 0;
            for (int i = 0; i < dataModel.getData().size(); i++) {
                for (int j = 0; j < dataModel.getData().size(); j++) {
                    report.append(dataModel.getSampleNames().get(i))
                          .append(" и ")
                          .append(dataModel.getSampleNames().get(j))
                          .append(": ")
                          .append(covariances.get(covIndex++))
                          .append("\n");
                }
            }
        }

        return report.toString();
    }
}
