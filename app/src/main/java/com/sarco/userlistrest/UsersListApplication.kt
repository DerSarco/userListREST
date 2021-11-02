package com.sarco.userlistrest

import android.app.Application
import com.sarco.userlistrest.common.utils.ReqResAPI

class UsersListApplication: Application() {

    companion object {
        lateinit var reqResAPI: ReqResAPI
    }

    override fun onCreate() {
        super.onCreate()

        //Volley
        reqResAPI = ReqResAPI.getInstance(this)
    }

}