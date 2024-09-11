import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mail } from '../../shared/models/mail.model'
import { AuthService } from '../../auth/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class MailService {

  BASE_URL = "http://localhost:8082";

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService
  ) { }

  private createHttpOptions() {
    const token = this.authService.getToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token != '' && token != null) {
      headers = headers.append('x-access-token', token);
    }

    return { headers: headers };
  }

  sendEmail(email: string, mailStructure: Mail): Observable<Mail> {
    return this.httpClient.post<Mail>(this.BASE_URL + '/send/' + email, mailStructure, this.createHttpOptions());
  }
}
