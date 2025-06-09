import { CharacterClass } from "./characterClass";

export interface CharacterBase {
  id?: number,
  name: string,
  baseHealth: number,
  baseAttack: number,
  baseDefense: number,
  baseSpeed: number,
  characterClass: CharacterClass,
}
