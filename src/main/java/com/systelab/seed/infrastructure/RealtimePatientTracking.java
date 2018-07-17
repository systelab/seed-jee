package com.systelab.seed.infrastructure;

import com.systelab.seed.infrastructure.events.cdi.PatientCreated;
import com.systelab.seed.model.patient.Patient;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/tracking")
public class RealtimePatientTracking {
    private Logger logger;

    private final Set<Session> sessions = new HashSet<>();

    @Inject
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @OnOpen
    public void onOpen(final Session session) {
        // Infinite by default on GlassFish. We need this principally for WebLogic.
        session.setMaxIdleTimeout(5L * 60L * 1000L);
        sessions.add(session);
    }

    @OnClose
    public void onClose(final Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message, final Session session) {
        Writer writer = new StringWriter();

        try (JsonGenerator generator = Json.createGenerator(writer)) {
            generator.writeStartObject().write("patientid", "papaaa").write("patientname", "asdsdasd").writeEnd();
        }

        String jsonValue = writer.toString();

        try {
            session.getBasicRemote().sendText(jsonValue);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onPatientCreated(@Observes @PatientCreated Patient patient) {
        Writer writer = new StringWriter();

        try (JsonGenerator generator = Json.createGenerator(writer)) {
            generator.writeStartObject().write("patientid", patient.getId()).write("patientname", patient.getName()).writeEnd();
        }

        String jsonValue = writer.toString();

        sessions.forEach((session)-> {
            try {
                session.getBasicRemote().sendText(jsonValue);
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Unable to publish WebSocket message", ex);
            }
        });

    }
}