import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { baseUrl, loginEndPoint } from '../../shared/urls/urls';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private apiURl  = baseUrl + loginEndPoint;

  constructor(private http : HttpClient) { }

  loginUser(userData: any) : Observable<any> {
    return this.http.post(this.apiURl, userData);
  }
}
