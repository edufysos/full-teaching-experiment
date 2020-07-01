package com.fullteaching.backend.calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.fullteaching.backend.session.Session;
import com.fullteaching.backend.user.UserComponent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CalendarController
 */

@RestController
public class CalendarController {

    private static final Logger log = LoggerFactory.getLogger(CalendarController.class);

    @Autowired
    UserComponent userComponent;

    @RequestMapping(value="/api-calendar", method = RequestMethod.POST)
    public ResponseEntity<Object> syncCalendar(@RequestBody List<Session> sessions) {
        String accessToken = userComponent.getAuthorizedClient().getAccessToken().getTokenValue();
        try {
            CalendarAPI.syncCalendar(sessions, accessToken);
            log.info("Your calendar was synced!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.info("Your calendar could not be synced due to", e.getCause());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (GeneralSecurityException e) {
            // TODO Auto-generated catch block
            log.info("Your calendar could not be synced due to", e.getCause());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        log.info("Calendar successfully synced");
        return new ResponseEntity<>(HttpStatus.OK); 
    }



    
}