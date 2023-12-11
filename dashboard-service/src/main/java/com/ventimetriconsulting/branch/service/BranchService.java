package com.ventimetriconsulting.branch.service;

import com.ventimetriconsulting.branch.entity.Branch;
import com.ventimetriconsulting.branch.entity.BranchUser;
import com.ventimetriconsulting.branch.entity.Role;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BranchService {

    private BranchRepository branchRepository;

    private BranchUserRepository branchUserRepository;


    @Transactional
    public BranchResponseEntity createBranch(BranchCreationEntity branchCreationEntity) {
        log.info("Branch Service - Create branch by user [{}] - Branch Entity {}",
                branchCreationEntity.getUserCode(),
                branchCreationEntity);
        Branch savedBranch = branchRepository.save(
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

        branchUserRepository.save(BranchUser.builder()
                        .branchCode(savedBranch.getBranchCode())
                        .userCode(branchCreationEntity.getUserCode())
                        .role(Role.OWNER)
                .build());

        return BranchResponseEntity.builder()
                .branchCode(savedBranch.getBranchCode())
                .phone(savedBranch.getPhoneNumber())
                .email(savedBranch.getEmail())
                .address(savedBranch.getAddress())
                .vat(savedBranch.getVat())
                .type(savedBranch.getType())
                .name(savedBranch.getName())
                .build();
    }

    public List<BranchResponseEntity> retrieveBranchesByUserCode(String userCode) {




        return null;
    }
}
