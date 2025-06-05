import { Component } from '@angular/core';
import { InputComponent } from "../../shared/input/input.component";
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [InputComponent,ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  formData: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {
    this.formData = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  get emailControl() { return this.formData.get('email') as FormControl; }
  get passwordControl() { return this.formData.get('password') as FormControl; }

  onSubmit(): void {
    if (this.formData.valid) {
      console.log('Login data:', this.formData.value);
    } else {
      console.log('Form is invalid');
    }
  }

  ForgotPassword(): void {
    console.log('Navigate to Forgot Password');
    this.router.navigate(['/forgot-password']);
  }

  NavigateToRegister(): void {
    console.log('Navigate to Register');
    this.router.navigate(['/register']);
  }
}
