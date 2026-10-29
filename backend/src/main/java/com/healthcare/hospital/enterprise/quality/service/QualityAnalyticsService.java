package com.healthcare.hospital.enterprise.quality.service;

import com.healthcare.hospital.enterprise.quality.entity.QualityAction;
import com.healthcare.hospital.enterprise.quality.entity.QualityReview;
import com.healthcare.hospital.enterprise.quality.repository.QualityActionRepository;
import com.healthcare.hospital.enterprise.quality.repository.QualityReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Quality reporting calculations designed for the department dashboard and audit reviews. */
@Service
@Transactional(readOnly=true)
public class QualityAnalyticsService {
    private static final Set<String> TERMINAL=Set.of("CLOSED","CANCELLED");
    private final QualityReviewRepository reviews;
    private final QualityActionRepository actions;
    private final QualityWorkflowPolicy policy;

    public QualityAnalyticsService(QualityReviewRepository reviews,QualityActionRepository actions,
                                   QualityWorkflowPolicy policy) {
        this.reviews=reviews;
        this.actions=actions;
        this.policy=policy;
    }

    public Map<String,Object> dashboard() {
        List<QualityReview> data=reviews.findTop200ByOrderByUpdatedAtDesc();
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("generatedOn",LocalDate.now().toString());
        result.put("populationSize",data.size());
        result.put("activeCases",data.stream().filter(r->!terminal(r)).count());
        result.put("closedCases",data.stream().filter(r->"CLOSED".equals(normalize(r.getStatus()))).count());
        result.put("cancelledCases",data.stream().filter(r->"CANCELLED".equals(normalize(r.getStatus()))).count());
        result.put("overdueCases",data.stream().filter(policy::overdue).count());
        result.put("criticalActiveCases",data.stream().filter(this::criticalActive).count());
        result.put("sentinelCases",data.stream().filter(QualityReview::isSentinelEvent).count());
        result.put("patientSafetyCases",data.stream().filter(QualityReview::isPatientSafetyImpact).count());
        result.put("awaitingVerification",data.stream().filter(r->"PENDING_VERIFICATION".equals(normalize(r.getStatus()))).count());
        result.put("severityDistribution",distribution(data,QualityReview::getSeverity));
        result.put("statusDistribution",distribution(data,QualityReview::getStatus));
        result.put("categoryDistribution",distribution(data,QualityReview::getCategory));
        result.put("aging",aging(data));
        result.put("monthlyTrend",monthlyTrend(data,6));
        result.put("departmentWorkload",departmentWorkload(data));
        result.put("actionSummary",actionSummary());
        result.put("sla",sla(data));
        return result;
    }

    public Map<String,Long> distribution(List<QualityReview> data,java.util.function.Function<QualityReview,String> field) {
        Map<String,Long> result=new TreeMap<>();
        for(QualityReview review:data) {
            String key=normalize(field.apply(review));
            if(key.isBlank()) key="UNASSIGNED";
            result.merge(key,1L,Long::sum);
        }
        return result;
    }

    public Map<String,Long> aging(List<QualityReview> data) {
        Map<String,Long> result=new LinkedHashMap<>();
        result.put("0-2 days",0L);
        result.put("3-7 days",0L);
        result.put("8-14 days",0L);
        result.put("15-30 days",0L);
        result.put("31+ days",0L);
        LocalDate today=LocalDate.now();
        for(QualityReview review:data) {
            if(terminal(review)||review.getReportedAt()==null) continue;
            long age=Math.max(0,ChronoUnit.DAYS.between(review.getReportedAt().toLocalDate(),today));
            if(age<=2) increment(result,"0-2 days");
            else if(age<=7) increment(result,"3-7 days");
            else if(age<=14) increment(result,"8-14 days");
            else if(age<=30) increment(result,"15-30 days");
            else increment(result,"31+ days");
        }
        return result;
    }

    public List<Map<String,Object>> monthlyTrend(List<QualityReview> data,int months) {
        int window=Math.max(1,Math.min(months,24));
        YearMonth current=YearMonth.now();
        List<Map<String,Object>> rows=new ArrayList<>();
        for(int offset=window-1;offset>=0;offset--) {
            YearMonth month=current.minusMonths(offset);
            long opened=data.stream().filter(r->r.getReportedAt()!=null&&YearMonth.from(r.getReportedAt()).equals(month)).count();
            long closed=data.stream().filter(r->r.getClosedAt()!=null&&YearMonth.from(r.getClosedAt()).equals(month)
                &&"CLOSED".equals(normalize(r.getStatus()))).count();
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("month",month.toString());
            row.put("opened",opened);
            row.put("closed",closed);
            rows.add(row);
        }
        return rows;
    }

    public List<Map<String,Object>> departmentWorkload(List<QualityReview> data) {
        Map<String,List<QualityReview>> groups=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(QualityReview review:data) {
            if(terminal(review)) continue;
            String department=review.getDepartment()==null||review.getDepartment().isBlank()?"Unassigned":review.getDepartment().trim();
            groups.computeIfAbsent(department,key->new ArrayList<>()).add(review);
        }
        List<Map<String,Object>> result=new ArrayList<>();
        for(Map.Entry<String,List<QualityReview>> entry:groups.entrySet()) {
            List<QualityReview> cases=entry.getValue();
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("department",entry.getKey());
            row.put("active",cases.size());
            row.put("overdue",cases.stream().filter(policy::overdue).count());
            row.put("critical",cases.stream().filter(this::criticalActive).count());
            row.put("unassigned",cases.stream().filter(r->r.getOwnerUsername()==null||r.getOwnerUsername().isBlank()).count());
            row.put("oldestOpenDays",cases.stream().filter(r->r.getReportedAt()!=null)
                .mapToLong(r->Math.max(0,ChronoUnit.DAYS.between(r.getReportedAt().toLocalDate(),LocalDate.now())))
                .max().orElse(0));
            result.add(row);
        }
        result.sort(Comparator.comparingLong(row->-((Number)row.get("overdue")).longValue()));
        return result;
    }

