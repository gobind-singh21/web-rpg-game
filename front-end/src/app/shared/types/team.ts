import { Character } from "./character";

export interface Team {
  name: string,
  skillPoints: number,
  characters: Character[]
}
