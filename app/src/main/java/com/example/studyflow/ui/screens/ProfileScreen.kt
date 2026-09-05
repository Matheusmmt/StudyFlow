package com.example.studyflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyflow.ui.viewmodel.PerfilViewModel

/**
 * Tela de Perfil do Estudante e Configurações do Semestre Atual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaPerfil(
    vm: PerfilViewModel,
    aoVoltar: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados locais dos campos de texto
    var nomeInput by remember(uiState.nome) { mutableStateOf(uiState.nome) }
    var matriculaInput by remember(uiState.matricula) { mutableStateOf(uiState.matricula) }
    var semestreInput by remember(uiState.semestreAtual) { mutableStateOf(uiState.semestreAtual) }

    // Controle do diálogo para sugestão de frase (POST)
    var mostrarDialogoSugestao by remember { mutableStateOf(false) }
    var textoSugestaoInput by remember { mutableStateOf("") }

    // Exibe mensagens do estado da ViewModel via Snackbar
    LaunchedEffect(uiState.mensagemStatus) {
        uiState.mensagemStatus?.let {
            snackbarHostState.showSnackbar(it)
            vm.limparMensagemStatus()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Perfil do Estudante") },
                navigationIcon = {
                    IconButton(onClick = aoVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Card em destaque: Frase Motivacional (obtida via API GET)
            CardFraseMotivacional(
                frase = uiState.fraseMotivacional,
                carregando = uiState.carregandoFrase,
                aoAtualizarFrase = { vm.obterFraseMotivacional() },
                aoAbrirSugestao = { mostrarDialogoSugestao = true }
            )

            // Seção de Informações Pessoais do Perfil (Persistidas no DataStore)
            Text(
                "Dados do Estudante",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = nomeInput,
                onValueChange = { nomeInput = it },
                label = { Text("Nome do Estudante") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = matriculaInput,
                onValueChange = { matriculaInput = it },
                label = { Text("Matrícula") },
                leadingIcon = { Icon(Icons.Default.Badge, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = semestreInput,
                onValueChange = { semestreInput = it },
                label = { Text("Semestre Atual (ex: 2026.1)") },
                leadingIcon = { Icon(Icons.Default.DateRange, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = { vm.salvarPerfil(nomeInput, matriculaInput, semestreInput) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Salvar Dados do Perfil")
            }

            Spacer(Modifier.height(8.dp))

            // Seção: Estatísticas do Semestre Atual (Dados calculados/consultados do Room)
            Text(
                "Estatísticas do Semestre",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            CartaoSuperficie {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Class,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            "Disciplinas no Semestre (${uiState.semestreAtual})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${uiState.totalDisciplinasSemestre} cadastradas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // Diálogo para sugerir frase e enviar via requisição POST para a API
    if (mostrarDialogoSugestao) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoSugestao = false },
            title = { Text("Sugerir Frase Motivacional") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Digite uma frase motivacional para enviar para a API (requisição POST):",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = textoSugestaoInput,
                        onValueChange = { textoSugestaoInput = it },
                        label = { Text("Sua Frase") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.enviarSugestaoFrase(textoSugestaoInput)
                        textoSugestaoInput = ""
                        mostrarDialogoSugestao = false
                    },
                    enabled = !uiState.carregandoEnvio
                ) {
                    Text("Enviar (POST)")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoSugestao = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/** Cartão base com fundo escuro Surface (#151528) e cantos arredondados. */
@Composable
private fun CartaoSuperficie(
    modifier: Modifier = Modifier,
    conteudo: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = conteudo
        )
    }
}

/**
 * Card em Destaque exibindo a frase motivacional do dia obtida via API GET.
 */
@Composable
private fun CardFraseMotivacional(
    frase: String,
    carregando: Boolean,
    aoAtualizarFrase: () -> Unit,
    aoAbrirSugestao: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.FormatQuote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Frase Motivacional do Dia",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = aoAtualizarFrase) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Atualizar Frase",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (carregando) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            } else {
                Text(
                    "\"$frase\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = aoAbrirSugestao,
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddComment, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Sugerir Frase")
            }
        }
    }
}
