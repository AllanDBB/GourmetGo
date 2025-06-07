package gourmetgo.client.data.localStorage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.User
import gourmetgo.client.data.models.Chef

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

    //Chef
    fun saveChef(chef: Chef) {
        val chefJson = gson.toJson(chef)
        prefs.edit().putString("chef_data", chefJson).apply()
    }

    fun getChef(): Chef? {
        val chefJson = prefs.getString("chef_data", null)
        return if (chefJson != null) {
            gson.fromJson(chefJson, Chef::class.java)
        } else null
    }

    //Client
    fun saveClient(client: Client) {
        val chefJson = gson.toJson(client)
        prefs.edit().putString("client_data", chefJson).apply()
    }

    fun getClient(): Client? {
        val clientJson = prefs.getString("client_data", null)
        return if (clientJson != null) {
            gson.fromJson(clientJson, Client::class.java)
        } else null
    }

    //Additional
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    private fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun logout() {
        clearAll()
    }


}