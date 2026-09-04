package com.example.studyflow.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Metadados de um arquivo escolhido nos arquivos do dispositivo. */
data class ArquivoInfo(
    val uri: String,
    val nome: String,
    val tamanho: Long,
    val tipo: String
)

object FileUtils {

    /** Mantém a permissão de leitura do arquivo mesmo após reiniciar o app (SAF). */
    fun persistirPermissao(context: Context, uri: Uri) {
        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    fun lerInfo(context: Context, uri: Uri): ArquivoInfo {
        var nome = uri.lastPathSegment ?: "arquivo"
        var tamanho = 0L
        context.contentResolver.query(uri, null, null, null, null)?.use { c ->
            val iNome = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val iTamanho = c.getColumnIndex(OpenableColumns.SIZE)
            if (c.moveToFirst()) {
                if (iNome >= 0) nome = c.getString(iNome)
                if (iTamanho >= 0) tamanho = c.getLong(iTamanho)
            }
        }
        val tipo = context.contentResolver.getType(uri) ?: "application/octet-stream"
        return ArquivoInfo(uri.toString(), nome, tamanho, tipo)
    }

    /** Abre o arquivo salvo no app padrão do dispositivo. */
    fun abrir(context: Context, uriString: String, tipo: String?) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(uriString), tipo ?: "*/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
    }

    fun compartilhar(context: Context, uriString: String, tipo: String?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = tipo ?: "*/*"
            putExtra(Intent.EXTRA_STREAM, Uri.parse(uriString))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar arquivo"))
    }

    /** Cria um destino temporário para a foto tirada pela câmera. */
    fun novoArquivoDeFoto(context: Context): Pair<File, Uri> {
        val dir = File(context.cacheDir, "capturas").apply { mkdirs() }
        val nome = "IMG_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
        val arquivo = File(dir, nome)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", arquivo)
        return arquivo to uri
    }

    fun formatarTamanho(bytes: Long?): String {
        if (bytes == null || bytes <= 0) return ""
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.getDefault(), "%.0f KB", kb)
        return String.format(Locale.getDefault(), "%.1f MB", kb / 1024)
    }

    fun formatarData(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(Date(millis))

    fun formatarDataHora(millis: Long): String =
        SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR")).format(Date(millis))

    fun diaDaSemana(millis: Long): String =
        SimpleDateFormat("EEEE", Locale("pt", "BR")).format(Date(millis))
            .replaceFirstChar { it.uppercase() }
}
