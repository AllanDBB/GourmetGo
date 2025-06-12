package gourmetgo.client.utils

import android.content.Context
import android.os.Environment
import gourmetgo.client.data.models.dtos.AssistanceResponse
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.pdf.PdfDocument
import android.graphics.Paint

object ViewAssistanceExportUtils {
    fun exportBookingsToCsv(context: Context, bookings: List<AssistanceResponse>): File? {
        val csvHeader = "Nombre,Email,Teléfono,Personas,Método de pago,Estado\n"
        val csvBody = bookings.joinToString("\n") { booking ->
            listOf(
                booking.name ?: "",
                booking.email ?: "",
                booking.phone ?: "",
                booking.people?.toString() ?: "",
                booking.paymentMethod ?: "",
                booking.status ?: ""
            ).joinToString(",")
        }
        val csvContent = csvHeader + csvBody
        val fileName = "asistencia_evento.csv"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        try {
            FileOutputStream(file).use { it.write(csvContent.toByteArray()) }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return file
    }

    fun exportBookingsToPdf(context: Context, bookings: List<AssistanceResponse>): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paintTitle = Paint().apply { textSize = 20f; isFakeBoldText = true }
        val paintHeader = Paint().apply { textSize = 16f; isFakeBoldText = true }
        val paintBody = Paint().apply { textSize = 14f }
        var y = 40
        
        canvas.drawText("Asistencia del evento", 40f, y.toFloat(), paintTitle)
        y += 32
        
        val eventName = bookings.firstOrNull()?.experience?.title ?: ""
        if (eventName.isNotBlank()) {
            canvas.drawText("Evento: $eventName", 40f, y.toFloat(), paintHeader)
            y += 28
        }
        
        canvas.drawText("Listado de asistentes:", 40f, y.toFloat(), paintHeader)
        y += 24
        
        bookings.forEach { booking ->
            val persona = buildString {
                append("Nombre: ${booking.name ?: "-"}")
                append("\nEmail: ${booking.email ?: "-"}")
                append("\nTeléfono: ${booking.phone ?: "-"}")
                append("\nPersonas: ${booking.people ?: "-"}")
                append("\nMétodo de pago: ${booking.paymentMethod ?: "-"}")
                append("\nEstado: ${booking.status ?: "-"}")
            }
            persona.split("\n").forEach { line ->
                canvas.drawText(line, 40f, y.toFloat(), paintBody)
                y += 20
            }
            y += 12 
            if (y > 800) {
                pdfDocument.finishPage(page)
                val newPage = pdfDocument.startPage(pageInfo)
                y = 40
            }
        }
        pdfDocument.finishPage(page)
        val fileName = "asistencia_evento.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
        pdfDocument.close()
        return file
    }
}
