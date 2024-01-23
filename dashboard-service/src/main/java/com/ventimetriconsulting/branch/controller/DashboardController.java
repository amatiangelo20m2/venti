package com.ventimetriconsulting.branch.controller;

import com.ventimetriconsulting.branch.entity.dto.DashboardData;
import com.ventimetriconsulting.branch.service.BranchService;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchResponseEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/dashboard")
@AllArgsConstructor
public class DashboardController {

    private BranchService branchService;

    @PostMapping(path = "/branch/save")
    @ResponseStatus(HttpStatus.CREATED)
    public BranchResponseEntity save(@RequestBody BranchCreationEntity branchCreationEntity) {
        return branchService.createBranch(branchCreationEntity);
    }

    @GetMapping(path = "/retrievedata")
    @ResponseStatus(HttpStatus.OK)
    public DashboardData retrieveDashboardData(@RequestParam String userCode){
        List<BranchResponseEntity> branchesByUserCode = branchService.getBranchesByUserCode(userCode);

        return DashboardData
                .builder()
                .branches(branchesByUserCode)
                .build();
    }

    @GetMapping(path = "/branch")
    @ResponseStatus(HttpStatus.OK)
    public BranchResponseEntity getbranch(@RequestParam String userCode,
                                          @RequestParam String branchCode){
        return branchService.getBranch(userCode, branchCode);
    }

}
