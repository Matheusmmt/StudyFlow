# 📚 StudyFlow

**StudyFlow** é um aplicativo Android nativo desenvolvido em **Kotlin** com **Jetpack Compose**, criado para ajudar estudantes a organizarem suas disciplinas, anotações, faltas, anexos acadêmicos, lembretes de estudo, progresso do semestre e perfil de estudante em um único lugar.

O aplicativo oferece uma experiência moderna, intuitiva e fluida com suporte completo a **Tema Claro e Escuro**, tratamento correto de fuso horário e persistência de dados local e remota.

---

## 📱 Sobre o Projeto

O StudyFlow foi desenvolvido como uma solução completa para o gerenciamento da rotina acadêmica.  

A aplicação permite que o usuário:

- Cadastre e organize suas disciplinas filtradas por **Semestre Atual**;
- Gerencie seu **Perfil de Estudante** (Nome, Matrícula e Semestre) salvo com **Preferences DataStore**;
- Acompanhe o **Progresso Automático** de cada disciplina com base no total de aulas, data de início e dias da semana;
- Crie anotações textuais e anexe **Arquivos Acadêmicos** (PDFs e imagens) via SAF (Storage Access Framework);
- Registre e controle o número de faltas com limite configurável por disciplina;
- Agende **Lembretes com Notificações Locais Exatas** via **AlarmManager** e **BroadcastReceiver**;
- Receba **Frases Motivacionais Diárias** consumidas de uma **API REST via Retrofit** (`@GET`), com opção de sugerir novas frases (`@POST`).

O projeto é estruturado na arquitetura **MVVM**, utilizando **Room Database**, **Preferences DataStore**, **Retrofit** e **Jetpack Compose**.

---

## ✨ Funcionalidades

### 👤 Perfil do Estudante & Preferences DataStore
O usuário possui uma tela de perfil dedicada onde pode personalizar suas informações acadêmicas:
- **Nome do Estudante**;
- **Matrícula**;
- **Semestre Atual**.

Essas preferências são gravadas de forma assíncrona usando **Preferences DataStore**, garantindo persistência leve e reativa sem bloquear a UI.

---

### 🎓 Filtro de Disciplinas por Semestre
A lista principal de disciplinas e as consultas no banco de dados (**Room**) são filtradas dinamicamente com base no **Semestre Atual** selecionado no perfil do estudante. Ao alterar o semestre no perfil, a interface se atualiza automaticamente para exibir apenas as disciplinas correspondentes.

---

### 📈 Progresso Automático da Disciplina
O acompanhamento de progresso é calculado automaticamente pelo app. Ao cadastrar uma disciplina, o usuário informa:
- **Total de Aulas Planejadas**;
- **Data de Início das Aulas**;
- **Dias da Semana**.

Com base na data atual, o sistema calcula quantas aulas já deveriam ter ocorrido até o momento e gera a porcentagem de progresso de cada matéria.

---

### 💬 Frases Motivacionais (Integração REST via Retrofit)
A Tela de Perfil conta com um Card em destaque que exibe uma **Frase Motivacional do Dia**:
- **Requisição `@GET`**: Consome a API pública (JSONPlaceholder) e mapeia a resposta para frases inspiradoras em Português;
- **Requisição `@POST`**: Permite ao estudante sugerir uma nova frase motivacional, enviando os dados em formato JSON para o servidor.

---

### ⏰ Lembretes & Alarmes Exatos (AlarmManager + BroadcastReceiver)
Os lembretes agendados contam com notificações locais que disparam na data e hora exatas configuradas pelo estudante:
- Utiliza **`AlarmManager`** com o método `setExactAndAllowWhileIdle()` para disparos precisos, mesmo em modo de economia de energia (Doze Mode);
- Notificações gerenciadas via **`BroadcastReceiver`** (`ReceptorLembrete`) e **`NotificationManager`**;
- Validação automática para impedir disparos incorretos caso o horário agendado seja no passado.

---

