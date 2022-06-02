package com.example.readme.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.readme.R
import org.json.JSONException
import java.security.MessageDigest


class ProfileFragment : Fragment(), View.OnClickListener {
    private val JSON_URL = "https://000readme.000webhostapp.com/";
    private val APP_PREFERENCES = "current_state";

    var userName = "Your Name";
    var userNick = "Your Nick";

    lateinit var linearLayoutProfile : LinearLayout;
    lateinit var relativeLayoutLogin : RelativeLayout;
    lateinit var recyclerViewFavourites : RecyclerView;

    lateinit var textViewUserNick : TextView;
    lateinit var textViewUserName : TextView;

    lateinit var buttonRegister : Button;
    lateinit var buttonSingIn : Button;
    lateinit var buttonLogOut : ImageButton;

    lateinit var mRequestQueue : RequestQueue;

    private lateinit var preferences: SharedPreferences

    var userId = -1;        // -1 - не аврторизован

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userName", userName);
        outState.putString("userNick", userNick);
        outState.putInt("userId", userId);
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            userName = savedInstanceState.getString("userName").toString()
            userNick = savedInstanceState.getString("userNick").toString()
            userId = savedInstanceState.getInt("userId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutProfile = view.findViewById(R.id.linearLayoutProfile)
        relativeLayoutLogin = view.findViewById(R.id.relativeLayoutLogin)
        recyclerViewFavourites = view.findViewById(R.id.recyclerViewFavourites)

        textViewUserNick = view.findViewById(R.id.textViewUserNick)
        textViewUserName = view.findViewById(R.id.textViewUserName)

        buttonRegister = view.findViewById(R.id.buttonRegister)
        buttonSingIn = view.findViewById(R.id.buttonSingIn)
        buttonLogOut = view.findViewById(R.id.buttonLogOut)
        buttonSingIn.setOnClickListener(this)
        buttonRegister.setOnClickListener(this)
        buttonLogOut.setOnClickListener(this)

        preferences = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (preferences.contains("userName")) {
            userName = preferences.getString("userName", userName).toString();
        }
        if (preferences.contains("userNick")) {
            userNick = preferences.getString("userNick", userNick).toString();
        }
        if (preferences.contains("userId")) {
            userId = preferences.getInt("userId", userId)
        }

        initState()
        setValues()

        mRequestQueue = Volley.newRequestQueue(getActivity()?.getApplicationContext());

        recyclerViewFavourites.layoutManager = LinearLayoutManager(activity)
        recyclerViewFavourites.adapter = CustomRecyclerFavouritesAdapter(fillList(JSON_URL))
    }

