package com.example.studyflow.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inicialização do Preferences DataStore como uma propriedade de extensão do Context
private val Context.dataStore by preferencesDataStore(name = "configuracoes_perfil")

/**
 * Classe responsável por gerenciar a persistência local dos dados do perfil do usuário
 * utilizando o Preferences DataStore.
 */
class PerfilDataStore(private val context: Context) {

    companion object {
        private val CHAVE_NOME = stringPreferencesKey("usuario_nome")
        private val CHAVE_MATRICULA = stringPreferencesKey("usuario_matricula")
        private val CHAVE_SEMESTRE = stringPreferencesKey("usuario_semestre")
    }

    /** Fluxo reativo do nome do usuário (padrão: "Aluno") */
    val nomeFlow: Flow<String> = context.dataStore.data.map { pref ->
        pref[CHAVE_NOME] ?: "Aluno"
    }

    /** Fluxo reativo da matrícula do usuário (padrão: "000000") */
    val matriculaFlow: Flow<String> = context.dataStore.data.map { pref ->
        pref[CHAVE_MATRICULA] ?: "000000"
    }

    /** Fluxo reativo do semestre atual selecionado (padrão: "2026.2") */
    val semestreAtualFlow: Flow<String> = context.dataStore.data.map { pref ->
        pref[CHAVE_SEMESTRE] ?: "2026.2"
    }

    /**
     * Salva as informações do perfil localmente de forma assíncrona.
     */
    suspend fun salvarPerfil(nome: String, matricula: String, semestre: String) {
        context.dataStore.edit { pref ->
            pref[CHAVE_NOME] = nome
            pref[CHAVE_MATRICULA] = matricula
            pref[CHAVE_SEMESTRE] = semestre
        }
    }
}
