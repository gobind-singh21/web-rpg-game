import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { baseUrl, signupEndPoint } from '../../shared/urls/urls';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private apiURl  = baseUrl+signupEndPoint;
  //  "http://localhost:8080/api/auth/signup"

  constructor(private http : HttpClient) { }

  registerUser(userData: any) : Observable<any> {
    return this.http.post(this.apiURl, userData);
  }

}