    private fun fillList(url: String): MutableList<List<String>> {
        var likes: MutableList<List<String>> = mutableListOf();
        var checker = false
        val request = JsonObjectRequest(
            Request.Method.GET,
            url + "likes.php?get_likes&user_id=$userId", null, { response ->
                try {
                    val LikesArray = response.getJSONArray("liked_texts_names")
                    val LikesArrayTexts = response.getJSONArray("liked_texts")
                    val LikesArrayIDs = response.getJSONArray("liked_texts_ids")
                    likes[0] = LikesArray.join("\n").split("\n")
                    likes.add(LikesArrayTexts.join("\n").split("\n"));
                    likes.add(LikesArrayIDs.join("\n").split("\n"));
                    checker = true
                    recyclerViewFavourites.adapter = CustomRecyclerFavouritesAdapter(likes)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
        if (!checker) likes.add(emptyList())
        return likes
    }

    override fun onPause() {
        super.onPause()
        val editor = preferences.edit()
        editor.putString("userName", userName)
        editor.putString("userNick", userNick)
        editor.putInt("userId", userId)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        if (preferences.contains("userName")) {
            userName = preferences.getString("userName", userName).toString();
        }
        if (preferences.contains("userNick")) {
            userNick = preferences.getString("userNick", userNick).toString();
        }
        if (preferences.contains("userId")) {
            userId = preferences.getInt("userId", userId)
        }
        getValues()
    }

    private fun initState() {
        if (userId == -1) {
            linearLayoutProfile.isVisible = false
            relativeLayoutLogin.isVisible = true
        }
        else {
            linearLayoutProfile.isVisible = true
            relativeLayoutLogin.isVisible = false
        }
    }

    private fun setValues() {
        if (userId != -1) {
            linearLayoutProfile.isVisible = true
            relativeLayoutLogin.isVisible = false
            textViewUserNick.text = userNick
            textViewUserName.text = userName
        }
    }

    private fun getValues() {
        if (userId != -1) {
            textViewUserNick.text = userNick
            textViewUserName.text = userName
            val request = JsonObjectRequest(
                    Request.Method.GET,
            JSON_URL + "users.php?get_user&user_id=$userId", null, { response ->
                try {
                    val userObject = response.getJSONObject("userObject")
                    if (userObject.getBoolean("user") and userObject.getBoolean("success")) {
                        userName = userObject.getString("name")
                        userNick = userObject.getString("nick")
                        textViewUserNick.text = userNick
                        textViewUserName.text = userName
                        setValues()
                    }
                    else {
                        Toast.makeText(activity, "Ошибка при подключении к серверу", Toast.LENGTH_SHORT).show()
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonRegister -> showRegisterWindow();
            R.id.buttonSingIn -> showSignInWindow();
            R.id.buttonLogOut -> logOut();
        }
    }

    private fun logOut() {
        userId = -1
        initState()
    }

    private fun showSignInWindow() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Вход")
        dialog.setMessage("Введите данные для входа")

        val inflater = LayoutInflater.from(activity)
        val v = inflater.inflate(R.layout.singin_window, null)
        dialog.setView(v)

        val editTextTextEmailAddress : EditText = v.findViewById(R.id.editTextTextEmailAddress)
        val editTextTextPassword : EditText = v.findViewById(R.id.editTextTextPassword)

        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.cancel()
            })
        dialog.setPositiveButton("Войти",
            DialogInterface.OnClickListener { dialog, id ->
                if (editTextTextEmailAddress.text.toString().isEmpty()) {
                    Toast.makeText(activity, "Для входа необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
                }
                else if (editTextTextPassword.text.toString().isEmpty()) {
                    Toast.makeText(activity, "Для входа необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
                }
                else {
                    makeSignIn(JSON_URL, editTextTextEmailAddress.text.toString(), editTextTextPassword.text.toString())
                    setValues()
                }
            })
        dialog.show()
    }

    private fun makeSignIn(url: String, email: String, passwordFromForm: String) {
        val md = MessageDigest.getInstance("MD5")
        val password = md.digest(passwordFromForm.toByteArray()).joinToString("") { "%02x".format(it) }
        val request = JsonObjectRequest(
            Request.Method.GET,
            url + "users.php?sign_in&email=$email&password=$password", null, { response ->
                try {
                    val userObject = response.getJSONObject("userObject")
                    if (userObject.getBoolean("user") == false)
                        Toast.makeText(activity, "Аккаунта с данным email не существует, пройдите регистрацию", Toast.LENGTH_SHORT).show()
                    else if (userObject.getBoolean("success") == false)
                        Toast.makeText(activity, "Неверный пароль", Toast.LENGTH_SHORT).show()
                    else {
                        userId = userObject.getInt("id")
                        userName = userObject.getString("name")
                        userNick = userObject.getString("nick")
                        setValues()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }

    private fun showRegisterWindow() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setTitle("Регистрация")
        dialog.setMessage("Введите все данные для регистрации")
        val inflater = LayoutInflater.from(activity)
        val v = inflater.inflate(R.layout.register_window, null)
        dialog.setView(v)

        val editTextTextPersonName : EditText = v.findViewById(R.id.editTextTextPersonName)
        val editTextTextEmailAddress : EditText = v.findViewById(R.id.editTextTextEmailAddress)
        val editTextDate : EditText = v.findViewById(R.id.editTextDate)
        val radioButtonMale : RadioButton = v.findViewById(R.id.radioButtonMale)
        val radioButtonFemale : RadioButton = v.findViewById(R.id.radioButtonFemale)
        val editTextTextPassword : EditText = v.findViewById(R.id.editTextTextPassword)

        dialog.setNegativeButton("Отменить",
            DialogInterface.OnClickListener { dialogInterface, _ ->
                dialogInterface.cancel()
            })
        dialog.setPositiveButton("Отправить",
            DialogInterface.OnClickListener { dialog, id ->
                if (editTextTextEmailAddress.text.toString().isEmpty()) {
                    Toast.makeText(activity, "Для регистрации необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
                }
                else if (editTextDate.text.toString().isEmpty()) {
                    Toast.makeText(activity, "Для регистрации необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
                }
                else if (editTextTextPersonName.text.toString().isEmpty()) {
                    Toast.makeText(activity, "Для регистрации необходимо заполнить все поля", Toast.LENGTH_SHORT).show()
                }
                else if (editTextTextPassword.text.toString().length < 8) {
                    Toast.makeText(activity, "Минимальная длина пароля - 8 символов", Toast.LENGTH_SHORT).show()
                }
                else {
                    makeRegister(JSON_URL, editTextTextEmailAddress.text.toString(), editTextTextPersonName.text.toString(), editTextDate.text.toString(), getGender(radioButtonMale, radioButtonFemale).toString(), editTextTextPassword.text.toString())
                    setValues()
                }
            })
        dialog.show()
    }

    private fun makeRegister(url: String, email: String, name: String, birthday: String, gender: String, passwordFromForm: String) {
        val md = MessageDigest.getInstance("MD5")
        val password = md.digest(passwordFromForm.toByteArray()).joinToString("") { "%02x".format(it) }
        val request = JsonObjectRequest(
            Request.Method.GET,
            url + "users.php?register&name=$name&email=$email&password=$password&birthday=$birthday&gender=$gender", null, { response ->
                try {
                    val userObject = response.getJSONObject("userObject")
                    if (userObject.getBoolean("duplicate"))
                        Toast.makeText(activity, "Данный пользователь уже зарегестрирован", Toast.LENGTH_SHORT).show()
                    else {
                        userId = userObject.getInt("id")
                        userName = userObject.getString("name")
                        userNick = userObject.getString("nick")
                        setValues()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }

    private fun getGender(radioButtonMale : RadioButton, radioButtonFemale : RadioButton) : Int {
        if (radioButtonMale.isChecked == true) return 1
        if (radioButtonFemale.isChecked == true) return 2
        return 0
    }
}