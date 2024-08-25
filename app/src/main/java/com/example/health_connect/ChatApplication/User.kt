package com.example.health_connect

class User {

    var email:String?=null

    var Name:String?=null

    var uid : String?=null
    constructor(){}
    constructor(Name:String?,email:String? , uid : String?=null)
    {
        this.Name = Name
        this.email = email
        this.uid = uid
    }
}
