import { Component } from '@angular/core';
import { InputComponent } from "../../shared/input/input.component";
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  imports: [InputComponent,ReactiveFormsModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {

  formData: FormGroup;

  constructor(private fb: FormBuilder, private router: Router) {
    this.formData = this.fb.group({
      OTP: ['', Validators.required],
    });
  }

  get OTPControl() { return this.formData.get('OTP') as FormControl; }

  onSubmit(): void {
    if (this.formData.valid) {
      console.log('OTP Enter :', this.formData.value);
    } else {
      console.log('Form is invalid');
    }
  }

  NavigateToCreatePassword(): void {
    console.log('Navigate to Create Password');
    this.router.navigate(['/create-password']);
  }
}
