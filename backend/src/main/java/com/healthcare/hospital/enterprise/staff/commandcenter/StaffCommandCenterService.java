package com.healthcare.hospital.enterprise.staff.commandcenter;

import org.springframework.stereotype.Service;
import java.util.*;

/** Supplies staff governance coverage and evaluates explicitly submitted evidence. */
@Service
public class StaffCommandCenterService {
    private final StaffControlEvaluator evaluator = new StaffControlEvaluator();
    public Map<String,Object> overview() {
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("domainCount", StaffControlCatalog.domainCount());
        result.put("controlCount", StaffControlCatalog.allRules().size());
        result.put("domains", StaffControlCatalog.allDomains().entrySet().stream().map(entry -> Map.of("name", entry.getKey(), "controls", entry.getValue().size())).toList());
        result.put("evidenceMode", "catalogue-only-until-workflow-adapters-connect");
        return result;
    }
    public StaffControlEvaluation evaluate(String domain, Map<String,String> evidence) {
        List<StaffControlRule> rules = StaffControlCatalog.allDomains().get(domain);
        if (rules == null) throw new IllegalArgumentException("Unknown staff control domain: " + domain);
        return evaluator.evaluate(domain, rules, evidence == null ? Map.of() : evidence);
    }
}
