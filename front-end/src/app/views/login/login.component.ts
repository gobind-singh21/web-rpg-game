import { Component } from '@angular/core';
import { InputComponent } from '../../shared/input/input.component';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../../core/services/login.service';

@Component({
  selector: 'app-login',
  imports: [InputComponent, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  formData: FormGroup;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private loginService: LoginService
  ) {
    this.formData = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', Validators.required],
    });
  }

  get emailControl() {
    return this.formData.get('email') as FormControl;
  }
  get passwordControl() {
    return this.formData.get('password') as FormControl;
  }

  onSubmit(): void {
    if (this.formData.invalid) {
      console.log('form is invalid');
    }

    const userData = {
      usernameOrEmail: this.formData.get('email')?.value,
      password: this.formData.get('password')?.value,
    };

    console.log('Login Data:', userData);

    if (this.formData.valid) {
      this.loginService.loginUser(userData).subscribe({
        next: (response: any) => {
          console.log('Login successful:', response);
          alert("You Have successfully Login");
          this.router.navigate(['/home']);
        },
        error: (error: any) => {
          console.error('Login failed:', error);
          const errorMessage = error.error?.error || 'Unknown error';
          alert(`Login failed: ${errorMessage}`);
        },
      });
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
