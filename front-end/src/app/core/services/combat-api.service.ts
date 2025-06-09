import { basicAttackEndPoint } from './../../shared/urls/urls';
import { inject, Injectable } from '@angular/core';
import { BasicTargetService } from './basic-target.service';
import { TurnOrderService } from './turn-order.service';
import { CharacterSnapService } from './character-stat.service';
import { HttpClient } from '@angular/common/http';
import { CharacterSnapshot } from '../../shared/types/characterSnapshot';
import { baseUrl } from '../../shared/urls/urls';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CombatApiService {

  private basicTargetService = inject(BasicTargetService);
  private turnOrderService = inject(TurnOrderService);
  private characterSnapService = inject(CharacterSnapService);

  battleStartApi(): void {
    // TODO : implement battle start API
  }

  basicAttackApi(): Observable<any> {
    var currentCharacterId = this.basicTargetService.basicTarget();
    var turnOrderSnapshot: Map<number, CharacterSnapshot> = new Map();
    this.turnOrderService.turnOrder().forEach((characterId) => {
      var characterSnapshot = this.characterSnapService.characterMap().get(characterId);
      if(characterSnapshot != undefined) {
        turnOrderSnapshot.set(characterId, characterSnapshot as CharacterSnapshot);
      }
    });
    return this.http.post(baseUrl + basicAttackEndPoint, {
      currentCharacterId,
      targetId: this.basicTargetService.basicTarget(),
      currentLineup: turnOrderSnapshot,
    });
  }

  skillApi(): Observable<any> {
    var currentCharacterId = this.basicTargetService.basicTarget();
    var turnOrderSnapshot: Map<number, CharacterSnapshot> = new Map();
    this.turnOrderService.turnOrder().forEach((characterId) => {
      var characterSnapshot = this.characterSnapService.characterMap().get(characterId);
      if(characterSnapshot != undefined) {
        turnOrderSnapshot.set(characterId, characterSnapshot as CharacterSnapshot);
      }
    });
    return this.http.post(baseUrl + basicAttackEndPoint, {
      currentCharacterId,
      currentLineup: turnOrderSnapshot,
    });
  }

  constructor(private http: HttpClient) {}
}
