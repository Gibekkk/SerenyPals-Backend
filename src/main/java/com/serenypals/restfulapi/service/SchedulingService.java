package com.serenypals.restfulapi.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class SchedulingService {
    @Autowired
    private OTPService otpService; 

    @Autowired
    private CleanUpService cleanUpService; 

    @Scheduled(fixedRate = 5000)
    public void clearRedundantData() {
        otpService.clearRedundantOTP();
        cleanUpService.fullClean();
    }
}
