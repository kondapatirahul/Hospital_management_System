package com.healthcare.hospital.enterprise.reception.commandcenter;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
/** Immutable snapshot of operational counts; unavailable values remain explicitly absent. */
public final class ReceptionDashboardSignalSnapshot{
 private final Map<String,Double> values; private final Instant capturedAt;
 public ReceptionDashboardSignalSnapshot(Map<String,Double> values,Instant capturedAt){
  Map<String,Double> copy=new LinkedHashMap<>();
  if(values!=null)values.forEach((k,v)->{if(k!=null&&!k.isBlank()&&v!=null&&Double.isFinite(v))copy.put(k,v);});
  this.values=Collections.unmodifiableMap(copy);this.capturedAt=capturedAt==null?Instant.now():capturedAt;
 }
 public static ReceptionDashboardSignalSnapshot empty(){return new ReceptionDashboardSignalSnapshot(Map.of(),Instant.now());}
 public boolean contains(String m){return values.containsKey(m);}
 public double value(String m){return values.getOrDefault(m,0.0);}
 public Map<String,Double> values(){return values;}
 public Instant capturedAt(){return capturedAt;}
}
