package com.fullteaching.backend.calendar;

import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.user.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
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

import org.springframework.security.core.context.SecurityContextHolder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CalendarAPI {
    private static final String APPLICATION_NAME = "Full teaching";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    public static void createEvent(Session session) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credentials =(Credential) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();

        Event event = new Event()
        .setSummary(session.getTitle())
        .setDescription(session.getDescription());

        DateTime startDateTime = new DateTime(session.getDate());
        EventDateTime start = new EventDateTime()
            .setDateTime(startDateTime);
        event.setStart(start);
        event.setEnd(null);

        List<EventAttendee> attendees = new ArrayList<EventAttendee>();
        

        for (User user : session.getCourse().getAttenders()) {
            EventAttendee atendee = new EventAttendee().setEmail(user.getName());
            attendees.add(atendee);
        }
        
        event.setAttendees(attendees);


        String calendarId = "primary";
        event = service.events().insert(calendarId, event).execute();
    }
}