import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RegisterComponent } from './views/register/register.component';
import { InputComponent } from "./shared/input/input.component";
import { CreatePasswordComponent } from "./views/create-password/create-password.component";
import { CharactercardComponent } from "./shared/charactercard/charactercard.component";
import { MatIconModule } from '@angular/material/icon';
import { Character } from './shared/types/character';
import { CharacterService } from './core/services/character.service';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatIconModule, CommonModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {

}
