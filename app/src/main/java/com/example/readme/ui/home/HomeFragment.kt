package com.example.readme.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.readme.R
import org.json.JSONException


class HomeFragment : Fragment(), View.OnClickListener {
    private val JSON_URL = "https://000readme.000webhostapp.com/";
    private val APP_PREFERENCES = "current_state";

    var nameArticle = "Name of an article";
    var sourceArticle = "Source of the article";
    var textArticle = "Text of the article";

    lateinit var nameTextView : TextView;
    lateinit var textTextView : TextView;
    lateinit var sourceTextView : TextView;

    lateinit var previousButton : Button;
    lateinit var nextButton : Button;
    lateinit var radioGroup: RadioGroup
    lateinit var radioButtonLike: RadioButton
    lateinit var radioButtonDislike: RadioButton

    lateinit var mRequestQueue : RequestQueue;

    private lateinit var preferences: SharedPreferences

    var watchedTexts = mutableListOf<Int>();
    var currentText = 1;

    var userId = -1;        // -1 - не аврторизован

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("nameArticle", nameArticle);
        outState.putString("sourceArticle", sourceArticle);
        outState.putString("textArticle", textArticle);
        outState.putInt("currentText", currentText);
        outState.putInt("userId", userId);
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            nameArticle = savedInstanceState.getString("nameArticle").toString()
            sourceArticle = savedInstanceState.getString("sourceArticle").toString()
            textArticle = savedInstanceState.getString("textArticle").toString()
            currentText = savedInstanceState.getInt("currentText")
            userId = savedInstanceState.getInt("userId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState);
        nameTextView = view.findViewById(R.id.textViewArticleName);
        sourceTextView = view.findViewById(R.id.textViewArticleSource);
        textTextView = view.findViewById(R.id.textViewArticleText);

        previousButton = view.findViewById(R.id.buttonPrevious)
        nextButton = view.findViewById(R.id.buttonNext)
        previousButton.setOnClickListener(this)
        nextButton.setOnClickListener(this)
        radioGroup = view.findViewById(R.id.radioGroup)
        radioButtonLike = view.findViewById(R.id.radioButtonLike)
        radioButtonDislike = view.findViewById(R.id.radioButtonDislike)

        preferences = requireActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        if (preferences.contains("nameArticle")) {
            nameArticle = preferences.getString("nameArticle", nameArticle).toString();
        }
        if (preferences.contains("sourceArticle")) {
            sourceArticle = preferences.getString("sourceArticle", sourceArticle).toString();
        }
        if (preferences.contains("textArticle")) {
            textArticle = preferences.getString("textArticle", textArticle).toString();
        }
        if (preferences.contains("currentText")) {
            currentText = preferences.getInt("currentText", currentText)
        }
        if (preferences.contains("userId")) {
            userId = preferences.getInt("userId", userId)
        }

        watchedTexts.add(currentText);

        mRequestQueue = Volley.newRequestQueue(getActivity()?.getApplicationContext());
        getTextById(JSON_URL, currentText)
        setValues();

        sourceTextView.setOnClickListener {view ->
            var link = JSON_URL + "share.php?text_id=$currentText";

            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Label", link)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(activity, "Ссылка скопирована в буфер обмена", Toast.LENGTH_SHORT).show()
        }

    }

    private fun getPreviousText() {
        sendAction(JSON_URL)
        var currentTextId = watchedTexts.indexOf(currentText)
        if (currentTextId >= 1) {
            getTextById(JSON_URL, watchedTexts[currentTextId - 1])
        }
    }

    private fun getNextText() {
        sendAction(JSON_URL)
        var currentTextId = watchedTexts.indexOf(currentText)
        if (currentTextId == watchedTexts.size - 1) {
            getText(JSON_URL)
            setValues()
            radioGroup.clearCheck()
        }
        else {
            getTextById(JSON_URL, watchedTexts[currentTextId + 1])
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buttonPrevious -> getPreviousText();
            R.id.buttonNext -> getNextText();
        }
    }

