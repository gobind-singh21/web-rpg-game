import { Component, EventEmitter, Input, Output } from "@angular/core";
import { CharactercardComponent } from "../charactercard/charactercard.component";
import { CommonModule } from "@angular/common";

@Component({
    selector: 'tiles',
    imports: [CharactercardComponent, CommonModule],
    templateUrl:'./tiles.component.html',
    styleUrl: './tiles.component.css'
})

export class Tiles {
    @Input() name: string = '';
    @Input() description: string = '';
    @Input() characterId: number = 0;
    @Input() character: any; 

    @Output() deleteCharacter = new EventEmitter<number>();

    onDelete() {
        console.log('Delete character with ID:', this.characterId);
        this.deleteCharacter.emit(this.characterId);
    }

    onCharacterSelect(event: any) {
        console.log('Character selected:', event);
    }
}