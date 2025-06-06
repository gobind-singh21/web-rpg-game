import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RegisterComponent } from './views/register/register.component';
import { InputComponent } from "./shared/input/input.component";
import { CreatePasswordComponent } from "./views/create-password/create-password.component";

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RegisterComponent, CreatePasswordComponent,],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

}
