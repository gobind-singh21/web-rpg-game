import { Component } from "@angular/core";
import { Tiles } from "../../shared/tiles/tiles.component";
import { CharactercardComponent } from "../../shared/charactercard/charactercard.component";



@Component({
    selector: 'team-making',
    imports:[Tiles, CharactercardComponent],
    templateUrl: './team-making.component.html',
    styleUrl: './team-making.component.css'
})

export class TeamMaking {

}