import { Component } from '@angular/core';
import { InputComponent } from "../../shared/input/input.component";
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  imports: [InputComponent, ReactiveFormsModule, CommonModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})

export class ForgotPasswordComponent {

  formData: FormGroup;

  message: string | null = null;
  error: string | null = null;

  constructor(private fb: FormBuilder, private router: Router, private http: HttpClient) {
    this.formData = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
    });
  }

  get emailControl() { return this.formData.get('email') as FormControl; }

  onSubmit(): void {
    this.message = null;
    this.error = null;

    if (this.formData.valid) {
      const email = this.formData.value.email;
      console.log('Requesting password reset for email:', email);

      this.http.post('http://localhost:8080/api/auth/forgot-password/request', {email: email})
        .subscribe({
          next: (response: any) => {
            if (response.message) {
              this.message = response.message;
            } else {
              this.message = 'Password reset request successful.'; 
            }
            console.log('Password reset request successful:', response);
            this.router.navigate(['/create-password'], { queryParams: { email: email } });
          },
          error: (err) => {
            if (err.error && err.error.error) {
              this.error = err.error.error;
            } else {
              this.error = 'Failed to send password reset request. Please try again.';
            }
            console.error('Password reset request failed:', err);
          }
        })
    } else {
      this.error = 'Please enter a valid email address.';
      console.log('Form is invalid');
    }
  }
}
