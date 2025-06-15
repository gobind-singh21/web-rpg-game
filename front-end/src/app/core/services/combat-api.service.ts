import { CharacterSnapshot } from './../../shared/types/characterSnapshot';
import { basicAttackEndPoint, battleStartEndPoint, skillEndPoint } from './../../shared/urls/urls';
import { inject, Injectable } from '@angular/core';
import { BasicTargetService } from './basic-target.service';
import { TurnOrderService } from './turn-order.service';
import { CharacterSnapService } from './character-stat.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { baseUrl } from '../../shared/urls/urls';
import { Observable } from 'rxjs';
import { TeamService } from './team.service';

@Injectable({
  providedIn: 'root'
})
export class CombatApiService {

  private basicTargetService = inject(BasicTargetService);
  private turnOrderService = inject(TurnOrderService);
  private characterSnapService = inject(CharacterSnapService);
  private teamService = inject(TeamService);

  battleStartApi(): Observable<any> {
    const characterIds: number[] = [...this.teamService.team1().characters.map((character) => character.id),
                                  ...this.teamService.team2().characters.map((character) => character.id)]
                                  .filter((id) => id != undefined);
    return this.http.post<any>(baseUrl + battleStartEndPoint, {
      characterIds
    }, {
      headers: this.getAuthHeader()
    });
  }

  basicAttackApi(): Observable<any> {
    const currentCharacterId = this.turnOrderService.turnOrder()[this.turnOrderService.currentCharacter()];
    const turnOrderSnapshot = this.createLineupSnapshotObject();
    return this.http.post(baseUrl + basicAttackEndPoint, {
      currentCharacterId,
      targetId: this.basicTargetService.basicTarget(),
      currentLineup: turnOrderSnapshot,
    }, {
      headers: this.getAuthHeader()
    });
  }

  skillApi(): Observable<any> {
    var currentCharacterId = this.turnOrderService.turnOrder()[this.turnOrderService.currentCharacter()];
    var turnOrderSnapshot = this.createLineupSnapshotObject();
    console.log(turnOrderSnapshot);
    return this.http.post(baseUrl + skillEndPoint, {
      currentCharacterId,
      currentTeam: this.teamService.isCharacterInTeam1(currentCharacterId) ? "Team 1" : "Team 2",
      currentLineup: turnOrderSnapshot,
    }, {
      headers: this.getAuthHeader()
    });
  }

  private createLineupSnapshotObject(): CharacterSnapshot[] {
    const turnOrderSnapshot: CharacterSnapshot[] = [];
    this.turnOrderService.turnOrder().forEach((characterId) => {
      const characterSnapshot = this.characterSnapService.characterMap().get(characterId);
      turnOrderSnapshot.push(characterSnapshot as CharacterSnapshot);
    });
    return turnOrderSnapshot;
  }

  private getAuthHeader(): HttpHeaders {
    const token = localStorage.getItem("token");
    const headers = new HttpHeaders().set("Authorization", `Bearer ${token}`);
    return headers;
  }

  constructor(private http: HttpClient) {}
}
