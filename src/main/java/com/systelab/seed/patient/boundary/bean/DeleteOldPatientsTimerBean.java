package com.systelab.seed.patient.boundary.bean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

@Singleton
@LocalBean
@Startup
public class DeleteOldPatientsTimerBean {
    @Resource
    private TimerService timerService;

    @Inject
    private Logger logger;

    @PostConstruct
    private void init() {
        cancelAnyRunningTimer();
        createTimer();
    }

    @Timeout
    public void execute(Timer timer) {
        LocalDate startDate = LocalDate.now().minusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String sqlDate = formatter.format(startDate);

        logger.log(Level.INFO, "TODO: Delete Patients where lastupdate<" + sqlDate + " and status is blank");
    }

    private void createTimer() {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("DeleteOldPatientsTimer");
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("*").minute("10");
        timerService.createCalendarTimer(schedule, timerConfig);
    }
    private void cancelAnyRunningTimer() {
        timerService.getTimers().stream().forEach((timer)-> {
            logger.log(Level.INFO, "Found running timer with info: " + timer.getInfo() + ", cancelling it");
            timer.cancel();
        });
    }
}