    override fun onPause() {
        super.onPause()
        val editor = preferences.edit()
        editor.putString("nameArticle", nameArticle)
        editor.putString("sourceArticle", sourceArticle)
        editor.putString("textArticle", textArticle)
        editor.putInt("currentText", currentText)
        //editor.putInt("userId", userId)
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
        if (preferences.contains("nameArticle")) {
            nameArticle = preferences.getString("nameArticle", nameArticle).toString();
        }
        if (preferences.contains("sourceArticle")) {
            sourceArticle = preferences.getString("sourceArticle", sourceArticle).toString();
        }
        if (preferences.contains("textArticle")) {
            textArticle = preferences.getString("textArticle", textArticle).toString();
        }
        if (preferences.contains("currentText")) {
            currentText = preferences.getInt("currentText", currentText)
        }
        if (preferences.contains("userId")) {
            userId = preferences.getInt("userId", userId)


        }
    }

    private fun setValues() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            nameTextView.text = Html.fromHtml(nameArticle, Html.FROM_HTML_MODE_LEGACY);
            sourceTextView.text = Html.fromHtml(sourceArticle, Html.FROM_HTML_MODE_LEGACY);
            textTextView.text = Html.fromHtml(textArticle, Html.FROM_HTML_MODE_LEGACY);
        }
        else {
            nameTextView.text = Html.fromHtml(nameArticle);
            sourceTextView.text = Html.fromHtml(sourceArticle);
            textTextView.text = Html.fromHtml(textArticle);
        }
    }

    private fun setAction(action : Int) {
        // 0 - ничего
        // 1 - like
        // 2 - dislike
        when (action) {
            0 -> radioGroup.clearCheck()
            1 -> {
                radioButtonLike.isChecked = true
                radioButtonDislike.isChecked = false
            }
            2 -> {
                radioButtonDislike.isChecked = true
                radioButtonLike.isChecked = false
            }
        }
    }

    private fun getText(url: String) {
        var extra_url : String
        if (userId == -1) {
            extra_url = "texts.php?get_text_by_id=${currentText + 1}"
        }
        else {
            extra_url = "texts.php?get_text&user_id=$userId"
        }
        val request = JsonObjectRequest(
            Request.Method.GET,  //GET - API-запрос для получение данных
            url + extra_url, null, { response ->
                try {
                    val textObject = response.getJSONObject("textObject")
                    currentText = textObject.getInt("id")
                    watchedTexts.add(currentText)
                    if (watchedTexts.size > 5)
                        watchedTexts.removeAt(0)
                    nameArticle = textObject.getString("name")
                    sourceArticle = textObject.getString("source")
                    textArticle = textObject.getString("text")
                    setValues() // создадим метод setValues для присваивания значений переменным
                    setAction(0)
                } catch (e: JSONException) {

                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request) // добавляем запрос в очередь
    }

    private fun getTextById(url: String, id: Int) {
        val request = JsonObjectRequest(
            Request.Method.GET,
            url + "texts.php?get_text_by_id=$id", null, { response ->
                try {
                    val textObject = response.getJSONObject("textObject")

                    currentText = textObject.getInt("id")
                    nameArticle = textObject.getString("name")
                    sourceArticle = textObject.getString("source")
                    textArticle = textObject.getString("text")
                    setValues()
                    getAction(JSON_URL)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }

    private fun getAction(url: String) {
        if (userId == -1) setAction(0)
        val request = JsonObjectRequest(
            Request.Method.GET,
            url + "actions.php?get_action&user_id=$userId&text_id=$currentText", null, { response ->
                try {
                    val actionObject = response.getJSONObject("actionObject")
                    val action = actionObject.getInt("action")
                    setAction(action)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }

    private fun sendAction(url: String) {
        if (userId == -1) return
        var action = CheckAction()
        val request = JsonObjectRequest(
            Request.Method.GET,
            url + "actions.php?set_action&user_id=$userId&text_id=$currentText&action=$action", null, { _ ->
                try {
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error ->
            error.printStackTrace()
        }
        mRequestQueue.add(request)
    }

    private fun CheckAction() : Int {
        if (radioButtonLike.isChecked == true) return 1
        if (radioButtonDislike.isChecked == true) return 2
        return 0
    }
}