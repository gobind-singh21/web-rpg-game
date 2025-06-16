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
import { LoggedInCheckService } from '../../core/services/logged-in-check.service';
import { MatDialog } from '@angular/material/dialog';
import { CustomDialogComponent } from '../../shared/custom-dialog/custom-dialog.component';

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
    private loginService: LoginService,
    private loggedInCheckService: LoggedInCheckService,
    private dialog: MatDialog
  ) {
    this.formData = this.fb.group({
      email: ['', [Validators.required]],
      password: ['', Validators.required],
    });
    if (loggedInCheckService.isAlreadyLoggedIn()) {
      router.navigate(["/home"])
    }
  }

  get emailControl() {
    return this.formData.get('email') as FormControl;
  }
  get passwordControl() {
    return this.formData.get('password') as FormControl;
  }

  onSubmit(): void {
    if (this.formData.invalid) {
      const dialogRef = this.dialog.open(CustomDialogComponent, {
        disableClose: true,
        data: {
          title: 'Error',
          message: 'Please fill all required fields',
          buttonText: 'OK'
        }
      });
      return;
    }

    const userData = {
      usernameOrEmail: this.formData.get('email')?.value,
      password: this.formData.get('password')?.value,
    };

    if (this.formData.valid) {
      this.loginService.loginUser(userData).subscribe({
        next: (response: any) => {
          localStorage.setItem('token', response.token);
          this.router.navigate(['/home']);
        },
        error: (error: any) => {
          const errorMessage = error.error?.error || 'Invalid email or password';
          const dialogRef = this.dialog.open(CustomDialogComponent, {
            disableClose: false,
            data: {
              title: 'Login Failed',
              message: errorMessage,
              buttonText: 'Try Again'
            },
            hasBackdrop: true,
            backdropClass: 'dialog-backdrop'
          });

          dialogRef.afterClosed().subscribe(() => {
            this.formData.patchValue({
              email: '',
              password: ''
            });
          });
        },
      });
    }
  }

  ForgotPassword(): void {
    this.router.navigate(['/forgot-password']);
  }

  NavigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
