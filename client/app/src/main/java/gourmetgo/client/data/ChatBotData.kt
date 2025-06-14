package gourmetgo.client.data

data class ChatMessage(
    val id: String,
    val text: String,
    val isFromBot: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatBotQuestion(
    val id: String,
    val question: String,
    val answer: String
)

object ChatBotData {
    val predefinedQuestions = listOf(
        ChatBotQuestion(
            id = "1",
            question = "¿Cómo puedo reservar una experiencia culinaria?",
            answer = "Para reservar una experiencia:\n\n1. Ve a la sección 'Experiencias'\n2. Selecciona la experiencia que te interese\n3. Revisa los detalles y horarios disponibles\n4. Haz clic en 'Reservar'\n5. Completa el proceso de pago\n\n¡Y listo! Recibirás una confirmación por email."
        ),
        ChatBotQuestion(
            id = "2", 
            question = "¿Qué tipos de experiencias culinarias hay disponibles?",
            answer = "En GourmetGo encontrarás una gran variedad:\n\n• Clases de cocina italiana\n• Experiencias de cocina asiática\n• Talleres de repostería\n• Cenas temáticas\n• Catas de vinos y maridajes\n• Cocina vegana y vegetariana\n• Experiencias de cocina molecular\n\n¡Algo para todos los gustos!"
        ),
        ChatBotQuestion(
            id = "3",
            question = "¿Puedo cancelar mi reserva?",
            answer = "Sí, puedes cancelar tu reserva:\n\n• Hasta 24 horas antes: Reembolso completo\n• Entre 12-24 horas: Reembolso del 50%\n• Menos de 12 horas: Sin reembolso\n\nPara cancelar, ve a 'Mi Historial' y selecciona la reserva que deseas cancelar."
        ),
        ChatBotQuestion(
            id = "4",
            question = "¿Cómo puedo contactar a un chef?",
            answer = "Puedes contactar a un chef de varias formas:\n\n1. A través del chat en la experiencia\n2. En la sección de comentarios\n3. Enviando un mensaje directo desde su perfil\n4. Durante la experiencia en vivo\n\nTodos nuestros chefs están comprometidos a responder rápidamente."
        ),
        ChatBotQuestion(
            id = "5",
            question = "¿Qué necesito para participar en una experiencia virtual?",
            answer = "Para experiencias virtuales necesitas:\n\n📱 Dispositivo con cámara y micrófono\n🌐 Conexión estable a internet\n🍳 Ingredientes (se envía lista previa)\n👨‍🍳 Utensilios básicos de cocina\n📍 Espacio cómodo para cocinar\n\n¡Te enviaremos todos los detalles al confirmar tu reserva!"
        ),
        ChatBotQuestion(
            id = "6",
            question = "¿Cómo funcionan las valoraciones y reseñas?",
            answer = "Nuestro sistema de valoraciones es simple:\n\n⭐ Califica de 1 a 5 estrellas\n💬 Escribe una reseña opcional\n📸 Comparte fotos de tu experiencia\n🏆 Ayuda a otros usuarios a decidir\n\nPuedes valorar después de completar la experiencia. ¡Tu opinión es muy valiosa!"
        ),
        ChatBotQuestion(
            id = "7",
            question = "¿Hay algún costo adicional por usar la plataforma?",
            answer = "¡No hay costos ocultos!\n\n✅ El registro es completamente gratuito\n✅ Solo pagas por las experiencias que reservas\n✅ Sin comisiones adicionales para usuarios\n✅ Precios transparentes siempre\n\nLo que ves es lo que pagas. ¡Así de simple!"
        ),
        ChatBotQuestion(
            id = "8",
            question = "¿Qué métodos de pago aceptan?",
            answer = "Aceptamos múltiples formas de pago:\n\n💳 Tarjetas de crédito y débito\n📱 PayPal\n💰 Transferencias bancarias\n📲 Pagos móviles\n\nTodos los pagos son seguros y están encriptados. Tu información financiera está protegida."
        )
    )
}
