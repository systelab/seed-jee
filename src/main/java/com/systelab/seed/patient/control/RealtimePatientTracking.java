package com.systelab.seed.patient.control;

import com.systelab.seed.patient.control.cdi.PatientCreated;
import com.systelab.seed.patient.entity.Patient;

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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;

@Singleton
@ServerEndpoint("/tracking")
public class RealtimePatientTracking {
    @Inject
    private Logger logger;

    private final Set<Session> sessions = new HashSet<>();

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
        logger.info("Message received from Web socket");
    }

    public void onPatientCreated(@Observes @PatientCreated Patient patient) {
        Writer writer = new StringWriter();

        try (JsonGenerator generator = Json.createGenerator(writer)) {
            generator.writeStartObject().write("patientid", patient.getId().toString()).write("patientname", patient.getName()).writeEnd();
        }
        sessions.forEach((session) -> sendMessageToSession(session, writer.toString()));
    }

    private void sendMessageToSession(Session session, String json) {
        try {
            session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            logger.warn("Unable to publish WebSocket message", ex);
        }
    }
}