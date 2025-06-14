package gourmetgo.client.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import gourmetgo.client.data.ChatMessage
import gourmetgo.client.data.ChatBotData
import java.util.*

data class ChatBotUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val currentUserMessage: String = ""
)

class ChatBotViewModel : ViewModel() {
    var uiState by mutableStateOf(ChatBotUiState())
        private set

    init {
        // Mensaje de bienvenida
        addBotMessage("¡Hola! 👋 Soy tu asistente virtual de GourmetGo. ¿En qué puedo ayudarte hoy?\n\nPuedes preguntarme sobre:\n• Cómo hacer reservas\n• Tipos de experiencias\n• Cancelaciones\n• Métodos de pago\n• ¡Y mucho más!")
    }

    fun updateUserMessage(message: String) {
        uiState = uiState.copy(currentUserMessage = message)
    }

    fun sendMessage() {
        val userMessage = uiState.currentUserMessage.trim()
        if (userMessage.isEmpty()) return

        // Añadir mensaje del usuario
        addUserMessage(userMessage)
        
        // Limpiar input
        uiState = uiState.copy(currentUserMessage = "")
        
        // Simular que el bot está escribiendo
        uiState = uiState.copy(isTyping = true)
        
        // Buscar respuesta del bot (simular delay)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val botResponse = findBotResponse(userMessage)
            addBotMessage(botResponse)
            uiState = uiState.copy(isTyping = false)
        }, 1500)
    }

    private fun addUserMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isFromBot = false
        )
        uiState = uiState.copy(messages = uiState.messages + message)
    }

    private fun addBotMessage(text: String) {
        val message = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = text,
            isFromBot = true
        )
        uiState = uiState.copy(messages = uiState.messages + message)
    }

    private fun findBotResponse(userMessage: String): String {
        val message = userMessage.lowercase()
        
        // Buscar coincidencias en las preguntas predefinidas
        val matchingQuestion = ChatBotData.predefinedQuestions.find { question ->
            val keywords = extractKeywords(question.question.lowercase())
            keywords.any { keyword -> message.contains(keyword) }
        }
        
        return matchingQuestion?.answer ?: getDefaultResponse()
    }
    
    private fun extractKeywords(question: String): List<String> {
        return when {
            question.contains("reservar") || question.contains("reserva") -> 
                listOf("reservar", "reserva", "booking", "book")
            question.contains("experiencia") || question.contains("tipos") -> 
                listOf("experiencia", "experiencias", "tipos", "tipo", "clases", "talleres")
            question.contains("cancelar") || question.contains("cancelación") -> 
                listOf("cancelar", "cancelación", "cancel", "devolver", "reembolso")
            question.contains("chef") || question.contains("contactar") -> 
                listOf("chef", "contactar", "contacto", "mensaje", "comunicar")
            question.contains("virtual") || question.contains("necesito") -> 
                listOf("virtual", "necesito", "requisitos", "qué necesito", "materiales")
            question.contains("valoraciones") || question.contains("reseñas") -> 
                listOf("valoraciones", "reseñas", "rating", "calificar", "comentarios")
            question.contains("costo") || question.contains("precio") -> 
                listOf("costo", "precio", "gratis", "dinero", "pagar", "coste")
            question.contains("pago") || question.contains("métodos") -> 
                listOf("pago", "métodos", "tarjeta", "paypal", "payment")
            else -> listOf()
        }
    }
    
    private fun getDefaultResponse(): String {
        val responses = listOf(
            "¡Interesante pregunta! 🤔 Aunque no tengo una respuesta específica para eso, puedo ayudarte con:\n\n• Información sobre reservas\n• Tipos de experiencias culinarias\n• Procesos de cancelación\n• Métodos de pago\n• Contacto con chefs\n\n¿Te gustaría saber algo sobre estos temas?",
            
            "No estoy seguro de cómo responder a eso específicamente, pero puedo contarte sobre:\n\n🍳 Nuestras experiencias culinarias\n📅 Cómo hacer reservas\n💳 Opciones de pago\n⭐ Sistema de valoraciones\n\n¿Hay algo de esto que te interese?",
            
            "¡Hmm! 🤖 Esa es una pregunta que no tengo programada, pero soy experto en:\n\n• Guiarte en el proceso de reservas\n• Explicar tipos de experiencias\n• Información sobre cancelaciones\n• Ayuda con pagos y métodos\n\n¿Puedo ayudarte con alguno de estos temas?"
        )
        return responses.random()
    }
    
    fun getQuickQuestions(): List<String> {
        return listOf(
            "¿Cómo reservar?",
            "Tipos de experiencias",
            "¿Puedo cancelar?",
            "Métodos de pago"
        )
    }
    
    fun sendQuickQuestion(question: String) {
        addUserMessage(question)
        uiState = uiState.copy(isTyping = true)
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val response = when (question) {
                "¿Cómo reservar?" -> ChatBotData.predefinedQuestions[0].answer
                "Tipos de experiencias" -> ChatBotData.predefinedQuestions[1].answer
                "¿Puedo cancelar?" -> ChatBotData.predefinedQuestions[2].answer
                "Métodos de pago" -> ChatBotData.predefinedQuestions[7].answer
                else -> getDefaultResponse()
            }
            addBotMessage(response)
            uiState = uiState.copy(isTyping = false)
        }, 1000)
    }
}
