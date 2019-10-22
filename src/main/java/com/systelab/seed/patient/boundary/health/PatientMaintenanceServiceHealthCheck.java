package com.systelab.seed.patient.boundary.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;

@Health
@ApplicationScoped
public class PatientMaintenanceServiceHealthCheck implements HealthCheck {

    private static boolean working = true;
    private static LocalDateTime lastExecution = LocalDateTime.MIN;

    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse
                .builder()
                .name("PatientMaintenanceService")
                .withData("last-update", lastExecution.toString());
        return working ? builder.up().build() : builder.down().build();
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public void setLastExecution(LocalDateTime lastExecution) {
        this.lastExecution = lastExecution;
    }
}