import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { InputComponent } from '../../shared/input/input.component';

@Component({
  selector: 'app-create-password',
  imports: [ReactiveFormsModule, InputComponent],
  templateUrl: './create-password.component.html',
  styleUrl: './create-password.component.css'
})
export class CreatePasswordComponent {

  formData: FormGroup;
  passwordControl: FormControl;
  confirmPasswordControl: FormControl;

  constructor(private fb: FormBuilder, private router: Router) {
    this.passwordControl = new FormControl('', [Validators.required]);
    this.confirmPasswordControl = new FormControl('', [Validators.required]);
    this.formData = this.fb.group({
      password: this.passwordControl,
      confirmPassword: this.confirmPasswordControl,
    }, { validators: this.passwordMatchValidator });
  }

  passwordMatchValidator(group: FormGroup) {
    const password = group.get('password')?.value;
    const confirmPassword = group.get('confirmPassword')?.value;
    return password === confirmPassword ? null : { passwordMismatch: true };
  }
  
  onSubmit(): void {
    if (this.formData.valid) {
      console.log('Password Reset : ', this.formData.value);
      this.router.navigate(['/login']);
    } else {
      console.log('Form is invalid');
    }
  }


}
