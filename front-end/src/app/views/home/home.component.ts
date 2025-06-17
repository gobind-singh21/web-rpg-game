import { Component } from '@angular/core';
import { setting } from '../setting/setting.component';
import { Router } from '@angular/router';
import { LoggedInCheckService } from '../../core/services/logged-in-check.service';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'home',
  imports: [setting, MatIcon],
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
    this.router.navigate(['/team-making']);
  }
  howToPlay() {
    this.router.navigate(["/how-to-play"])
  }
}
