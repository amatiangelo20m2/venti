package com.ventimetriconsulting.branch.service;

import com.ventimetriconsulting.branch.configuration.bookingconf.entity.dto.BranchResponseEntity;
import com.ventimetriconsulting.branch.entity.*;
import com.ventimetriconsulting.branch.entity.dto.BranchCreationEntity;
import com.ventimetriconsulting.branch.exception.customexceptions.GlobalException;
import com.ventimetriconsulting.branch.repository.BranchRepository;
import com.ventimetriconsulting.branch.repository.BranchUserRepository;
import com.ventimetriconsulting.branch.exception.customexceptions.BranchNotFoundException;
import com.ventimetriconsulting.supplier.dto.SupplierDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        try{
            log.info("Branch Service - Create branch by user [{}] - Branch Type {} - Branch Entity {}",
                    branchCreationEntity.getUserCode(),
                    branchCreationEntity.getType(),
                    branchCreationEntity);


            Branch savedBranch = branchRepository.save(
                    Branch.builder()
                            .branchId(0)
                            .phoneNumber(branchCreationEntity.getPhoneNumber())
                            .vat(branchCreationEntity.getVat())
                            .name(branchCreationEntity.getName())
                            .address(branchCreationEntity.getAddress())
                            .email(branchCreationEntity.getEmail())
                            .city(branchCreationEntity.getCity())
                            .cap(branchCreationEntity.getCap())
                            .type(branchCreationEntity.getType())
                            .logoImage(branchCreationEntity.getLogoImage())
                            .build());

            log.info("Link branch created with id {} to a user with mail {}", savedBranch.getBranchId(), branchCreationEntity.getEmail());
            branchUserRepository.save(BranchUser.builder()
                    .id(0)
                    .branch(savedBranch)
                    .userCode(branchCreationEntity.getUserCode())
                    .role(Role.PROPRIETARIO)
                    .build());

//            branchUserRepository.flush();

            return BranchResponseEntity.builder()
                    .branchId(savedBranch.getBranchId())
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

        } catch(Exception e){
            log.error(e.getMessage());
            throw new GlobalException(e.getMessage());
        }
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
        log.info("Convert Branch User object to a dto");
        return BranchResponseEntity.builder()
                .branchId(branchUser.getBranch().getBranchId())
                .name(branchUser.getBranch().getName())
                .address(branchUser.getBranch().getAddress())
                .email(branchUser.getBranch().getEmail())
                .phone(branchUser.getBranch().getPhoneNumber())
                .vat(branchUser.getBranch().getVat())
                .type(branchUser.getBranch().getType())
                .branchCode(branchUser.getBranch().getBranchCode())
                .logoImage(branchUser.getBranch().getLogoImage())
                .role(branchUser.getRole())
                .supplierDTOList(SupplierDTO.toDTOList(branchUser.getBranch().getSuppliers()))
                .build();
    }


    public BranchResponseEntity getBranch(String userCode,
                                          String branchCode) {

        log.info("Retrieve branch for user with code {} and branch with code {}", userCode, branchCode);
        Optional<BranchUser> branchByUserCodeAndBranchCode = branchUserRepository.findBranchesByUserCodeAndBranchCode(userCode, branchCode);

        if(branchByUserCodeAndBranchCode.isPresent()) {
            return convertToBranchResponseEntity(branchByUserCodeAndBranchCode.get());
        }
        throw new BranchNotFoundException("Branch not found for user with code [" + userCode + "] and branch with code [" + branchCode + "] ");
    }

    public BranchResponseEntity getBranchData(String branchCode) {
        log.info("Retrieve branch info by code {}", branchCode);
        Optional<Branch> byBranchCode = branchRepository.findByBranchCode(branchCode);
        if(byBranchCode.isPresent()){
            return BranchResponseEntity.builder()
                    .branchId(byBranchCode.get().getBranchId())
                    .name(byBranchCode.get().getName())
                    .branchCode(byBranchCode.get().getBranchCode())
                    .address(byBranchCode.get().getAddress())
                    .logoImage(byBranchCode.get().getLogoImage())
                    .phone(byBranchCode.get().getPhoneNumber())
                    .email(byBranchCode.get().getEmail())
                    .build();
        }else{
            log.error("GetBranchData method give error. No branch found with branch code " + branchCode);
            throw new BranchNotFoundException("No branch found with branch code " + branchCode);
        }
    }

    @Transactional
    public void setFcmToken(String userCode, String branchCode, String fcmToken) {
        log.info("Configure fcm token for branch code {}. User Code {}. FCM Token: {}", branchCode, userCode, fcmToken);

        Optional<List<BranchUser>> branchesByUserCode = branchUserRepository.findBranchesByUserCode(userCode);

        if(branchesByUserCode.isPresent()){
            if(!branchesByUserCode.get().isEmpty()) {
                for(BranchUser branchUser : branchesByUserCode.get()){
                    branchUser.setFMCToken(fcmToken);
                }
            }
        }
    }
}
