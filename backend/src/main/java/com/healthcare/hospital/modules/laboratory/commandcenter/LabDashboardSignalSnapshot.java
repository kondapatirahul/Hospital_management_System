package com.healthcare.hospital.modules.laboratory.commandcenter;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
/** Immutable laboratory signal snapshot. Missing values remain missing, not zero. */
public final class LabDashboardSignalSnapshot{
 private final Map<String,Double> values;
 private final Instant capturedAt;
 public LabDashboardSignalSnapshot(Map<String,Double> values,Instant capturedAt){
  Map<String,Double> copy=new LinkedHashMap<>();
  if(values!=null)values.forEach((key,value)->{
   if(key!=null&&!key.isBlank()&&value!=null&&Double.isFinite(value))copy.put(key,value);
  });
  this.values=Collections.unmodifiableMap(copy);
  this.capturedAt=capturedAt==null?Instant.now():capturedAt;
 }
 public static LabDashboardSignalSnapshot empty(){return new LabDashboardSignalSnapshot(Map.of(),Instant.now());}
 public boolean contains(String metric){return values.containsKey(metric);}
 public double value(String metric){return values.getOrDefault(metric,0.0);}
 public Map<String,Double> values(){return values;}
 public Instant capturedAt(){return capturedAt;}
 public int size(){return values.size();}
}
