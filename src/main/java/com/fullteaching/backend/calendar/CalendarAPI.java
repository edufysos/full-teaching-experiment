package com.fullteaching.backend.calendar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.fullteaching.backend.session.Session;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarAPI {
    private static final Logger log = LoggerFactory.getLogger(CalendarAPI.class);
    private static final String APPLICATION_NAME = "Full teaching";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    private static Credential getCredentials(String token) throws IOException {
        return new GoogleCredential().setAccessToken(token);
    }
    
    public static void syncCalendar(List<Session> sessions, String token) throws IOException, GeneralSecurityException{
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(token))
                .setApplicationName(APPLICATION_NAME)
                .build();
        Events events = service.events().list("primary").setQ("session").execute();
        List<Event> items = events.getItems();
        Boolean synced;
        for (Session session : sessions) {
            synced = false;
            for (Event event : items) {
                if (event.getSummary() == session.getTitle()){
                    synced = true;
                    break;
                }
            }
            if (!synced){
                createEvent(session, service);
            }
        }
    }
    
    public static void createEvent(Session session, Calendar service) throws IOException, GeneralSecurityException {
        Event event = new Event()
        .setSummary(session.getTitle())
        .setDescription(session.getDescription());

        DateTime startDateTime = new DateTime(session.getDate());
        DateTime endDateTime = new DateTime(session.getDate()+60000);
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setStart(start);
        event.setEnd(end);

        service.events().insert("primary", event).execute();
    }

}