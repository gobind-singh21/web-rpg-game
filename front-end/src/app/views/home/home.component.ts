import { Component } from '@angular/core';
import { setting } from '../setting/setting.component';

@Component({
  selector: 'home',
  imports: [setting],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  startPlay(){
    alert("Play Started")
  }
}
