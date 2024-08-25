package com.example.health_connect

class User {

    var email:String?=null

    var Name:String?=null

    var uid : String?=null

    var profileurl : String?=null
    constructor(){}
    constructor(Name:String?,email:String? , uid : String?=null , profileurl : String?=null)
    {
        this.Name = Name
        this.email = email
        this.uid = uid
        this.profileurl = profileurl
    }
}