    public Map<String,Object> actionSummary() {
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("open",actions.countByStatus("OPEN"));
        result.put("inProgress",actions.countByStatus("IN_PROGRESS"));
        result.put("blocked",actions.countByStatus("BLOCKED"));
        result.put("completedAwaitingVerification",actions.countByStatus("COMPLETED"));
        result.put("verified",actions.countByStatus("VERIFIED"));
        result.put("cancelled",actions.countByStatus("CANCELLED"));
        List<QualityAction> overdue=actions.findByStatusAndDueDateBeforeOrderByDueDateAsc("OPEN",LocalDate.now());
        result.put("overdueOpen",overdue.size());
        result.put("overdueReferences",overdue.stream().limit(10).map(QualityAction::getActionReference).toList());
        return result;
    }

    public Map<String,Object> sla(List<QualityReview> data) {
        long eligible=data.stream().filter(r->r.getDueDate()!=null).count();
        long onTime=data.stream().filter(r->r.getDueDate()!=null&&
            ("CLOSED".equals(normalize(r.getStatus()))&&r.getClosedAt()!=null&&
             !r.getClosedAt().toLocalDate().isAfter(r.getDueDate()) ||
             !terminal(r)&&!LocalDate.now().isAfter(r.getDueDate()))).count();
        long overdue=data.stream().filter(policy::overdue).count();
        double percentage=eligible==0?100.0:Math.round((onTime*10000.0)/eligible)/100.0;
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("casesWithDueDate",eligible);
        result.put("onTrackOrClosedOnTime",onTime);
        result.put("overdue",overdue);
        result.put("onTimePercentage",percentage);
        result.put("calculationNote","Computed from the most recent 200 cases returned by the register.");
        return result;
    }

    public List<Map<String,Object>> ownerWorkload() {
        List<QualityReview> data=reviews.findTop200ByOrderByUpdatedAtDesc();
        Map<String,List<QualityReview>> groups=new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for(QualityReview review:data) {
            if(terminal(review)) continue;
            String owner=review.getOwnerUsername()==null||review.getOwnerUsername().isBlank()?"Unassigned":review.getOwnerUsername().trim();
            groups.computeIfAbsent(owner,key->new ArrayList<>()).add(review);
        }
        List<Map<String,Object>> rows=new ArrayList<>();
        groups.forEach((owner,cases)->{
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("owner",owner);
            row.put("activeCases",cases.size());
            row.put("overdue",cases.stream().filter(policy::overdue).count());
            row.put("critical",cases.stream().filter(this::criticalActive).count());
            row.put("oldestDueDate",cases.stream().map(QualityReview::getDueDate).filter(java.util.Objects::nonNull).min(LocalDate::compareTo).orElse(null));
            rows.add(row);
        });
        rows.sort(Comparator.comparingLong(row->-((Number)row.get("overdue")).longValue()));
        return rows;
    }

    public List<Map<String,Object>> highRiskQueue() {
        return reviews.findTop200ByOrderByUpdatedAtDesc().stream()
            .filter(r->!terminal(r))
            .filter(r->criticalActive(r)||policy.overdue(r)||r.isSentinelEvent())
            .sorted(Comparator.comparingInt(this::riskScore).reversed())
            .limit(50)
            .map(this::riskRow)
            .toList();
    }

    private Map<String,Object> riskRow(QualityReview review) {
        Map<String,Object> row=new LinkedHashMap<>();
        row.put("id",review.getId());
        row.put("reference",review.getCaseReference());
        row.put("title",review.getTitle());
        row.put("severity",review.getSeverity());
        row.put("status",review.getStatus());
        row.put("owner",review.getOwnerUsername());
        row.put("dueDate",review.getDueDate());
        row.put("overdue",policy.overdue(review));
        row.put("sentinelEvent",review.isSentinelEvent());
        row.put("patientSafetyImpact",review.isPatientSafetyImpact());
        row.put("riskScore",riskScore(review));
        return row;
    }

    private int riskScore(QualityReview review) {
        int score=switch(normalize(review.getSeverity())) {
            case "CRITICAL" -> 100;
            case "HIGH" -> 70;
            case "MODERATE" -> 40;
            default -> 10;
        };
        if(policy.overdue(review)) score+=25;
        if(review.isSentinelEvent()) score+=50;
        if(review.isPatientSafetyImpact()) score+=15;
        if(review.getOwnerUsername()==null||review.getOwnerUsername().isBlank()) score+=10;
        return score;
    }

    private boolean criticalActive(QualityReview review) {
        return "CRITICAL".equals(normalize(review.getSeverity()))&&!terminal(review);
    }

    private boolean terminal(QualityReview review) {
        return TERMINAL.contains(normalize(review.getStatus()));
    }

    private String normalize(String value) {
        return value==null?"":value.trim().toUpperCase(java.util.Locale.ROOT);
    }

    private void increment(Map<String,Long> values,String key) {
        values.put(key,values.getOrDefault(key,0L)+1L);
    }
}
