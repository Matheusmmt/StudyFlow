
# 📚 StudyFlow

**StudyFlow** é um aplicativo Android nativo desenvolvido em **Kotlin** com **Jetpack Compose**, criado para ajudar estudantes a organizarem suas disciplinas, anotações, faltas, lembretes de estudo e materiais acadêmicos em um único lugar.

O objetivo do projeto é oferecer uma solução simples, moderna e funcional para o gerenciamento da rotina acadêmica, permitindo que o usuário cadastre disciplinas e acompanhe informações importantes de cada uma delas de forma organizada.

---

## 📱 Sobre o Projeto

O StudyFlow foi desenvolvido como um aplicativo acadêmico com foco em organização estudantil.  
A aplicação permite que o usuário:

- Cadastre disciplinas;
- Informe o nome do professor;
- Defina o limite máximo de faltas;
- Acesse uma tela de detalhes para cada disciplina;
- Crie anotações;
- Anexe arquivos, como PDFs e imagens(ainda não funcional);
- Registre faltas;
- Visualize o progresso das faltas por disciplina;
- Crie lembretes com data e hora(ainda não funcional);
- Utilize o aplicativo em tema claro ou escuro.

O projeto segue uma estrutura organizada baseada na arquitetura **MVVM**, utilizando persistência local com **Room Database**.

---

## ✨ Funcionalidades

### 📘 Gerenciamento de Disciplinas

Na tela inicial, o usuário pode cadastrar, editar, excluir e visualizar suas disciplinas.

Cada disciplina possui:

- Nome;
- Professor;
- Limite máximo de faltas;
- Cor de destaque;
- Acesso individual à tela de detalhes.

---

### 📝 Anotações

Dentro de cada disciplina, o usuário pode criar anotações com:

- Título;
- Conteúdo;
- Arquivo anexado;
- Data de criação automática.

Essa funcionalidade é útil para guardar resumos, observações de aula, links importantes ou materiais relacionados à disciplina.

---

### 📎 Anexos de Arquivos

O app permite anexar arquivos às anotações, como:

- PDFs;
- Imagens;
- Outros documentos acadêmicos.

Isso torna o StudyFlow mais completo, pois o estudante pode manter seus materiais organizados por disciplina.

---

### 📅 Controle de Faltas

Cada disciplina possui uma aba específica para controle de faltas.

O usuário pode:

- Registrar uma falta;
- Escolher a data da falta usando um seletor de data;
- Visualizar o histórico de faltas;
- Acompanhar o total de faltas;
- Ver uma barra de progresso em relação ao limite máximo permitido;
- Receber um alerta visual quando o limite for atingido.

---

### ⏰ Lembretes

O StudyFlow permite criar lembretes associados a cada disciplina.

Cada lembrete possui:

- Título;
- Data;
- Hora;
- Status de conclusão.

A seleção de data e hora é feita com componentes visuais do Material Design 3, facilitando o uso pelo estudante.

---

### 🌗 Tema Claro e Escuro

O aplicativo possui suporte a:

- Tema claro;
- Tema escuro;

O tema é aplicado automaticamente de acordo com as configurações do sistema do usuário.

---

## 🧱 Tecnologias Utilizadas

O projeto foi desenvolvido utilizando as seguintes tecnologias:

- **Kotlin**
- **Jetpack Compose**
- **Material Design 3**
- **Room Database**
- **Navigation Compose**
- **ViewModel**
- **StateFlow**
- **Coroutines**
- **MVVM**
- **KSP**

---

## 🏗️ Arquitetura do Projeto

O StudyFlow utiliza a arquitetura **MVVM**, separando responsabilidades entre camadas de dados, lógica de negócio e interface.

