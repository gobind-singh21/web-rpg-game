import { CharacterSnapshot } from "./characterSnapshot";

export interface ActionResponse {
  validMove: boolean,
  message: string,
  lineup: CharacterSnapshot[],
}
