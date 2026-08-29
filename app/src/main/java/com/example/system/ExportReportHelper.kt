package com.example.system

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.GameSession
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object ExportReportHelper {

    fun generateJsonReport(session: GameSession): String {
        val json = JSONObject().apply {
            put("reportId", "SR-${if (session.id != 0L) session.id else session.startTime}")
            put("gameTitle", session.gameTitle)
            put("packageName", session.packageName)
            put("timestampMillis", session.startTime)
            put("durationMinutes", (session.durationMillis / 60000L).coerceAtLeast(1))
            put("overallScore", session.overallScore)
            
            val scores = JSONObject().apply {
                put("pingScore", session.pingScore)
                put("memoryScore", session.memoryScore)
                put("tempScore", session.tempScore)
            }
            put("scores", scores)

            val metrics = JSONObject().apply {
                put("avgLatencyMs", session.avgLatencyMs)
                put("pingJitterMs", session.pingJitterMs)
                put("minFreeRamPercent", session.minFreeRamPercent)
                put("peakBatteryTempC", session.peakBatteryTempC)
            }
            put("metrics", metrics)

            put("summaryText", session.summaryText)
            put("exportedBy", "Game Booster+ Android")
        }
        return json.toString(2)
    }

    fun exportAndShareJson(context: Context, session: GameSession): File? {
        return try {
            val jsonContent = generateJsonReport(session)
            val fileName = "session_report_${session.gameTitle.lowercase().replace(" ", "_")}_${session.startTime}.json"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                out.write(jsonContent.toByteArray())
            }
            shareFile(context, file, "application/json")
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportAndSharePdf(context: Context, session: GameSession): File? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 22f
                isFakeBoldText = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 14f
            }

            val bodyPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
            }

            val accentPaint = Paint().apply {
                color = Color.parseColor("#1B5E20") // Dark green
                textSize = 14f
                isFakeBoldText = true
            }

            var y = 50f

            canvas.drawText("GAME BOOSTER+ PERFORMANCE REPORT", 40f, y, titlePaint)
            y += 30f

            canvas.drawText("Game: ${session.gameTitle} (${session.packageName})", 40f, y, subtitlePaint)
            y += 20f

            val mins = (session.durationMillis / 60000L).coerceAtLeast(1)
            canvas.drawText("Session Duration: $mins minutes", 40f, y, bodyPaint)
            y += 30f

            canvas.drawText("OVERALL SCORE: ${session.overallScore} / 100", 40f, y, accentPaint)
            y += 30f

            canvas.drawText("--- PERFORMANCE SCORES ---", 40f, y, subtitlePaint)
            y += 20f
            canvas.drawText("• Ping Stability: ${session.pingScore} / 100", 60f, y, bodyPaint)
            y += 18f
            canvas.drawText("• Free Memory: ${session.memoryScore} / 100", 60f, y, bodyPaint)
            y += 18f
            canvas.drawText("• Temperature Control: ${session.tempScore} / 100", 60f, y, bodyPaint)
            y += 30f

            canvas.drawText("--- TELEMETRY READINGS ---", 40f, y, subtitlePaint)
            y += 20f
            canvas.drawText("• Average Network Latency: ${session.avgLatencyMs} ms (Jitter: ${session.pingJitterMs} ms)", 60f, y, bodyPaint)
            y += 18f
            canvas.drawText("• Minimum Free RAM: ${session.minFreeRamPercent}%", 60f, y, bodyPaint)
            y += 18f
            canvas.drawText("• Peak Battery Temp: ${String.format("%.1f°C", session.peakBatteryTempC)}", 60f, y, bodyPaint)
            y += 30f

            canvas.drawText("Summary:", 40f, y, subtitlePaint)
            y += 18f
            canvas.drawText(session.summaryText, 60f, y, bodyPaint)

            pdfDocument.finishPage(page)

            val fileName = "session_report_${session.gameTitle.lowercase().replace(" ", "_")}_${session.startTime}.pdf"
            val file = File(context.cacheDir, fileName)
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            shareFile(context, file, "application/pdf")
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Export Session Report"))
        } catch (_: Exception) {}
    }
}
