package com.ventimetriconsulting.controller;

import com.ventimetriconsulting.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.service.BranchService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/branch")
@AllArgsConstructor
public class BranchController {

    private BranchService branchService;

    @PostMapping(path = "/save")
    public BranchResponseEntity save(@RequestBody BranchCreationEntity branchCreationEntity) { return branchService.createBranch(branchCreationEntity); }

}
