package com.ventimetriconsulting.branch.controller;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.entity.dto.VentiMetriQuadriData;
import com.ventimetriconsulting.branch.service.BranchService;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/")
@AllArgsConstructor
public class BranchController {

    private BranchService branchService;

    @PostMapping(path = "/branch/save")
    public ResponseEntity<BranchResponseEntity> save(@RequestBody BranchCreationEntity branchCreationEntity) {
        BranchResponseEntity branchResponseEntity = branchService.createBranch(branchCreationEntity);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(branchResponseEntity);
    }

    @GetMapping(path = "/retrievedata")
    public ResponseEntity<VentiMetriQuadriData> retrieveDashboardData(@RequestParam String userCode){
        List<BranchResponseEntity> branchesByUserCode = branchService.getBranchesByUserCode(userCode);

        return ResponseEntity.status(HttpStatus.OK)
                .body(VentiMetriQuadriData
                        .builder()
                        .branches(branchesByUserCode)
                        .build());
    }

    @GetMapping(path = "/branch")
    public ResponseEntity<BranchResponseEntity> getBranch(@RequestParam String userCode, @RequestParam String branchCode) {
        BranchResponseEntity branch = branchService.getBranch(userCode, branchCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(branch);
    }

    @GetMapping(path = "/getbranchdata")
    public ResponseEntity<BranchResponseEntity> getBranchData(@RequestParam String branchCode) {
        BranchResponseEntity branchData = branchService.getBranchData(branchCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(branchData);
    }

    @PostMapping(path = "/setfmctoken")
    public ResponseEntity<Void> setFcmToken(@RequestParam String userCode, @RequestParam String branchCode,
                                            @RequestParam String fcmToken) {
        branchService.setFcmToken(userCode, branchCode, fcmToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
