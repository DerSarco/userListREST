package com.sarco.userlistrest.userListModule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sarco.userlistrest.R
import com.sarco.userlistrest.UsersListApplication
import com.sarco.userlistrest.common.utils.Constants
import com.sarco.userlistrest.databinding.FragmentUsersListBinding
import org.json.JSONObject

/**
 * A simple [Fragment] subclass.
 * Use the [UsersListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UsersListFragment : Fragment() {

    private lateinit var mBinding: FragmentUsersListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentUsersListBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSingleUser()
    }

    private fun getSingleUser() {
        val url = Constants.BASE_URL + Constants.API_PATH + Constants.SELECT_USER_PATH + "/2"

        val jsonObject = JSONObject()

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, { response ->
            Log.i("response", response.toString())
            val data = response.optJSONObject("data")
            if(data != null){
                val avatar = data.optString(Constants.AVATAR_URL_PROP, Constants.ERROR_VALUE)
                val firstName = data.optString(Constants.FIRST_NAME_PROP, Constants.ERROR_VALUE)
                val lastName = data.optString(Constants.LAST_NAME_PROP, Constants.ERROR_VALUE)
                val email = data.optString(Constants.EMAIL_PARAM, Constants.ERROR_VALUE)
                updateUI(avatar, "$firstName $lastName", email)
            }

        },{
            if(it.networkResponse.statusCode == 400){
                Toast.makeText(requireContext(),
                    getString(R.string.error_network_message),
                    Toast.LENGTH_SHORT).show()
            }
        }){
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["Content-Type"] = "application/json"
                return params
            }
        }

        UsersListApplication.reqResAPI.addToRequestQueue(jsonObjectRequest)
    }

    private fun updateUI(avatar: String, name: String, email: String) {

        Glide.with(this)
            .load(avatar)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(mBinding.ivProfile)
        mBinding.tvUserName.text = name
        mBinding.tvEmail.text = email
    }


    override fun onDestroy() {
        super.onDestroy()

    }

}