import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private apiURl  = "http://localhost:8080/api/auth/signup"

  constructor(private http : HttpClient) { }

  registerUser(userData: any) : Observable<any> {
    return this.http.post(this.apiURl, userData);
  }

}
