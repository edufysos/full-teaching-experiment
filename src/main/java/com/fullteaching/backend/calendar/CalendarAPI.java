package com.fullteaching.backend.calendar;

import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
/* import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets; */
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CalendarAPI {
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

        Events events = service.events().list("primary").setQ(" -FullTeaching").execute();
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
        .setDescription(session.getDescription()+" -FullTeaching");

        DateTime startDateTime = new DateTime(session.getDate());
        DateTime endDateTime = new DateTime(session.getDate()+60000);
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setStart(start);
        event.setEnd(end);

        service.events().insert("primary", event).execute();
    }

}