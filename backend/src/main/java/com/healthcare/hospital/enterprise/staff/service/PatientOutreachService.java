package com.healthcare.hospital.enterprise.staff.service;

import com.healthcare.hospital.enterprise.staff.entity.DischargeChecklist;
import com.healthcare.hospital.enterprise.staff.entity.PatientOutreach;
import com.healthcare.hospital.enterprise.staff.repository.DischargeChecklistRepository;
import com.healthcare.hospital.enterprise.staff.repository.PatientOutreachRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** Enforces outreach lifecycle rules and records safety escalations. */
@Service
@Transactional
public class PatientOutreachService {
    private static final Set<String> CHANNELS=Set.of("PHONE","SMS","PORTAL","VIDEO","CAREGIVER","IN_PERSON");
    private static final Set<String> STATUSES=Set.of("OPEN","IN_PROGRESS","WAITING_FOR_PATIENT","WAITING_FOR_CLINICIAN","CLOSED","CANCELLED");
    private static final Set<String> OUTCOMES=Set.of("NOT_ATTEMPTED","REACHED","NO_ANSWER","BUSY","WRONG_NUMBER","CALLBACK_REQUESTED","CAREGIVER_REACHED","PORTAL_RESPONSE","ESCALATED","DECLINED");
    private static final Set<String> PRIORITIES=Set.of("LOW","NORMAL","HIGH","URGENT");
    private static final Set<String> ESCALATIONS=Set.of("NONE","ROUTINE","PRIORITY","URGENT","EMERGENCY");
    private static final Set<String> TEAMS=Set.of("Clinical","Nursing","Pharmacy","Reception","Home Care","Social Work","Care Coordination","Other");
    private final PatientOutreachRepository repository;
    private final DischargeChecklistRepository checklists;

    public PatientOutreachService(PatientOutreachRepository repository, DischargeChecklistRepository checklists){this.repository=repository;this.checklists=checklists;}

    @Transactional(readOnly=true) public List<PatientOutreach> all(){return repository.findTop300ByOrderByUpdatedAtDesc();}
    @Transactional(readOnly=true) public PatientOutreach get(long id){return require(id);}
    @Transactional(readOnly=true) public List<PatientOutreach> patientHistory(String reference){return repository.findByPatientReferenceIgnoreCaseOrderByCreatedAtDesc(required(reference,"Patient reference",60));}
    @Transactional(readOnly=true) public List<PatientOutreach> checklistHistory(long id){if(!checklists.existsById(id))throw new NoSuchElementException("Discharge checklist not found.");return repository.findByDischargeChecklistIdOrderByCreatedAtDesc(id);}

    @Transactional(readOnly=true)
    public List<PatientOutreach> search(String status,String team,String priority,String escalation,LocalDate from,LocalDate to,String term){
        if(from!=null&&to!=null&&from.isAfter(to))throw new IllegalArgumentException("Start date must be on or before end date.");
        return repository.search(filter(status,STATUSES,"status"),filterTeam(team),filter(priority,PRIORITIES,"priority"),filter(escalation,ESCALATIONS,"escalation"),from,to,optional(term,120));
    }

    public PatientOutreach create(long checklistId,String patientReference,String patientName,String purpose,String channel,String priority,String team,String assignee,LocalDate nextDate,String contactNumber,String preferredWindow,boolean consent,boolean interpreter,String actor){
        DischargeChecklist checklist=checklists.findById(checklistId).orElseThrow(()->new NoSuchElementException("Discharge checklist not found."));
        if(!"COMPLETED".equalsIgnoreCase(checklist.getStatus()))throw new IllegalStateException("Outreach can only be linked to a completed discharge-readiness checklist.");
        PatientOutreach item=new PatientOutreach();item.setReferenceCode(nextReference());item.setDischargeChecklist(checklist);
        item.setPatientReference(required(patientReference,"Patient reference",60));item.setPatientName(required(patientName,"Patient name",140));
        item.setPurpose(required(purpose,"Contact purpose",180));item.setChannel(value(channel,CHANNELS,"contact channel"));item.setPriority(value(priority,PRIORITIES,"priority"));
        item.setAssignedTeam(team(team));item.setAssignedTo(optional(assignee,100));item.setNextContactDate(nextDate==null?LocalDate.now():nextDate);
        item.setContactNumber(optional(contactNumber,1000));item.setPreferredWindow(optional(preferredWindow,120));item.setConsentConfirmed(consent);item.setInterpreterRequired(interpreter);
        item.setCreatedBy(required(actor,"Creator",100));return repository.save(item);
    }

