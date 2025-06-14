package gourmetgo.client.data.localStorage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
    }    //Chef
    fun saveChef(chef: Chef) {
        Log.d("SharedPrefsManager", "Saving chef: $chef")
        Log.d("SharedPrefsManager", "Chef _id: '${chef._id}'")
        val chefJson = gson.toJson(chef)
        Log.d("SharedPrefsManager", "Chef JSON: $chefJson")
        prefs.edit().putString("chef_data", chefJson).apply()
        Log.d("SharedPrefsManager", "Chef saved successfully")
    }fun getChef(): Chef? {
        val chefJson = prefs.getString("chef_data", null)
        Log.d("SharedPrefsManager", "chefJson: $chefJson")
        return if (chefJson != null) {
            try {
                val chef = gson.fromJson(chefJson, Chef::class.java)
                Log.d("SharedPrefsManager", "Parsed chef: $chef")
                Log.d("SharedPrefsManager", "Chef _id: '${chef._id}'")
                chef
            } catch (e: Exception) {
                Log.e("SharedPrefsManager", "Error parsing chef JSON", e)
                null
            }
        } else {
            Log.w("SharedPrefsManager", "No chef data found in SharedPrefs")
            null
        }
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

    // Método para verificar y debuggear el estado actual
    fun debugUserData(): String {
        val token = getToken()
        val user = getUser()
        val chef = getChef()
        val client = getClient()
        
        return buildString {
            appendLine("=== SharedPrefs Debug Info ===")
            appendLine("Token: ${if (token != null) "EXISTS (${token.take(20)}...)" else "NULL"}")
            appendLine("User: $user")
            appendLine("Chef: $chef")
            appendLine("Chef _id: '${chef?._id ?: "NULL"}'")
            appendLine("Client: $client")
            appendLine("Client id: '${client?.id ?: "NULL"}'")
            appendLine("IsLoggedIn: ${isLoggedIn()}")
            appendLine("==============================")
        }
    }

    // Método para limpiar datos corruptos y forzar re-login
    fun clearCorruptedData() {
        Log.w("SharedPrefsManager", "Clearing potentially corrupted data")
        clearAll()
    }

    fun clearChefData() {
        Log.d("SharedPrefsManager", "Clearing chef data...")
        prefs.edit().remove("chef_data").apply()
        Log.d("SharedPrefsManager", "Chef data cleared")
    }
}