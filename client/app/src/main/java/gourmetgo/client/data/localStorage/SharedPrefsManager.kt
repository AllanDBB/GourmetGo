package gourmetgo.client.data.localStorage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import gourmetgo.client.data.models.User

class SharedPrefsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("GourmetGoPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // TOKEN
    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

     fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    // USER
    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString("user_data", userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString("user_data", null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun logout() {
        prefs.edit()
            .remove("auth_token")
            .remove("user_data")
            .apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}