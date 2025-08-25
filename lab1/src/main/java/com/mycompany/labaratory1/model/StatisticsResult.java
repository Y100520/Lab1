package com.mycompany.labaratory1.model;

public class StatisticsResult {
    private final String sampleName;
    private final double geometricMean;
    private final double mean;
    private final double stdDev;
    private final double range;
    private final double count;
    private final double coefficientOfVariation;
    private final double min;
    private final double max;
    private final double variance;

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

    public String getSampleName() { return sampleName; }
    public double getGeometricMean() { return geometricMean; }
    public double getMean() { return mean; }
    public double getStdDev() { return stdDev; }
    public double getRange() { return range; }
    public double getCount() { return count; }
    public double getCoefficientOfVariation() { return coefficientOfVariation; }
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getVariance() { return variance; }
}