### 📁 Anotações e Anexos de Arquivos (PDFs e Imagens)
Dentro de cada disciplina, o estudante conta com abas dedicadas para:
- **Anotações**: Criação e edição de notas de aula com título e conteúdo;
- **Arquivos**: Anexo e abertura de documentos externos (PDFs, fotos da lousa e apostilas) utilizando o leitor nativo do dispositivo via `Intent.ACTION_VIEW` e permissões persistentes do SAF.

---

### 📅 Controle de Faltas
Aba dedicada para monitorar a frequência:
- Registro de faltas com data e justificativa/observação;
- Histórico detalhado por disciplina;
- Barra de progresso comparando o total de faltas com o limite máximo permitido;
- Alertas visuais e notificações quando o limite estiver próximo ou atingido.

---

### 🕒 Correção de Fuso Horário nas Datas (Off-by-one Fix)
Normalização dos timestamps retornados pelo `DatePicker` do Material 3. A função `normalizarDataUtcParaLocal` recalcula a data de UTC meia-noite para o início do dia no fuso horário local do dispositivo (ex: GMT-3), evitando que o dia selecionado mude para o dia anterior ao salvar no banco ou exibir na tela.

---

### 🌗 Suporte Completo a Tema Claro e Escuro
- Adapta-se automaticamente ao modo configurado no sistema Android (`isSystemInDarkTheme()`);
- Gerenciamento dinâmico da cor dos ícones da barra de status e barra de navegação (`isAppearanceLightStatusBars`);
- Paletas de alto contraste para garantir excelente legibilidade no **Dark Mode** e no **Light Mode**.

---

## 🧱 Tecnologias Utilizadas

O projeto utiliza o ecossistema moderno do desenvolvimento Android Nativo:

- **Kotlin**
- **Jetpack Compose**
- **Material Design 3**
- **Room Database**
- **Preferences DataStore**
- **Retrofit 2 & Gson**
- **OkHttp (HttpLoggingInterceptor)**
- **AlarmManager & BroadcastReceiver**
- **Navigation Compose**
- **ViewModel & StateFlow**
- **Coroutines**
- **MVVM Architecture**
- **KSP (Kotlin Symbol Processing)**

---

## 🏗️ Arquitetura do Projeto

O aplicativo segue o padrão **MVVM (Model-View-ViewModel)** com separação clara de responsabilidades:

```text
  [ UI (Jetpack Compose) ]
             ↓
    [ ViewModels / StateFlow ]
             ↓
     [ StudyRepository ]
      ↙         ↓        ↘
[ Room DB ]  [DataStore]  [ Retrofit API ]
```

### 1. Model (Entidades & DTOs)
- **Room Entities**: `Subject`, `Note`, `ArquivoEntity`, `Absence`, `Reminder`;
- **DTOs de Rede**: `FrasePost` para comunicação HTTP com a API.

### 2. Data Source (Persistência & Rede)
- **Local (Room)**: `SubjectDao`, `NoteDao`, `ArquivoDao`, `AbsenceDao`, `ReminderDao`;
- **Local (Preferences DataStore)**: `PerfilDataStore` (Nome, Matrícula, Semestre);
- **Remoto (Retrofit)**: `ApiClient` e `FrasesApi` (`https://jsonplaceholder.typicode.com/`).

### 3. Repository
- `StudyRepository`: Centraliza a origem dos dados (Room, DataStore e Retrofit).

### 4. ViewModel
- `SubjectListViewModel`: Controla a lista de disciplinas filtradas por semestre;
- `SubjectDetailViewModel`: Gerencia anotações, arquivos, faltas e lembretes de uma disciplina;
- `PerfilViewModel`: Controla o estado da Tela de Perfil, preferências do DataStore e chamadas GET/POST da API.

---

## 📁 Estrutura de Pastas

