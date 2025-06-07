import { Effect } from "./effect";

export interface CharacterSnapshot {
  id?: number,
  team: string,
  currentHealth: number,
  shield: number,
  effects: Effect[],
}
