import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { InputComponent } from '../../shared/input/input.component';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { baseUrl, resetPasswordWithCode } from '../../shared/urls/urls';

@Component({
  selector: 'app-create-password',
  imports: [ReactiveFormsModule, InputComponent, CommonModule],
  templateUrl: './create-password.component.html',
  styleUrl: './create-password.component.css'
})
export class CreatePasswordComponent implements OnInit {

  formData: FormGroup;

  userEmail: string | null = null;
  message: string | null = null;
  error: string | null = null;

  constructor(private fb: FormBuilder, private router: Router, private activatedRoute: ActivatedRoute, private http: HttpClient) {

    this.formData = this.fb.group({
      otp: ['', [
        Validators.required,
        Validators.pattern(/^\d{6}$/)
      ]],
      newPassword: ['', [
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(40)
      ]],
      confirmPassword: ['', [Validators.required]],
    }, { validators: this.passwordMatchValidator });
  }

  ngOnInit(): void {
      this.activatedRoute.queryParams.subscribe(params => {
        this.userEmail = params['email'];
        if (!this.userEmail) {
          console.warn('Email not found in route parameters. Cannot proceed with password reset.');
          this.error = 'Missing email for password reset. Please start the process again.';
          this.router.navigate(['/forgot-password']);
        }
      });
  }

  passwordMatchValidator(group: AbstractControl): { [key: string]: boolean } | null {
    const password = group.get('newPassword')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }

  get otpControl() { return this.formData.get('otp') as FormControl; }
  get newPasswordControl() { return this.formData.get('newPassword') as FormControl; }
  get confirmPasswordControl() { return this.formData.get('confirmPassword') as FormControl; }
  
  onSubmit(): void {
    this.message = null;
    this.error = null;

    if (this.formData.hasError('passwordMismatch')) {
      this.error = 'Passwords do not match';
      console.log('Frontend validation error: Passwords do not match');
    }

    if (this.formData.valid && this.userEmail) {
      console.log('Form is valid. Submitting password reset request...');

      const requestBody = {
        email: this.userEmail,
        resetCode: this.formData.value.otp,
        newPassword: this.formData.value.newPassword
      };

      this.http.post(baseUrl+resetPasswordWithCode, requestBody)
        .subscribe({
          next: (response: any) => {
            this.message = response.message;
            console.log('Password reset successful:', response);
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 2000);
          },
          error: (err) => {
            this.error = err.error?.error || 'Failed to reset password. Please try again.';
            console.error('Password reset failed:', err);
          }
        });
    } else {
      if (!this.userEmail) {
        this.error = 'Email not found for reset. Please restart the process.';
      } else {
        this.error = 'Please fill in all required fields correctly.';
      }
      console.log('Form is invalid or email is missing.');
    }
  }
}