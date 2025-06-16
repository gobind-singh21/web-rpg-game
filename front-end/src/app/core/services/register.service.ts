import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { baseUrl, signupEndPoint } from '../../shared/urls/urls';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private apiURl  = baseUrl + signupEndPoint;

  constructor(private http : HttpClient) { }

  registerUser(userData: any) : Observable<any> {
    return this.http.post(this.apiURl, userData);
  }

}
