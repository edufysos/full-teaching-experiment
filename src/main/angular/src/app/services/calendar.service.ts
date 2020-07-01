
import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';
import { Session } from '../classes/session';
import { Observable } from 'rxjs/Observable';

@Injectable()
export class CalendarService {

    private urlCalendar = "/api-calendar"

    constructor(private http: Http) { }

    //Synchronize all sessions 
    public sync(sessions: Session[]) {
        
        let body = JSON.stringify(sessions);
        console.log(body);
        let headers = new Headers({
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
          });
          let options = new RequestOptions({ headers });
        return this.http.post(this.urlCalendar, body, options)
            .map(response => {
                console.log("Sync calendar SUCESS. Response: ", response.json());
                return "Synced successfully"
            })
            .catch(error => this.handleError("Calendar sync FAIL. Response: ", error));
    }

    private handleError(message: string, error: any) {
        console.error(message, error);
        return Observable.throw("Server error (" + error.status + "): " + error.text())
      }
}