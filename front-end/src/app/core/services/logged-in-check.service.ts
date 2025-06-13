import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoggedInCheckService {

  constructor() { }

  isAlreadyLoggedIn() : boolean {
    var token = localStorage.getItem("token");
    return token == null ? false : true;
  }
}