    public PatientOutreach recordAttempt(long id,String outcome,String summary,String symptoms,String medication,String wound,String mobility,String social,String questions,String advice,LocalDate nextDate,boolean identity,boolean caregiver,boolean appointment,boolean warningSigns,boolean redFlag,boolean breathing,boolean chestPain,boolean neurological,boolean pain,boolean medicationAccess,String actor){
        PatientOutreach item=require(id);ensureOpen(item);String result=value(outcome,OUTCOMES,"contact outcome");
        if(!item.isConsentConfirmed()&&!Set.of("NO_ANSWER","BUSY","WRONG_NUMBER").contains(result))throw new IllegalStateException("Document contact consent before recording a substantive conversation.");
        item.setOutcome(result);item.setConversationSummary(optional(summary,3000));item.setReportedSymptoms(optional(symptoms,2500));item.setMedicationConcerns(optional(medication,2500));item.setWoundConcerns(optional(wound,2500));item.setMobilityNotes(optional(mobility,2500));item.setSocialSupportNotes(optional(social,2500));item.setPatientQuestions(optional(questions,2500));item.setAdviceProvided(optional(advice,2500));
        item.setIdentityVerified(identity);item.setCaregiverContacted(caregiver);item.setAppointmentConfirmed(appointment);item.setWarningSignsUnderstood(warningSigns);item.setBreathingConcern(breathing);item.setChestPainConcern(chestPain);item.setNeurologicalConcern(neurological);item.setUncontrolledPain(pain);item.setMedicationAccessConcern(medicationAccess);item.setRedFlagSymptoms(redFlag||breathing||chestPain||neurological||pain);
        item.setAttemptCount(item.getAttemptCount()+1);item.setLastAttemptAt(LocalDateTime.now());item.setLastUpdatedBy(required(actor,"Actor",100));
        if(item.isRedFlagSymptoms()){item.setEscalationLevel("EMERGENCY");item.setPriority("URGENT");item.setClinicianHandoffRequired(true);item.setSupervisorReviewRequired(true);item.setStatus("WAITING_FOR_CLINICIAN");item.setNextContactDate(LocalDate.now());item.setEscalationNotes(append(item.getEscalationNotes(),LocalDateTime.now()+" — red-flag symptoms reported; urgent clinical handoff required."));}
        else if(medicationAccess){item.setEscalationLevel("PRIORITY");item.setPriority("HIGH");item.setClinicianHandoffRequired(true);item.setStatus("WAITING_FOR_CLINICIAN");item.setNextContactDate(LocalDate.now());}
        else if(Set.of("NO_ANSWER","BUSY","CALLBACK_REQUESTED").contains(result)){item.setStatus("WAITING_FOR_PATIENT");item.setNextContactDate(nextDate==null?LocalDate.now().plusDays(1):nextDate);}
        else{item.setStatus("IN_PROGRESS");item.setNextContactDate(nextDate);}
        return repository.save(item);
    }

    public PatientOutreach assign(long id,String teamName,String assignee,String priority,String actor){PatientOutreach item=require(id);ensureOpen(item);item.setAssignedTeam(team(teamName));item.setAssignedTo(optional(assignee,100));item.setPriority(value(priority,PRIORITIES,"priority"));item.setLastUpdatedBy(required(actor,"Actor",100));return repository.save(item);}

    public PatientOutreach escalate(long id,String level,String reason,boolean clinician,boolean supervisor,String actor){PatientOutreach item=require(id);ensureOpen(item);String escalation=value(level,ESCALATIONS,"escalation level");if("NONE".equals(escalation))throw new IllegalArgumentException("Choose an escalation level.");String note=required(reason,"Escalation reason",2500);item.setEscalationLevel(escalation);item.setEscalationNotes(append(item.getEscalationNotes(),LocalDateTime.now()+" — "+actor+": "+note));item.setClinicianHandoffRequired(clinician||Set.of("URGENT","EMERGENCY").contains(escalation));item.setSupervisorReviewRequired(supervisor||Set.of("URGENT","EMERGENCY").contains(escalation));item.setPriority(Set.of("URGENT","EMERGENCY").contains(escalation)?"URGENT":"HIGH");item.setStatus("WAITING_FOR_CLINICIAN");item.setLastUpdatedBy(required(actor,"Actor",100));if("EMERGENCY".equals(escalation)){item.setRedFlagSymptoms(true);item.setNextContactDate(LocalDate.now());}return repository.save(item);}

