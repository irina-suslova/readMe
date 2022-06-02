package com.example.readme.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.text.set
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.readme.R
import org.json.JSONException
import java.security.MessageDigest


class SettingsFragment : Fragment(), View.OnClickListener {
    private val JSON_URL = "https://000readme.000webhostapp.com/";
    private val APP_PREFERENCES = "current_state";

    lateinit var linearLayoutSettings : LinearLayout;
    lateinit var switchTheme : SwitchCompat;

    lateinit var imageButtonName : ImageButton;
    lateinit var imageButtonEmail : ImageButton;
    lateinit var imageButtonPassword : ImageButton;

    lateinit var editTextSettingsPersonName : EditText;
    lateinit var editTextSettingsPersonEmail : EditText;
    lateinit var editTextSettingsPersonPassword : EditText;
    lateinit var editTextSettingsPersonPasswordConform : EditText;

    lateinit var mRequestQueue : RequestQueue;

    private lateinit var preferences: SharedPreferences

    var userId = -1;        // -1 - не аврторизован

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("userId", userId);
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            userId = savedInstanceState.getInt("userId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutSettings = view.findViewById(R.id.linearLayoutSettings)
        switchTheme = view.findViewById(R.id.switchTheme)

        imageButtonName = view.findViewById(R.id.imageButtonName)
        imageButtonEmail = view.findViewById(R.id.imageButtonEmail)
        imageButtonPassword = view.findViewById(R.id.imageButtonPassword)

        imageButtonName.setOnClickListener(this)
        imageButtonEmail.setOnClickListener(this)
        imageButtonPassword.setOnClickListener(this)

        editTextSettingsPersonName = view.findViewById(R.id.editTextSettingsPersonName)
        editTextSettingsPersonEmail = view.findViewById(R.id.editTextSettingsPersonEmail)
        editTextSettingsPersonPassword = view.findViewById(R.id.editTextSettingsPersonPassword)
        editTextSettingsPersonPasswordConform = view.findViewById(R.id.editTextSettingsPersonPasswordConform)

        preferences = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (preferences.contains("userId")) {
            userId = preferences.getInt("userId", userId)
        }

        initState()

        mRequestQueue = Volley.newRequestQueue(getActivity()?.getApplicationContext());

        switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val editor = preferences.edit()
        editor.putInt("userId", userId)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        if (preferences.contains("userId")) {
            userId = preferences.getInt("userId", userId)
        }
        val currentNightMode = (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> switchTheme.isChecked=false
            Configuration.UI_MODE_NIGHT_YES -> switchTheme.isChecked=true
        }

    }

    private fun initState() {
        linearLayoutSettings.isVisible = userId != -1
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imageButtonName -> sendName();
            R.id.imageButtonEmail -> sendEmail();
            R.id.imageButtonPassword -> sendPassword();
        }
    }

    private fun sendName() {
        if (editTextSettingsPersonName.text.toString().isEmpty()) {
            Toast.makeText(activity, "Необходимо заполнить поле", Toast.LENGTH_SHORT).show()
            return
        }
        val name = editTextSettingsPersonName.text.toString()
        val request = JsonObjectRequest(
            Request.Method.GET,
            JSON_URL + "settings.php?change_name&name=$name&user_id=$userId", null, { response ->
                try {
                    val duplicate = response.getBoolean("duplicate")
                    if (duplicate) {
                        Toast.makeText(
                            activity,
                            "Имя должно отличаться от текущего",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        editTextSettingsPersonName.text.clear()
                        Toast.makeText(activity, "Имя изменено", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }

    private fun sendEmail() {
        if (editTextSettingsPersonEmail.text.toString().isEmpty()) {
            Toast.makeText(activity, "Необходимо заполнить поле", Toast.LENGTH_SHORT).show()
            return
        }
        val email = editTextSettingsPersonEmail.text.toString()
        val request = JsonObjectRequest(
            Request.Method.GET,
            JSON_URL + "settings.php?change_email&email=$email&user_id=$userId", null, { response ->
                try {
                    val duplicate = response.getBoolean("duplicate")
                    if (duplicate)
                        Toast.makeText(activity, "Данный Email занят", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(activity, "Email изменен", Toast.LENGTH_SHORT).show()
                        editTextSettingsPersonEmail.text.clear()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }

    private fun sendPassword() {
        if (editTextSettingsPersonPassword.text.toString().length != editTextSettingsPersonPasswordConform.text.toString().length) {
            Toast.makeText(activity, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            return
        }
        if (editTextSettingsPersonPassword.text.toString().length < 8) {
            Toast.makeText(activity, "Минимальная длина пароля - 8 символов", Toast.LENGTH_SHORT).show()
            return
        }
        val passwordFromForm = editTextSettingsPersonPassword.text.toString()
        val md = MessageDigest.getInstance("MD5")
        val password = md.digest(passwordFromForm.toByteArray()).joinToString("") { "%02x".format(it) }
        val request = JsonObjectRequest(
            Request.Method.GET,
            JSON_URL + "settings.php?change_password&password=$password&user_id=$userId", null, { response ->
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        Toast.makeText(activity, "Пароль изменен", Toast.LENGTH_SHORT).show()
                        editTextSettingsPersonPassword.text.clear()
                        editTextSettingsPersonPasswordConform.text.clear()
                    }
                    else {
                        Toast.makeText(activity, "Что-то пошло не так, попробуйте позже", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }


}