```text
Model → Repository → ViewModel → UI
````

### Model

Representa as entidades do banco de dados:

* `Subject`
* `Note`
* `Absence`
* `Reminder`

### Repository

Centraliza o acesso aos dados e faz a comunicação entre os DAOs e a ViewModel.

### ViewModel

Gerencia o estado da interface e executa operações assíncronas usando Coroutines.

### UI

Construída com Jetpack Compose, sendo responsável pela exibição das telas, componentes e interações do usuário.

---

## 📁 Estrutura de Pastas

```text
com.example.studyflow
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── Converters.kt
│   │   ├── SubjectDao.kt
│   │   ├── NoteDao.kt
│   │   ├── AbsenceDao.kt
│   │   └── ReminderDao.kt
│   ├── model/
│   │   ├── Subject.kt
│   │   ├── Note.kt
│   │   ├── Absence.kt
│   │   └── Reminder.kt
│   └── repository/
│       └── SubjectRepository.kt
├── ui/
│   ├── screens/
│   │   ├── SubjectListScreen.kt
│   │   ├── SubjectDetailScreen.kt
│   │   └── tabs/
│   │       ├── NotesTab.kt
│   │       ├── AbsencesTab.kt
│   │       └── RemindersTab.kt
│   ├── viewmodel/
│   │   ├── SubjectViewModel.kt
│   │   └── SubjectViewModelFactory.kt
│   ├── components/
│   │   └── DateTimePickers.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── MainActivity.kt
```

---

## 🗃️ Banco de Dados

O projeto utiliza **Room Database** para persistência local dos dados.

### Entidades principais

#### Subject

Representa uma disciplina cadastrada pelo usuário.

Campos principais:

* `id`
* `nome`
* `professor`
* `maxFaltas`
* `corHex`

#### Note

Representa uma anotação vinculada a uma disciplina.

Campos principais:

* `id`
* `disciplinaId`
* `titulo`
* `conteudo`
* `arquivoUri`
* `criadoEm`

#### Absence

Representa uma falta registrada em uma disciplina.

Campos principais:

* `id`
* `disciplinaId`
* `data`
* `motivo`

#### Reminder

Representa um lembrete vinculado a uma disciplina.

Campos principais:

* `id`
* `disciplinaId`
* `titulo`
* `dataHora`
* `concluido`

---


## 🧭 Navegação

A navegação é feita com **Navigation Compose**.

O app possui duas telas principais:

### Tela de Lista de Disciplinas

Essa tela exibe todas as disciplinas cadastradas.

### Tela de Detalhes da Disciplina

Essa tela exibe as informações internas de uma disciplina específica, organizadas em abas.

---

## 🖥️ Telas do Aplicativo

### Tela Inicial

A tela inicial mostra a lista de disciplinas cadastradas.
Caso nenhuma disciplina exista, é exibida uma mensagem orientando o usuário a criar uma nova.

Principais ações:

* Criar disciplina;
* Editar disciplina;
* Excluir disciplina;
* Abrir detalhes da disciplina.

---

### Tela de Detalhes

A tela de detalhes possui abas para organizar as informações da disciplina.

Abas disponíveis:

* **Anotações**
* **Faltas**
* **Lembretes**

---

## 🎨 Interface

A interface foi desenvolvida com **Jetpack Compose** e **Material Design 3**.

O app utiliza:

* Cards elevados;
* Floating Action Button;
* Top App Bar;
* Tab Row;
* Date Picker;
* Time Picker;
* Outlined Text Fields;
* Tema claro e escuro;
* Ícones do Material Icons.

A proposta visual é ser simples, moderna e funcional, mantendo boa usabilidade para estudantes.

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

* Android Studio;
* JDK configurado;
* Gradle sincronizado;
* Emulador Android ou dispositivo físico;
* SDK Android atualizado.

---

### Passo a passo

1. Clone o repositório:

```bash
git clone https://github.com/matheusmmt/StudyFlow.git
```

2. Acesse a pasta do projeto:

```bash
cd StudyFlow
```

3. Abra o projeto no Android Studio.

4. Aguarde a sincronização do Gradle.

5. Execute o app em um emulador ou celular Android.

---

## 📌 Requisitos do Projeto

O projeto atende aos seguintes requisitos:

* Aplicativo Android nativo;
* Desenvolvimento em Kotlin;
* Uso de Jetpack Compose;
* Mínimo de duas telas;
* Navegação ativa entre telas;
* Persistência local de dados;
* Organização em arquitetura MVVM;
* Separação clara entre dados, lógica e interface;
* Interface moderna e funcional.

---


## 📊 Fluxo Geral do App

```text
Usuário abre o app
        ↓
Visualiza a lista de disciplinas
        ↓
Cria ou seleciona uma disciplina
        ↓
Acessa a tela de detalhes
        ↓
Gerencia anotações, faltas e lembretes
        ↓
Os dados ficam salvos localmente com Room
```

---


## 🔮 Melhorias Futuras

Algumas funcionalidades que podem ser adicionadas futuramente:

* Notificações reais para lembretes;
* Integração com AlarmManager ou WorkManager;
* Tela de calendário;
* Filtro de disciplinas por semestre;
* Organização por horários de aula;
* Backup dos dados;
* Sincronização em nuvem;
* Login de usuário;
* Exportação de anotações;
* Abertura direta dos arquivos anexados;
* Personalização de cores das disciplinas;
* Gráficos de desempenho acadêmico;
* Cálculo automático de risco de reprovação por falta.

---


## 🧑‍💻 Autor

Desenvolvido por **Matheus Melo**.

Projeto criado para fins acadêmicos, com o objetivo de aplicar conceitos de desenvolvimento Android moderno, persistência local, arquitetura MVVM e criação de interfaces com Jetpack Compose.

---


## 🏁 Conclusão

O **StudyFlow** é uma aplicação Android voltada para estudantes que desejam organizar melhor sua rotina acadêmica.

Com ele, é possível centralizar informações importantes de cada disciplina, acompanhar faltas, criar lembretes e armazenar anotações em um ambiente simples, moderno e funcional.

O projeto demonstra o uso prático de tecnologias modernas do desenvolvimento Android, como **Kotlin**, **Jetpack Compose**, **Room**, **Navigation Compose** e arquitetura **MVVM**.

```
