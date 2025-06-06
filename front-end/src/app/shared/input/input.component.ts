import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  imports: [ReactiveFormsModule],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css'
})
export class InputComponent {
  @Input() id: string = '';
  @Input() type: string = 'text';
  @Input() label: string = '';
  @Input() control!: FormControl;
}