    public PatientOutreach acknowledgeHandoff(long id,String note,String actor){PatientOutreach item=require(id);if(!item.isClinicianHandoffRequired()&&!item.isSupervisorReviewRequired())throw new IllegalStateException("No handoff is awaiting acknowledgement.");item.setHandoffNotes(append(item.getHandoffNotes(),LocalDateTime.now()+" — acknowledged by "+actor+": "+required(note,"Handoff note",2500)));item.setClinicianHandoffRequired(false);item.setSupervisorReviewRequired(false);item.setLastUpdatedBy(required(actor,"Actor",100));return repository.save(item);}

    public PatientOutreach resolveSafety(long id,String note,String actor){PatientOutreach item=require(id);ensureOpen(item);if(item.isClinicianHandoffRequired()||item.isSupervisorReviewRequired())throw new IllegalStateException("Acknowledge the handoff before resolving safety flags.");if(!item.isRedFlagSymptoms()&&!item.isMedicationAccessConcern())throw new IllegalStateException("No active safety flags need resolution.");item.setEscalationNotes(append(item.getEscalationNotes(),LocalDateTime.now()+" — safety review resolved by "+actor+": "+required(note,"Resolution note",2500)));item.setRedFlagSymptoms(false);item.setBreathingConcern(false);item.setChestPainConcern(false);item.setNeurologicalConcern(false);item.setUncontrolledPain(false);item.setMedicationAccessConcern(false);item.setEscalationLevel("ROUTINE");item.setPriority("NORMAL");item.setStatus("IN_PROGRESS");item.setNextContactDate(LocalDate.now());item.setLastUpdatedBy(required(actor,"Actor",100));return repository.save(item);}

    public PatientOutreach close(long id,String notes,String actor){PatientOutreach item=require(id);ensureOpen(item);if(item.isRedFlagSymptoms())throw new IllegalStateException("Active red-flag symptoms must be clinically resolved before closure.");if(item.isClinicianHandoffRequired()||item.isSupervisorReviewRequired())throw new IllegalStateException("Pending handoff must be acknowledged before closure.");if(item.getAttemptCount()==0)throw new IllegalStateException("Record at least one contact attempt before closure.");if("NOT_ATTEMPTED".equals(item.getOutcome()))throw new IllegalStateException("Select the latest contact outcome.");item.setClosureNotes(required(notes,"Closure summary",2500));item.setClosedBy(required(actor,"Actor",100));item.setClosedAt(LocalDateTime.now());item.setStatus("CLOSED");item.setNextContactDate(null);return repository.save(item);}

    public PatientOutreach reopen(long id,String reason,String actor){PatientOutreach item=require(id);if(!"CLOSED".equals(item.getStatus()))throw new IllegalStateException("Only closed records can be reopened.");item.setClosureNotes(append(item.getClosureNotes(),"Reopened "+LocalDateTime.now()+" by "+actor+": "+required(reason,"Reason",1000)));item.setStatus("IN_PROGRESS");item.setClosedAt(null);item.setClosedBy(null);item.setNextContactDate(LocalDate.now());return repository.save(item);}
    public PatientOutreach cancel(long id,String reason,String actor){PatientOutreach item=require(id);ensureOpen(item);item.setClosureNotes(append(item.getClosureNotes(),"Cancelled "+LocalDateTime.now()+" by "+actor+": "+required(reason,"Cancellation reason",1000)));item.setStatus("CANCELLED");item.setClosedAt(LocalDateTime.now());item.setClosedBy(actor);item.setNextContactDate(null);return repository.save(item);}

