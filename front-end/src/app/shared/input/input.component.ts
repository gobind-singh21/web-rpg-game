import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css'
})
export class InputComponent {
  @Input() id: string = '';
  @Input() type: string = 'text';
  @Input() label: string = '';
  @Input() control!: FormControl;

  // getErrorMessage(): string {
  //   if (this.control?.hasError('required')) {
  //     return `${this.label} is required`;
  //   }
  //   if (this.control?.hasError('email')) {
  //     return `Invalid email format`;
  //   }
  //   return '';
  // }
}
