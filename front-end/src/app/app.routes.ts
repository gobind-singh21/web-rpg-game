import { Router, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './views/login/login.component';
import { RegisterComponent } from './views/register/register.component';
import { NgModule } from '@angular/core';
import { CreatePasswordComponent } from './views/create-password/create-password.component';
import { ForgotPasswordComponent } from './views/forgot-password/forgot-password.component';
import { HomeComponent } from './views/home/home.component';
import { TeamMaking } from './views/team-making/team-making.component';
import { BattlefieldComponent } from './views/battlefield/battlefield.component';
import { HowToPlayComponent } from './views/how-to-play/how-to-play/how-to-play.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {path: 'create-password', component: CreatePasswordComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'home', component: HomeComponent},
  {path: 'team-making', component: TeamMaking},
  {path: 'battlefield', component: BattlefieldComponent},
  {path: 'how-to-play', component: HowToPlayComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})
export class AppRoutingModule {}
