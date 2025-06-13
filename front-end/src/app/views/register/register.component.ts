import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { InputComponent } from "../../shared/input/input.component";
import { Router } from '@angular/router';
import { RegisterService } from '../../core/services/register.service';
import { LoggedInCheckService } from '../../core/services/logged-in-check.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, InputComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  formData: FormGroup;

  constructor(private fb: FormBuilder, private  router: Router, private registerService: RegisterService, private loggedInCheckService: LoggedInCheckService) {
    this.formData = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['', Validators.required],
      password: ['', Validators.required],
    });
    if (loggedInCheckService.isAlreadyLoggedIn()) {
      router.navigate(["/home"]);
    }
  }

  get emailControl() { return this.formData.get('email') as FormControl; }
  get nameControl() { return this.formData.get('name') as FormControl; }
  get passwordControl() { return this.formData.get('password') as FormControl; }

  onSubmit(): void {
    if (this.formData.invalid) {
      console.log('Form is invalid');
      return;
    }

    const userData = {
      username: this.formData.get('name')?.value, // Map 'name' to 'username'
      email: this.formData.get('email')?.value,
      password: this.formData.get('password')?.value
    };

    this.registerService.registerUser(userData).subscribe({
      next: (response: any) => {
        console.log('Registration successful:', response);
        localStorage.setItem('token', response.token);
        this.router.navigate(['/home']);
      },
      error: (error: any) => {
        console.error('Registration failed:', error);
        const errorMessage = error.error?.error || 'Unknown error';
        alert(`Registration failed: ${errorMessage}`);
      }
    });
  }

  navigateToLogin(): void {
    console.log('Navigate to login');
    this.router.navigate(['/login']);
  }
}
