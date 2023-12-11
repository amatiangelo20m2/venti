package com.ventimetriconsulting.service;

import com.ventimetriconsulting.entity.Branch;
import com.ventimetriconsulting.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.repository.BranchRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class BranchService {

    private BranchRepository branchRepository;

    public BranchResponseEntity createBranch(BranchCreationEntity branchCreationEntity) {
        log.info("Branch Service - Create branch by user [{}] - Branch Entity {}",
                branchCreationEntity.getUserid(),
                branchCreationEntity);
        Branch save = branchRepository.save(
                Branch.builder()
                        .branchId(0)
                        .phoneNumber(branchCreationEntity.getPhone())
                        .vat(branchCreationEntity.getVat())
                        .name(branchCreationEntity.getName())
                        .address(branchCreationEntity.getAddress())
                        .email(branchCreationEntity.getEmail())
                        .phoneNumber(branchCreationEntity.getPhone())
                        .type(branchCreationEntity.getType())
                        .build());

        return BranchResponseEntity.builder()
                .branchCode(save.getBranchCode())
                .phone(save.getPhoneNumber())
                .email(save.getEmail())
                .address(save.getAddress())
                .vat(save.getVat())
                .type(save.getType())
                .name(save.getName())
                .build();
    }
}
