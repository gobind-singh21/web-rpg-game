import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Login } from './views/auth/Login/Login';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Login],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  
}
