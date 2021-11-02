package com.sarco.userlistrest.mainModule

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.sarco.userlistrest.R
import com.sarco.userlistrest.UsersListApplication
import com.sarco.userlistrest.common.utils.Constants
import com.sarco.userlistrest.databinding.ActivityMainBinding
import com.sarco.userlistrest.userListModule.UsersListFragment
import org.json.JSONObject

class MainActivity : AppCompatActivity(), MainAux {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.swType.setOnCheckedChangeListener { button, isChecked ->
            button.text = if(isChecked)
                getString(R.string.main_text_login) else getString(R.string.main_text_register)
            mBinding.btnLogin.text = button.text
        }

        mBinding.btnLogin.setOnClickListener {
            login()
        }

        mBinding.btnUserList.setOnClickListener{
            openUserList()
        }
    }

    private fun openUserList() {
        val userListFragment = UsersListFragment()

        val mFragmentManager = supportFragmentManager
        val fragmentTransaction = mFragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, userListFragment).addToBackStack(null).commit()
    }

    private fun login() {
        val swType = if(mBinding.swType.isChecked) Constants.LOGIN_PATH else Constants.REGISTER_PATH

        val url = Constants.BASE_URL + Constants.API_PATH + swType

        val jsonParams = JSONObject()
        val email = mBinding.etEmail.text.toString().trim()
        if(email.isNotEmpty()){
            jsonParams.put(Constants.EMAIL_PARAM, email)
        }
        val password = mBinding.etPassword.text.toString().trim()
        if(password.isNotEmpty()){
            jsonParams.put(Constants.PASSWORD_PARAM, password)
        }

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, { response ->

            val id = response.optString(Constants.ID_PROP,  Constants.ERROR_VALUE)
            val token = response.optString(Constants.TOKEN_PROP,  Constants.ERROR_VALUE)

            val result = if(id.equals(Constants.ERROR_VALUE)) "${Constants.TOKEN_PROP} : $token"
            else "${Constants.ID_PROP}: $id ,${Constants.TOKEN_PROP} : $token"
            updateUI(result)
        }, {
            it.printStackTrace()
            if(it.networkResponse.statusCode == 400){
                updateUI(getString(R.string.main_network_error))
        }
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                return params
            }
        }

        UsersListApplication.reqResAPI.addToRequestQueue(jsonObjectRequest)
    }

    private fun updateUI(result: String) {
        mBinding.tvUserInfo.text = result
        mBinding.tvUserInfo.visibility = View.VISIBLE
    }

    override fun buttonListVisible() {
        mBinding.btnUserList.visibility = if(mBinding.btnUserList.visibility == View.GONE)
            View.VISIBLE
            else View.GONE
    }
}