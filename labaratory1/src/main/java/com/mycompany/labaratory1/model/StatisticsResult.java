/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.labaratory1.model;

public class StatisticsResult {
    private String sampleName;
    private double geometricMean;
    private double mean;
    private double stdDev;
    private double range;
    private double count;
    private double coefficientOfVariation;
    private double min;
    private double max;
    private double variance;

    public StatisticsResult() {
    }

    public StatisticsResult(String sampleName, double geometricMean, double mean,
                            double stdDev, double range, double count,
                            double coefficientOfVariation, double min,
                            double max, double variance) {
        this.sampleName = sampleName;
        this.geometricMean = geometricMean;
        this.mean = mean;
        this.stdDev = stdDev;
        this.range = range;
        this.count = count;
        this.coefficientOfVariation = coefficientOfVariation;
        this.min = min;
        this.max = max;
        this.variance = variance;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public double getGeometricMean() {
        return geometricMean;
    }

    public void setGeometricMean(double geometricMean) {
        this.geometricMean = geometricMean;
    }

    public double getMean() {
        return mean;
    }

    public void setMean(double mean) {
        this.mean = mean;
    }

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public double getCoefficientOfVariation() {
        return coefficientOfVariation;
    }

    public void setCoefficientOfVariation(double coefficientOfVariation) {
        this.coefficientOfVariation = coefficientOfVariation;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getVariance() {
        return variance;
    }

    public void setVariance(double variance) {
        this.variance = variance;
    }
}