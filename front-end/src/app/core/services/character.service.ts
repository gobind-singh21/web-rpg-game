import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Character } from '../../shared/types/character';
import { map, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CharacterService {
  private charactersURL = 'http://localhost:8080/api/characters/all';

  constructor(private http: HttpClient) {}

  // getAllCharacters(): Observable<Character[]> {
  //   return this.http.get<Character[]>(this.charactersURL);
  // }

  // getAllCharacters(): Observable<Character[]> {
  //   const token = localStorage.getItem('token'); // Get the JWT token from localStorage
  //   const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
  //   console.log('Fetching characters from API...');
  //   return this.http.get<Character[]>(this.charactersURL, { headers });

  // }

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
}
