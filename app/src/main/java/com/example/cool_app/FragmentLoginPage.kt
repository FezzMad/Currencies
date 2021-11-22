package com.example.cool_app

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_login_page.*

class FragmentLoginPage : Fragment(R.layout.fragment_login_page) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // для сохранения/загрузки наличия авторизации
        val myPreferenced: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
        val myEditor: SharedPreferences.Editor = myPreferenced.edit()

        // переменная авторизации
        var isLogin: Boolean = myPreferenced.getBoolean("ISLOGIN", false)


        if (isLogin) {
            // пользователь уже авторизирован
            findNavController().navigate(R.id.action_fragmentLoginPage_to_fragmentIncreasedExchangeRate)
        } else {
            // пользователь ещё авторизирован
            buttonLogin.setOnClickListener {
                val email = viewEmail.text.toString()
                val password = viewPassword.text.toString()
                if (email == "mda200@mail.ru" && password == "1") {
                    isLogin = true
                    myEditor.putBoolean("ISLOGIN", isLogin)
                    myEditor.apply()
                    findNavController().navigate(R.id.action_fragmentLoginPage_to_fragmentIncreasedExchangeRate)
                }
            }
        }
    }

}