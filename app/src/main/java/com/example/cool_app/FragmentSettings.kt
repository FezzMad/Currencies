package com.example.cool_app

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.*


class FragmentSettings : Fragment(R.layout.fragment_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val number = if (viewNumber.text.isNotEmpty() && viewNumber.text != null)
//         viewNumber.text.toString() else "2.0"
//        val myEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
//        myEditor.putString("NUMBER", number)
//        myEditor.apply()
    }

}