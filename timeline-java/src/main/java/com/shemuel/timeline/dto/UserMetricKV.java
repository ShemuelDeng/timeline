package com.shemuel.timeline.dto;

public class UserMetricKV {
    private String metricKey;
    private Long metricValue;

    public String getMetricKey() { return metricKey; }
    public void setMetricKey(String metricKey) { this.metricKey = metricKey; }

    public Long getMetricValue() { return metricValue; }
    public void setMetricValue(Long metricValue) { this.metricValue = metricValue; }
}