```text
com.example.studyflow
├── data/
│   ├── local/
│   │   ├── AbsenceDao.kt
│   │   ├── AppDatabase.kt
│   │   ├── ArquivoDao.kt
│   │   ├── Converters.kt
│   │   ├── NoteDao.kt
│   │   ├── PerfilDataStore.kt
│   │   ├── ReminderDao.kt
│   │   └── SubjectDao.kt
│   ├── model/
│   │   ├── Absence.kt
│   │   ├── ArquivoEntity.kt
│   │   ├── Note.kt
│   │   ├── Reminder.kt
│   │   └── Subject.kt
│   ├── remote/
│   │   └── StudyApi.kt
│   └── repository/
│       ├── StudyRepository.kt
│       └── SubjectRepository.kt
├── ui/
│   ├── components/
│   │   └── DateTimePickers.kt
│   ├── screens/
│   │   ├── AddEditSubjectScreen.kt
│   │   ├── ProfileScreen.kt
│   │   ├── SubjectDetailScreen.kt
│   │   ├── SubjectListScreen.kt
│   │   └── tabs/
│   │       ├── AbsencesTab.kt
│   │       ├── ArquivosTab.kt
│   │       ├── NotesTab.kt
│   │       └── RemindersTab.kt
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       └── ViewModels.kt
├── utils/
│   ├── CalculoProgressoUtils.kt
│   ├── FileUtils.kt
│   ├── NotificationHelper.kt
│   └── ReceptorLembrete.kt
├── MainActivity.kt
└── StudyFlowApp.kt
```

---

## 🗃️ Banco de Dados & Armazenamento Local

### 1. Room Database (`studyflow.db`)

- **`Subject`**: Armazena as disciplinas do estudante (`id`, `nome`, `professor`, `horario`, `cor`, `icone`, `maxFaltas`, `totalAulas`, `diasSemana`, `dataInicio`, `semestre`, `createdAt`).
- **`Note`**: Registra as anotações textuais criadas por disciplina.
- **`ArquivoEntity`**: Registra arquivos anexados (PDFs, imagens) por disciplina.
- **`Absence`**: Registra o histórico de faltas por disciplina.
- **`Reminder`**: Guarda os lembretes cadastrados com data/hora.

### 2. Preferences DataStore (`configuracoes_perfil`)

- `usuario_nome`: Nome do estudante;
- `usuario_matricula`: Matrícula acadêmica;
- `usuario_semestre`: Semestre letivo ativo (ex: "2026.1").

---

## 🌐 Consumo de API Externa (Retrofit)

- **Base URL**: `https://jsonplaceholder.typicode.com/`
- **Endpoints Utilizados**:
  - `GET /posts/{id}`: Busca dados da frase na API e mapeia para a mensagem motivacional do dia;
  - `POST /posts`: Permite enviar uma sugestão de frase do usuário com payload JSON.

---

## 🧭 Navegação

Utiliza **Navigation Compose** com rotas estruturadas:

1. **`"lista"`**: Tela principal com o resumo das disciplinas do semestre ativo e acesso rápido ao perfil;
2. **`"detalhe/{id}"`**: Tela interna de detalhes da disciplina organizada nas abas Anotações, Arquivos, Faltas e Lembretes;
3. **`"form/{id}"`**: Formulário de criação/edição de disciplinas com configurador de aulas, data de início e dias da semana;
4. **`"perfil"`**: Tela de perfil do estudante, estatísticas, frases motivacionais da API e edição de semestre/dados no DataStore.

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

- Android Studio Flamingo ou superior;
- JDK 17 configurado;
- Gradle sincronizado;
- Emulador Android (API 26 ou superior) ou dispositivo físico.

### Passo a Passo

1. Clone o repositório:
```bash
git clone https://github.com/matheusmmt/StudyFlow.git
```

2. Acesse a pasta do projeto:
```bash
cd StudyFlow
```

3. Abra o projeto no **Android Studio**.

4. Aguarde a sincronização automática do Gradle.

5. Execute a aplicação no seu dispositivo ou emulador.

---

## 🧑‍💻 Autor

Desenvolvido por **Matheus Melo**.

Projeto acadêmico focado na demonstração prática do desenvolvimento Android moderno utilizando **Kotlin**, **Jetpack Compose**, **Room**, **Preferences DataStore**, **Retrofit**, **AlarmManager** e arquitetura **MVVM**.
