import { Component } from '@angular/core';
import { setting } from '../setting/setting.component';
import { Router } from '@angular/router';
import { LoggedInCheckService } from '../../core/services/logged-in-check.service';

@Component({
  selector: 'home',
  imports: [setting],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor(private router: Router, private loggedInCheckService: LoggedInCheckService) {
    if(!loggedInCheckService.isAlreadyLoggedIn()) {
      router.navigate(["/login"]);
    }
  }
  startPlay(){
    // alert("Play Started")
    console.log("Play Begin");
    this.router.navigate(['/team-making']);
  }
}
