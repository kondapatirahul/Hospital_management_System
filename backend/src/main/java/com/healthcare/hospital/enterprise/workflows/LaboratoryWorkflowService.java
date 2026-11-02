package com.healthcare.hospital.enterprise.workflows;

import com.healthcare.hospital.entity.LabSample;
import com.healthcare.hospital.entity.LabReport;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.stereotype.Service;

/** Laboratory specimen intake, rejection checks, workload and turnaround analytics. */
@Service
public class LaboratoryWorkflowService {
    private static final Set<String> FINAL=Set.of("RELEASED","REJECTED","CANCELLED");
    public List<LabSample> searchSamples(Collection<LabSample> samples,String query) {
        return WorkflowAnalytics.safe(samples).stream().filter(s->WorkflowAnalytics.contains(s.getSampleCode(),query)||
            WorkflowAnalytics.contains(s.getSampleType(),query)||WorkflowAnalytics.contains(s.getStatus(),query)||
            WorkflowAnalytics.contains(s.getCollectedBy(),query)).sorted(Comparator.comparing(LabSample::getCreatedAt,
            Comparator.nullsLast(Comparator.reverseOrder()))).toList();
    }
    public List<LabSample> byStatus(Collection<LabSample> samples,String status) {
        return WorkflowAnalytics.safe(samples).stream().filter(s->WorkflowAnalytics.key(s.getStatus()).equals(WorkflowAnalytics.key(status))).toList();
    }
    public List<LabSample> pendingReceipt(Collection<LabSample> samples) {
        return WorkflowAnalytics.safe(samples).stream().filter(s->Set.of("ORDERED","COLLECTED").contains(WorkflowAnalytics.key(s.getStatus()))).toList();
    }
    public List<LabSample> rejected(Collection<LabSample> samples) {
        return byStatus(samples,"REJECTED");
    }
    public List<String> validateSample(LabSample sample) {
        if(sample==null)return List.of("Sample is required");
        List<String> errors=new ArrayList<>();
        if(sample.getSampleCode()==null||sample.getSampleCode().isBlank())errors.add("Sample code is required");
        if(sample.getLabReport()==null)errors.add("Linked laboratory report is required");
        if(sample.getSampleType()==null||sample.getSampleType().isBlank())errors.add("Sample type is required");
        if("REJECTED".equalsIgnoreCase(sample.getStatus())&&(sample.getRejectionReason()==null||sample.getRejectionReason().isBlank()))errors.add("Rejection reason is required");
        return List.copyOf(errors);
    }
    public Map<String,Long> statusCounts(Collection<LabSample> samples){return WorkflowAnalytics.count(WorkflowAnalytics.safe(samples),LabSample::getStatus);}
    public Map<String,Long> sampleTypeCounts(Collection<LabSample> samples){return WorkflowAnalytics.count(WorkflowAnalytics.safe(samples),LabSample::getSampleType);}
    public Map<String,Long> collectorWorkload(Collection<LabSample> samples) {
        Map<String,Long> result=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        WorkflowAnalytics.safe(samples).stream().filter(s->s.getCollectedBy()!=null&&!s.getCollectedBy().isBlank())
            .forEach(s->result.merge(s.getCollectedBy(),1L,Long::sum));
        return Map.copyOf(result);
    }
    public long receivedAwaitingProcessing(Collection<LabSample> samples) {
        return WorkflowAnalytics.safe(samples).stream().filter(s->"RECEIVED".equalsIgnoreCase(s.getStatus())).count();
    }
    public List<LabSample> agingSamples(Collection<LabSample> samples,LocalDateTime now,int ageHours) {
        if(now==null||ageHours<0)return List.of();
        LocalDateTime cutoff=now.minusHours(ageHours);
        return WorkflowAnalytics.safe(samples).stream().filter(s->!FINAL.contains(WorkflowAnalytics.key(s.getStatus())))
            .filter(s->s.getCreatedAt()!=null&&s.getCreatedAt().isBefore(cutoff)).toList();
    }
    public List<String> duplicateSampleCodes(Collection<LabSample> samples) {
        Map<String,Long> counts=new HashMap<>();
        WorkflowAnalytics.safe(samples).forEach(s->{if(s.getSampleCode()!=null)counts.merge(s.getSampleCode().trim().toLowerCase(Locale.ROOT),1L,Long::sum);});
        return counts.entrySet().stream().filter(e->e.getValue()>1).map(Map.Entry::getKey).sorted().toList();
    }
    public WorkflowSnapshot snapshot(Collection<LabSample> samples,LocalDateTime now) {
        List<LabSample> list=WorkflowAnalytics.safe(samples);
        long active=list.stream().filter(s->!FINAL.contains(WorkflowAnalytics.key(s.getStatus()))).count();
        long attention=agingSamples(list,now,24).size()+rejected(list).size();
        return WorkflowAnalytics.snapshot("LABORATORY",list.stream().map(LabSample::getStatus).toList(),list.size(),active,attention,List.of());
    }
    public Map<String,Object> intakeBoard(Collection<LabSample> samples,LocalDateTime now) {
        List<LabSample> list=WorkflowAnalytics.safe(samples);
        return Map.of("total",list.size(),"pendingReceipt",pendingReceipt(list).size(),
            "awaitingProcessing",receivedAwaitingProcessing(list),"agingOver24Hours",agingSamples(list,now,24).size(),
            "rejected",rejected(list).size(),"byType",sampleTypeCounts(list));
    }
}
