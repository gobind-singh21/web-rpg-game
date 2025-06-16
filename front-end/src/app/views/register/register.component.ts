import { Component } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { InputComponent } from "../../shared/input/input.component";
import { Router } from '@angular/router';
import { RegisterService } from '../../core/services/register.service';
import { LoggedInCheckService } from '../../core/services/logged-in-check.service';
import { MatDialog } from '@angular/material/dialog';
import { BattleOverDialogComponent } from '../battle-over-dialog/battle-over-dialog.component';
import { CustomDialogComponent } from '../../shared/custom-dialog/custom-dialog.component';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, InputComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  formData: FormGroup;

  constructor(private fb: FormBuilder, private  router: Router, private registerService: RegisterService, private loggedInCheckService: LoggedInCheckService, private dialog: MatDialog) {
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
      return;
    }

    const userData = {
      username: this.formData.get('name')?.value, // Map 'name' to 'username'
      email: this.formData.get('email')?.value,
      password: this.formData.get('password')?.value
    };

    this.registerService.registerUser(userData).subscribe({
      next: (response: any) => {
        localStorage.setItem('token', `Bearer ${response.token}`);
        const dialogRef = this.dialog.open(CustomDialogComponent, {
          disableClose: false,
          data: {
            title: 'Successful',
            message: 'User registered successfully!',
            buttonText: 'OK'
          },
          hasBackdrop: true,
          backdropClass: 'dialogue-backdrop'
        });

        dialogRef.afterClosed().subscribe(() => {
          this.router.navigate(['/login']);
        });
      },
      error: (error: any) => {
        const errorMessage = error.error?.error || 'Unknown error';
        const dialogRef = this.dialog.open(CustomDialogComponent, {
          disableClose: false,
          data: {
            title: 'Registration Failed',
            message: errorMessage,
            buttonText: 'Try Again'
          },
          hasBackdrop: true
        });
      }
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
