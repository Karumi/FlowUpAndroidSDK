/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package com.flowup.reporter;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.flowup.utils.MetricNameUtils;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

import static com.flowup.utils.MetricNameUtils.replaceDashes;
import static com.flowup.utils.MetricNameUtils.replaceDots;

public class MetricsReport {

  private final long reportingTimestamp;
  private final SortedMap<String, Gauge> gauges;
  private final SortedMap<String, Counter> counters;
  private final SortedMap<String, Histogram> histograms;
  private final SortedMap<String, Meter> meters;
  private final SortedMap<String, Timer> timers;

  public MetricsReport(long reportingTimestamp, SortedMap<String, Gauge> gauges,
      SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms,
      SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
    this.reportingTimestamp = reportingTimestamp;
    this.gauges = gauges;
    this.counters = counters;
    this.histograms = histograms;
    this.meters = meters;
    this.timers = timers;
  }

  public long getReportingTimestamp() {
    return reportingTimestamp;
  }

  public SortedMap<String, Gauge> getGauges() {
    return gauges;
  }

  public SortedMap<String, Counter> getCounters() {
    return counters;
  }

  public SortedMap<String, Histogram> getHistograms() {
    return histograms;
  }

  public SortedMap<String, Meter> getMeters() {
    return meters;
  }

  public SortedMap<String, Timer> getTimers() {
    return timers;
  }

  public String getAppPackageName() {
    return replaceDashes(findCrossMetricInfoAtPosition(0));
  }

  public String getInstallationUUID() {
    return replaceDashes(findCrossMetricInfoAtPosition(3));
  }

  public String getDeviceModel() {
    return findCrossMetricInfoAtPosition(4);
  }

  public int getNumberOfCores() {
    try {
      return Integer.valueOf(findCrossMetricInfoAtPosition(5));
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  public String getScreenDensity() {
    return findCrossMetricInfoAtPosition(7);
  }

  public String getScreenSize() {
    return findCrossMetricInfoAtPosition(8);
  }

  private Set<String> getMetricNames() {
    Set<String> metricNames = new HashSet<>();
    metricNames.addAll(gauges.keySet());
    metricNames.addAll(counters.keySet());
    metricNames.addAll(histograms.keySet());
    metricNames.addAll(meters.keySet());
    metricNames.addAll(timers.keySet());
    return metricNames;
  }

  private String findCrossMetricInfoAtPosition(int index) {
    Set<String> metricNames = getMetricNames();
    for (String metricName : metricNames) {
      return MetricNameUtils.findCrossMetricInfoAtPosition(index, metricName);
    }
    return null;
  }
}
