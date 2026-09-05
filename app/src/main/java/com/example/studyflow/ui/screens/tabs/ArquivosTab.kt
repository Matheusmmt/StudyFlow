package com.example.studyflow.ui.screens.tabs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyflow.data.model.ArquivoEntity
import com.example.studyflow.ui.components.formatarData
import com.example.studyflow.ui.viewmodel.SubjectDetailViewModel
import com.example.studyflow.utils.FileUtils


@Composable
fun ArquivosTab(
    vm: SubjectDetailViewModel,
    idDisciplina: Long,
    context: Context = LocalContext.current
) {
    val arquivos by vm.arquivos.collectAsStateWithLifecycle()

    // Lógica do seletor SAF (Storage Access Framework) para abrir o explorador de arquivos
    val seletorArquivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            // Garante permissão persistente de leitura do arquivo no dispositivo
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    fileUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            // Lê nome e metadados do arquivo
            val info = FileUtils.lerInfo(context, fileUri)
            val novoArquivo = ArquivoEntity(
                disciplinaId = idDisciplina,
                nomeArquivo = info.nome,
                uri = fileUri.toString(),
                tamanhoOuTipo = FileUtils.formatarTamanho(info.tamanho)
            )
            vm.salvarArquivo(novoArquivo)
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (arquivos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Nenhum arquivo anexado ainda. Toque no + para adicionar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(arquivos, key = { it.id }) { arquivo ->
                    ItemArquivoEstilizado(
                        arquivo = arquivo,
                        aoClicar = { abrirArquivo(context, arquivo.uri) },
                        aoExcluir = { vm.deletarArquivo(arquivo) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                // Abre o gerenciador de arquivos do celular
                seletorArquivo.launch(arrayOf("application/pdf", "image/*", "*/*"))
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Arquivo")
        }
    }
}

/**
 * Item de lista estilizado para exibição dos arquivos.
 */
@Composable
private fun ItemArquivoEstilizado(
    arquivo: ArquivoEntity,
    aoClicar: () -> Unit,
    aoExcluir: () -> Unit
) {
    val ehPdf = arquivo.nomeArquivo.endsWith(".pdf", ignoreCase = true)

    Surface(
        onClick = aoClicar,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge/Ícone visual (Vermelho para PDF / Azul para outros arquivos)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (ehPdf) Color(0xFFE53935) else Color(0xFF2196F3)),
                contentAlignment = Alignment.Center
            ) {
                if (ehPdf) {
                    Text(
                        text = "PDF",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                } else {
                    Icon(
                        Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = arquivo.nomeArquivo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${formatarData(arquivo.dataAdicao)} ${arquivo.tamanhoOuTipo?.let { "• $it" }.orEmpty()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = aoExcluir) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Excluir Arquivo",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Lógica de abertura do arquivo no leitor externo do celular via Intent.ACTION_VIEW.
 */
private fun abrirArquivo(context: Context, uriString: String) {
    val uri = Uri.parse(uriString)
    val tipoMime = context.contentResolver.getType(uri) ?: "*/*"

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, tipoMime)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching {
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "Não foi possível abrir este arquivo.", Toast.LENGTH_SHORT).show()
    }
}
