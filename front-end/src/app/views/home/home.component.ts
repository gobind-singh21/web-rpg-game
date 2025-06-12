import { Component } from '@angular/core';
import { setting } from '../setting/setting.component';
import { Router } from '@angular/router';

@Component({
  selector: 'home',
  imports: [setting],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  constructor(private router: Router) { }
  startPlay(){
    // alert("Play Started")
    console.log("Play Begin");
    this.router.navigate(['/team-making']);
  }
}
