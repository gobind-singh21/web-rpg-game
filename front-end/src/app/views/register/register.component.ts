import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { InputComponent } from "../../shared/input/input.component";
import { Router } from '@angular/router';
import { RegisterService } from '../../core/services/register.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, InputComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  formData: FormGroup;

  constructor(private fb: FormBuilder, private  router: Router, private registerService: RegisterService) {
    this.formData = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  get emailControl() { return this.formData.get('email') as FormControl; }
  get nameControl() { return this.formData.get('name') as FormControl; }
  get passwordControl() { return this.formData.get('password') as FormControl; }

  onSubmit(): void {
    if (this.formData.valid) {
      this.registerService.registerUser(this.formData.value).subscribe({
        next: (response) => {
          console.log('Registration successful', response);
          this.navigateToLogin();
        },
        error: (error) => {
          console.error('Registration failed', error);
        }
      });
    } else {
      console.log('Form is invalid');
    }
  }

  navigateToLogin(): void {
    console.log('Navigate to login');
    this.router.navigate(['/login']);
  }
}
