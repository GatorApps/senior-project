package org.gatorapps.garesearch.controller;

import org.gatorapps.garesearch.model.garesearch.ApplicantProfile;
import org.gatorapps.garesearch.model.garesearch.Lab;
import org.gatorapps.garesearch.repository.garesearch.ApplicantProfileRepository;
import org.gatorapps.garesearch.repository.garesearch.LabRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.data.util.TypeUtils.type;

@SpringBootTest
public class RetrieveObjExample extends BaseTest {

    @Autowired
    LabRepository labRepository;

    @Autowired
    ApplicantProfileRepository applicantProfileRepository;


//    @Test
//    public void getLab(){
//        Lab lab = labRepository.findById("99dcf5a77621f49532e47b52").orElseThrow();
//
//        System.out.println(lab.getName());
//    }
//
//    @Test
//    public void getApplicant(){
//        ApplicantProfile applicant = applicantProfileRepository.findById("b5c0c01ab87e195493ae9c10").orElseThrow();
//
//        System.out.println(type(applicant.getLastUpdateTimeStamp().getClass()));
//        System.out.println(applicant.getLastUpdateTimeStamp());
//    }



}
