import { Router, RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './views/login/login.component';
import { RegisterComponent } from './views/register/register.component';
import { NgModule } from '@angular/core';
import { CreatePasswordComponent } from './views/create-password/create-password.component';
import { ForgotPasswordComponent } from './views/forgot-password/forgot-password.component';
import { HomeComponent } from './views/home/home.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {path: 'create-password', component: CreatePasswordComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'home', component: HomeComponent}
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})
export class AppRoutingModule {}
