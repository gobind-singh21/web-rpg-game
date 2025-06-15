import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CurrentTeamService {

  currentTeam = signal<number>(1);

  constructor() { }
}
