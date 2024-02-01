package com.ventimetriconsulting.branch.service;

import com.ventimetriconsulting.branch.entity.*;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.branch.exception.BranchNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BranchService {

    private BranchRepository branchRepository;

    private BranchUserRepository branchUserRepository;

    @Transactional
    public BranchResponseEntity createBranch(BranchCreationEntity branchCreationEntity) {

        log.info("Branch Service - Create branch by user [{}] - Branch Type {} - Branch Entity {}",
                branchCreationEntity.getUserCode(),
                branchCreationEntity.getType(),
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
                        .logoImage(branchCreationEntity.getLogoImage())
                        .build());

        branchUserRepository.save(BranchUser.builder()
                .id(0)
                .branch(savedBranch)
                .userCode(branchCreationEntity.getUserCode())
                .role(Role.PROPRIETARIO)
                .build());

        return BranchResponseEntity.builder()
                .branchCode(savedBranch.getBranchCode())
                .phone(savedBranch.getPhoneNumber())
                .email(savedBranch.getEmail())
                .address(savedBranch.getAddress())
                .vat(savedBranch.getVat())
                .type(savedBranch.getType())
                .name(savedBranch.getName())
                .role(Role.PROPRIETARIO)
                .logoImage(savedBranch.getLogoImage())
                .build();
    }

    public List<BranchResponseEntity> getBranchesByUserCode(String userCode) {

        log.info("Retrieve branches for user with code {}", userCode);
        Optional<List<BranchUser>> branchesByUserCode = branchUserRepository.findBranchesByUserCode(userCode);

        if(branchesByUserCode.isPresent()){
            if(!branchesByUserCode.get().isEmpty()) {
                return branchesByUserCode.get().stream()
                        .map(this::convertToBranchResponseEntity)
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private BranchResponseEntity convertToBranchResponseEntity(BranchUser branchUser) {
        return BranchResponseEntity.builder()
                .name(branchUser.getBranch().getName())
                .address(branchUser.getBranch().getAddress())
                .email(branchUser.getBranch().getEmail())
                .phone(branchUser.getBranch().getPhoneNumber())
                .vat(branchUser.getBranch().getVat())
                .type(branchUser.getBranch().getType())
                .branchCode(branchUser.getBranch().getBranchCode())
                .logoImage(branchUser.getBranch().getLogoImage())
                .role(branchUser.getRole())
                .build();
    }


    public BranchResponseEntity getBranch(String userCode, String branchCode) {

        log.info("Retrieve branch for user with code {} and branch with code {}", userCode, branchCode);
        Optional<BranchUser> branchByUserCodeAndBranchCode = branchUserRepository.findBranchesByUserCodeAndBranchCode(userCode, branchCode);

        if(branchByUserCodeAndBranchCode.isPresent()){

            return convertToBranchResponseEntity(branchByUserCodeAndBranchCode.get());
        }
        throw new BranchNotFoundException("Branch not found for user with code [" + userCode + "] and branch with code [" + branchCode + "] ");
    }

    public BranchResponseEntity getBranchData(String branchCode) {
        log.info("Retrieve branch info by code {}", branchCode);
        Optional<Branch> byBranchCode = branchRepository.findByBranchCode(branchCode);
        if(byBranchCode.isPresent()){
            return BranchResponseEntity.builder()
                    .name(byBranchCode.get().getName())
                    .branchCode(byBranchCode.get().getBranchCode())
                    .address(byBranchCode.get().getAddress())
                    .logoImage(byBranchCode.get().getLogoImage())
                    .phone(byBranchCode.get().getPhoneNumber())
                    .email(byBranchCode.get().getEmail())
                    .build();
        }else{
            log.error("getBranchData method give error. No branch found with branch code " + branchCode);
            throw new BranchNotFoundException("No branch found with branch code " + branchCode);
        }
    }
}
