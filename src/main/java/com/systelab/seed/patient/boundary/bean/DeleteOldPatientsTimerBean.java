package com.systelab.seed.patient.boundary.bean;

import com.systelab.seed.patient.boundary.health.PatientMaintenanceServiceHealthCheck;
import com.systelab.seed.patient.boundary.PatientService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.time.LocalDateTime;
import org.slf4j.Logger;

@Singleton
@Startup
public class DeleteOldPatientsTimerBean {
    @Resource
    private TimerService timerService;

    @Inject
    private Logger logger;

    @Inject
    PatientMaintenanceServiceHealthCheck healthCheck;

    @EJB
    private PatientService patientService;

    @PostConstruct
    private void init() {
        cancelAnyRunningTimer();
        createTimer();
    }

    @Timeout
    public void execute(Timer timer) {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(1);
            patientService.deactivatePatientsBefore(startDate);
            logger.info("Patients DB purged!");
            healthCheck.setLastExecution(LocalDateTime.now());
            healthCheck.setWorking(true);
        } catch (Exception ex) {
            logger.info("Patients DB not purged!", ex);
            healthCheck.setWorking(false);
        }
    }

    private void createTimer() {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("DeleteOldPatientsTimer");
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*").minute("*").second("0/30");
        timerService.createCalendarTimer(schedule, timerConfig);
    }

    private void cancelAnyRunningTimer() {
        timerService.getTimers().stream().forEach((timer) -> {
            logger.info("Found running timer with info: {}, cancelling it", timer.getInfo());
            timer.cancel();
        });
    }
}
