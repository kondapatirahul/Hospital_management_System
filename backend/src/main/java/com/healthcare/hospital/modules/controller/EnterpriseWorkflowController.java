package com.healthcare.hospital.modules.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Landing page for enterprise workflow modules. */
@Controller
public class EnterpriseWorkflowController {
    @GetMapping("/enterprise")
    public String index() {
        return "enterprise/index";
    }
}
