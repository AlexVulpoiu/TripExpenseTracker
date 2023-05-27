package com.unibuc.fmi.tripexpensetracker.controller;

import com.unibuc.fmi.tripexpensetracker.security.services.UserDetailsImpl;
import com.unibuc.fmi.tripexpensetracker.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/export")
public class ExportController {

    private final ExportService exportService;

    @Autowired
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/individualSpending")
    public ResponseEntity<?> exportIndividualSpendingReport(Principal principal) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Individual_Spending.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(exportService.exportIndividualSpendingReport(((UserDetailsImpl) ((UsernamePasswordAuthenticationToken)principal).getPrincipal()).getId()));
    }

    @GetMapping("/tripSpending/{tripId}")
    public ResponseEntity<?> exportTripSpendingReport(Principal principal, @PathVariable Long tripId) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Trip_Spending.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(exportService.exportTripSpendingReport(((UserDetailsImpl) ((UsernamePasswordAuthenticationToken)principal).getPrincipal()).getId(), tripId));
    }
}
