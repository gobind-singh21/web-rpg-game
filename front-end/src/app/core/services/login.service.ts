import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private apiURl  = "http://localhost:8080/api/auth/login"

  constructor(private http : HttpClient) { }

  loginUser(userData: any) : Observable<any> {
    return this.http.post(this.apiURl, userData);
  }
}
