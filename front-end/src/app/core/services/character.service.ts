import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Character } from '../../shared/types/character';
import { map, Observable, tap } from 'rxjs';
import { baseUrl, charactersAll } from '../../shared/urls/urls';
import { Effect } from '../../shared/types/effect';

@Injectable({
  providedIn: 'root',
})
export class CharacterService {
  private charactersURL = baseUrl+charactersAll;

  constructor(private http: HttpClient) {}

  getAllCharacters(): Observable<Character[]> {
    const token = localStorage.getItem('token'); // Get the JWT token from localStorage
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http
      .get<any[]>(this.charactersURL, { headers })
      .pipe(
        map((characters) => characters.map((char) => this.mapToCharacter(char)))
      );
  }

  private mapToCharacter(data: any): Character {
    return {
      id: data.id,
      base: {
        id: data.id,
        name: data.name,
        baseHealth: data.baseHealth,
        baseAttack: data.baseAttack,
        baseDefense: data.baseDefense,
        baseSpeed: data.baseSpeed,
        characterClass: data.characterClass,
        characterCost: data.characterCost,
        imageUrl: data.imageUrl,
        description: data.description,
        backgroundColor: data.backgroundColor,
      },
      snapshot: {
        id: data.id,
        team: '',
        currentHealth: data.baseHealth,
        shield: 0,
        effects: [],
      },
    };
  }

  getMaxSpeed(baseSpeed: number, effects: Effect[]): number {
    var maxSpeed: number = baseSpeed;
    effects.forEach(effect => {
      maxSpeed += (effect.isBuff ? 1 : -1) * (baseSpeed * effect.speedPercent) / 100;
    });
    return maxSpeed;
  }

  getMaxHealth(baseHealth: number, effects: Effect[]): number {
    var maxHealth: number = baseHealth;
    effects.forEach(effect => {
      maxHealth += (effect.isBuff ? 1 : -1) * (baseHealth * effect.healthPercent) / 100;
    });
    return maxHealth;
  }

  getMaxAttack(baseAttack: number, effects: Effect[]): number {
    var maxAttack: number = baseAttack;
    effects.forEach(effect => {
      maxAttack += (effect.isBuff ? 1 : -1) * (baseAttack * effect.attackPercent) / 100;
    });
    return maxAttack;
  }

  getMaxDefense(baseDefense: number, effects: Effect[]): number {
    var maxDefense: number = baseDefense;
    effects.forEach(effect => {
      maxDefense += (effect.isBuff ? 1 : -1) * (baseDefense * effect.defensePercent) / 100;
    });
    return maxDefense;
  }
}