    @Transactional(readOnly=true) public List<PatientOutreach> overdue(){return repository.overdue(LocalDate.now());}
    @Transactional(readOnly=true) public List<PatientOutreach> safetyQueue(){return repository.safetyQueue();}
    @Transactional(readOnly=true) public Map<String,Long> metrics(){Map<String,Long> m=new LinkedHashMap<>();m.put("open",repository.countByStatus("OPEN"));m.put("inProgress",repository.countByStatus("IN_PROGRESS"));m.put("waiting",repository.countByStatus("WAITING_FOR_PATIENT")+repository.countByStatus("WAITING_FOR_CLINICIAN"));m.put("overdue",(long)overdue().size());m.put("safety",(long)safetyQueue().size());m.put("dueToday",repository.countByNextContactDateAndStatusNot(LocalDate.now(),"CLOSED"));m.put("total",repository.count());return m;}
    @Transactional(readOnly=true) public Map<String,Long> statusCounts(){return map(repository.statusCounts());}
    @Transactional(readOnly=true) public Map<String,Long> outcomeCounts(){return map(repository.outcomeCounts());}
    @Transactional(readOnly=true) public Map<String,Long> teamWorkload(){return map(repository.workloadByTeam());}
    @Transactional(readOnly=true) public String exportCsv(List<PatientOutreach> items){StringBuilder b=new StringBuilder("Reference,Patient Reference,Patient Name,Checklist,Channel,Status,Outcome,Priority,Escalation,Team,Assignee,Attempts,Next Contact,Red Flags\r\n");for(PatientOutreach o:items)csvRow(b,o.getReferenceCode(),o.getPatientReference(),o.getPatientName(),o.getDischargeChecklist().getReferenceCode(),o.getChannel(),o.getStatus(),o.getOutcome(),o.getPriority(),o.getEscalationLevel(),o.getAssignedTeam(),o.getAssignedTo(),String.valueOf(o.getAttemptCount()),String.valueOf(o.getNextContactDate()),String.valueOf(o.isRedFlagSymptoms()));return b.toString();}

    private PatientOutreach require(long id){return repository.findById(id).orElseThrow(()->new NoSuchElementException("Outreach record not found."));}
    private void ensureOpen(PatientOutreach item){if(item.isFinalised())throw new IllegalStateException("Record is finalised. Reopen it before changing workflow state.");}
    private String nextReference(){String date=LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);String candidate;int i=1;do{candidate="OUT-"+date+"-"+String.format(Locale.ROOT,"%04d",i++);}while(repository.existsByReferenceCode(candidate));return candidate;}
    private static String filter(String v,Set<String> allowed,String label){String c=optional(v,50);if(c==null)return null;c=c.toUpperCase(Locale.ROOT);if(!allowed.contains(c))throw new IllegalArgumentException("Invalid "+label+" filter.");return c;}
    private static String filterTeam(String v){String c=optional(v,100);if(c==null)return null;return TEAMS.stream().filter(t->t.equalsIgnoreCase(c)).findFirst().orElseThrow(()->new IllegalArgumentException("Invalid team filter."));}
    private static String team(String v){String c=required(v,"Team",100);return TEAMS.stream().filter(t->t.equalsIgnoreCase(c)).findFirst().orElseThrow(()->new IllegalArgumentException("Choose a valid team."));}
    private static String value(String v,Set<String> allowed,String label){String c=required(v,label,40).toUpperCase(Locale.ROOT);if(!allowed.contains(c))throw new IllegalArgumentException("Unsupported "+label+".");return c;}
    private static String required(String v,String label,int max){String c=optional(v,max);if(c==null)throw new IllegalArgumentException(label+" is required.");return c;}
    private static String optional(String v,int max){if(v==null||v.trim().isEmpty())return null;String c=v.trim();return c.length()<=max?c:c.substring(0,max);}
    private static String append(String old,String next){String value=old==null||old.isBlank()?next:old+"\n"+next;return value.length()<=2500?value:value.substring(value.length()-2500);}
    private static Map<String,Long> map(List<Object[]> rows){Map<String,Long> result=new LinkedHashMap<>();for(Object[] row:rows)result.put(String.valueOf(row[0]==null?"Unassigned":row[0]),((Number)row[1]).longValue());return result;}
    private static void csvRow(StringBuilder b,String... values){for(int i=0;i<values.length;i++){if(i>0)b.append(',');String v=values[i]==null?"":values[i];b.append('"').append(v.replace("\"","\"\"").replace('\n',' ').replace('\r',' ')).append('"');}b.append("\r\n");}
}
