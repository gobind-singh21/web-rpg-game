import { Component, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms";


@Component({
    selector: 'Login',
    templateUrl: 'Login.html',
    styleUrl: 'Login.css',
    standalone: true,
    imports:[
        FormsModule
    ]
})

export class Login implements OnInit {
    signupUsers: any[] = [];
    signupObj:any = {
        userName: '',
        email: '',
        password: ''
    };
    loginObj: any = {
        userName: '',
        password: ''
    };
    constructor() { }

    ngOnInit(): void {
        const localData = localStorage.getItem('signUpUsers');
        if(localData != null) {
            this.signupUsers = JSON.parse(localData);
        }
    }
    onSignUp() {
        this.signupUsers.push(this.signupObj);
        localStorage.setItem('signUpUsers', JSON.stringify(this.signupUsers));
        this.signupObj = {
            userName: '',
            email: '',
            password: ''
        }
    }
    onLogin() {
        const isUserExist = this.signupUsers.find(m => m.userName == this.loginObj.userName && m.password == this.loginObj.password);
        if(isUserExist != undefined) {
            alert('User Login Successfully');
        }else{
            alert('Wrong credententials')
        }

    }
}