package com.ventimetriconsulting.branch.controller;

import com.ventimetriconsulting.branch.service.BranchService;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchResponseEntity;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/branch")
@AllArgsConstructor
public class BranchController {

    private BranchService branchService;

    @PostMapping(path = "/save")
    public BranchResponseEntity save(@RequestBody BranchCreationEntity branchCreationEntity) { return branchService.createBranch(branchCreationEntity); }

    @GetMapping(path = "/getbranchlist")
    public List<BranchResponseEntity> branchResponseEntityList(@RequestParam String userCode){
        return branchService.retrieveBranchesByUserCode(userCode);

    }
}
