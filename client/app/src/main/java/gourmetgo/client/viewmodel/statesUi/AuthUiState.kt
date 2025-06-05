package gourmetgo.client.viewmodel.statesUi

import gourmetgo.client.data.models.Client
import gourmetgo.client.data.models.Chef

/**
 * AuthUiState - Estado unificado para autenticación
 * 
 * Maneja tanto usuarios normales (Client) como chefs (Chef)
 * según el resultado del login unificado de la API.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    
    // Usuario actual - puede ser Client o Chef
    val currentUser: Any? = null,           // ← Cambio principal
    val userType: String? = null,           // ← "user" o "chef"
    
    // Helpers para acceso tipado
    val client: Client? = currentUser as? Client,   // ← Computed property
    val chef: Chef? = currentUser as? Chef,         // ← Computed property
    
    // Estados de error
    val error: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)