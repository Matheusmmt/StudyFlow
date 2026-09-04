package com.example.studyflow.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studyflow.data.model.*
import com.example.studyflow.data.remote.FrasePost
import com.example.studyflow.data.repository.StudyRepository
import com.example.studyflow.utils.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Lista de disciplinas filtradas pelo semestre atual do usuário + busca. */
@OptIn(ExperimentalCoroutinesApi::class)
class SubjectListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = StudyRepository(app)

    val busca = MutableStateFlow("")
    val semestreAtual: StateFlow<String> = repo.semestreAtualFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "2026.1")

    val disciplinas: StateFlow<List<Subject>> = repo.semestreAtualFlow.flatMapLatest { semestre ->
        combine(repo.disciplinasPorSemestre(semestre), busca) { lista, q ->
            if (q.isBlank()) lista
            else lista.filter { it.nome.contains(q, true) || it.professor.orEmpty().contains(q, true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val proximoLembrete: StateFlow<Reminder?> =
        repo.proximoLembrete().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun salvar(s: Subject) = viewModelScope.launch {
        // Para novas disciplinas (id == 0), obtém obrigatoriamente o semestre atual dinâmico do Preferences DataStore
        val disciplinaComSemestre = if (s.id == 0L) {
            val semestreDinamico = repo.semestreAtualFlow.first()
            s.copy(semestre = semestreDinamico)
        } else {
            s
        }
        repo.salvarDisciplina(disciplinaComSemestre)
    }

    fun deletar(s: Subject) = viewModelScope.launch { repo.deletarDisciplina(s) }
    fun disciplina(id: Long) = repo.disciplina(id)
}

/** Detalhe: anotações/arquivos, faltas e lembretes de uma disciplina. */
class SubjectDetailViewModel(app: Application, private val disciplinaId: Long) : AndroidViewModel(app) {
    private val repo = StudyRepository(app)

    val disciplina = repo.disciplina(disciplinaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val anotacoes = repo.anotacoes(disciplinaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val faltas = repo.faltas(disciplinaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val lembretes = repo.lembretes(disciplinaId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun salvarAnotacao(n: Note) = viewModelScope.launch { repo.salvarAnotacao(n.copy(disciplinaId = disciplinaId)) }
    fun deletarAnotacao(n: Note) = viewModelScope.launch { repo.deletarAnotacao(n) }

    fun registrarFalta(data: Long, justificada: Boolean) = viewModelScope.launch {
        repo.registrarFalta(Absence(
            disciplinaId = disciplinaId,
            data = data,
            justificada = justificada,
            motivo = "Falta registrada",
        ))
        val ctx = getApplication<Application>()
        val disc = disciplina.value
        val total = faltas.value.size + 1
        if (disc != null && disc.maxFaltas > 0 && total >= disc.maxFaltas * 0.75) {
            NotificationHelper.notificar(
                ctx, disciplinaId.toInt() + 9000,
                "Atenção em ${disc.nome}",
                "Você já tem $total de ${disc.maxFaltas} faltas permitidas."
            )
        }
    }

    fun deletarFalta(a: Absence) = viewModelScope.launch { repo.deletarFalta(a) }

    fun salvarLembrete(r: Reminder) = viewModelScope.launch {
        val id = repo.salvarLembrete(r.copy(disciplinaId = disciplinaId))
        if (r.ativo) {
            NotificationHelper.agendarLembrete(
                getApplication(), id, r.titulo,
                r.descricao ?: "Hora de estudar ${disciplina.value?.nome.orEmpty()}",
                r.dataHora
            )
        }
    }

    fun deletarLembrete(r: Reminder) = viewModelScope.launch {
        NotificationHelper.cancelarLembrete(getApplication(), r.id)
        repo.deletarLembrete(r)
    }



    class Factory(private val app: Application, private val id: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SubjectDetailViewModel(app, id) as T
    }
}

/** Estado da UI para a Tela de Perfil. */
data class PerfilUiState(
    val nome: String = "",
    val matricula: String = "",
    val semestreAtual: String = "2026.1",
    val totalDisciplinasSemestre: Int = 0,
    val fraseMotivacional: String = "Carregando frase motivacional...",
    val carregandoFrase: Boolean = false,
    val carregandoEnvio: Boolean = false,
    val mensagemStatus: String? = null
)

/**
 * ViewModel responsável por gerenciar a Tela de Perfil, persistência no Preferences DataStore,
 * consultas por Semestre no Room e chamadas GET e POST da API via Retrofit.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = StudyRepository(app)

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    // Lista fixa de frases motivacionais em Português para mapear a resposta da API externa
    private val frasesPortugues = listOf(
        "O sucesso é a soma de pequenos esforços repetidos dia após dia.",
        "Acredite no seu potencial: estudar é construir a ponte para o seu próprio futuro!",
        "A persistência é o caminho do êxito.",
        "Cada hora de estudo dedicada hoje é um degrau a mais nos seus objetivos.",
        "O conhecimento é o único bem que se multiplica quando compartilhado.",
        "Não espere por oportunidades perfeitas: crie-as com a sua dedicação diária.",
        "Aprender é a única coisa de que a mente nunca se cansa e jamais se arrepende."
    )

    init {
        // Observa a leitura reativa do perfil gravado no DataStore
        viewModelScope.launch {
            combine(
                repo.nomeFlow,
                repo.matriculaFlow,
                repo.semestreAtualFlow
            ) { nome, matricula, semestre ->
                Triple(nome, matricula, semestre)
            }.collect { (nome, matricula, semestre) ->
                _uiState.update {
                    it.copy(
                        nome = nome,
                        matricula = matricula,
                        semestreAtual = semestre
                    )
                }
            }
        }

        // Observa a contagem de disciplinas cadastradas no semestre atual no Room
        viewModelScope.launch {
            repo.semestreAtualFlow.flatMapLatest { semestre ->
                repo.contarDisciplinasPorSemestre(semestre)
            }.collect { total ->
                _uiState.update { it.copy(totalDisciplinasSemestre = total) }
            }
        }

        // Busca uma nova frase motivacional na inicialização
        obterFraseMotivacional()
    }

    /**
     * Requisição @GET: Busca frase na API do JSONPlaceholder e mapeia para a lista em Português.
     */
    fun obterFraseMotivacional() = viewModelScope.launch {
        _uiState.update { it.copy(carregandoFrase = true) }
        runCatching {
            val idRandom = (1..10).random()
            repo.obterFrasePorId(idRandom)
        }.onSuccess { fraseApi ->
            val indice = ((fraseApi.id ?: 1L) % frasesPortugues.size).toInt()
            val fraseTraduzida = frasesPortugues[indice]
            _uiState.update {
                it.copy(
                    fraseMotivacional = fraseTraduzida,
                    carregandoFrase = false
                )
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    fraseMotivacional = frasesPortugues.random(),
                    carregandoFrase = false,
                    mensagemStatus = "Frase motivacional carregada offline."
                )
            }
        }
    }

    /**
     * Requisição @POST: Envia uma sugestão de frase do usuário para a API externa.
     */
    fun enviarSugestaoFrase(sugestaoTexto: String) = viewModelScope.launch {
        if (sugestaoTexto.isBlank()) {
            _uiState.update { it.copy(mensagemStatus = "Digite o texto da frase antes de enviar!") }
            return@launch
        }
        _uiState.update { it.copy(carregandoEnvio = true) }
        val novaFrase = FrasePost(texto = sugestaoTexto)
        runCatching {
            repo.enviarSugestaoFrase(novaFrase)
        }.onSuccess {
            _uiState.update {
                it.copy(
                    carregandoEnvio = false,
                    mensagemStatus = "Sugestão de frase enviada com sucesso (POST)!"
                )
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    carregandoEnvio = false,
                    mensagemStatus = "Erro ao enviar sugestão: ${error.localizedMessage}"
                )
            }
        }
    }

    /**
     * Salva as alterações de Nome, Matrícula e Semestre Atual no DataStore.
     */
    fun salvarPerfil(nome: String, matricula: String, semestre: String) = viewModelScope.launch {
        repo.salvarPerfil(nome, matricula, semestre)
        _uiState.update { it.copy(mensagemStatus = "Perfil atualizado com sucesso!") }
    }

    fun limparMensagemStatus() {
        _uiState.update { it.copy(mensagemStatus = null) }
    }
}
