import { CharacterBase } from "./characterBase";
import { CharacterSnapshot } from "./characterSnapshot";

export interface Character {
  id?: number,
  base: CharacterBase,
  snapshot: CharacterSnapshot,
}
