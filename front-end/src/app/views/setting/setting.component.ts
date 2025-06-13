import { CommonModule } from '@angular/common';
import { Component, OnInit, HostBinding } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css'],
  imports: [CommonModule]
})
export class setting implements OnInit {

  menuOpen: boolean = false;
  animationPlayed: boolean = false; // To control 'from-arrow' animation on initial load

  isRotated: boolean = false;

  @HostBinding('class.background--blur')
  get applyBackgroundBlur() {
    return this.menuOpen;
  }

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
    if (!this.animationPlayed) {
      this.animationPlayed = true;
    }

    this.isRotated = !this.isRotated;


  }

  onLogout() {
    localStorage.removeItem("token");
    this.router.navigate(["/login"]);
  }
}
