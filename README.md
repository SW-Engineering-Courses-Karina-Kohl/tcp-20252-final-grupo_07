<h1 align="center">Matriculador INF/UFRGS</h1>

<p align="center">
  Geração automática de grades • MVC • Java • Swing
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-blue" />
  <img src="https://img.shields.io/badge/Build-Gradle-success" />
  <img src="https://img.shields.io/badge/Tested-JUnit5-red" />
</p>

---

## Descrição do Projeto
O **Matriculador INF/UFRGS** é uma aplicação em Java desenvolvida para auxiliar estudantes na montagem de seus horários.  

O sistema permite:

- Importar disciplinas via **CSV**
- Inserir turmas manualmente
- Selecionar disciplinas desejadas
- Definir preferências (turno, professores, horários proibidos, número de cadeiras)
- Gerar automaticamente **todas as grades possíveis**
- Visualizar as grades em interface gráfica (Swing)

O projeto segue o padrão **MVC (Model–View–Controller)**.

---

# Arquitetura do Sistema (MVC)

```
src/main/java/
│
├── model/          
│     ├── Disciplina.java
│     ├── Turma.java
│     ├── Professor.java
│     ├── Horario.java
│     ├── Grade.java
│     ├── Preferencias.java
│     ├── ExtracaoDados.java
│     ├── GeradorDeGrades.java
│     └── GerenciadorDePreferencias.java
│
├── app/
│     ├── controller/
│     │       └── AppController.java
│     └── view/
│             ├── MenuInicialGUI.java
│             ├── InsercaoGUI.java
│             ├── SelecaoDisciplinasGUI.java
│             ├── PreferenciasGUI.java
│             └── GradeGUI.java
│
└── Main.java
```

---

# Funcionalidades

### Entrada de Dados
- Carregamento via CSV
- Input manual de turmas

### Backend
- Parser de CSV
- Geração de grades possíveis
- Aplicação de preferências

### Interface Gráfica
- Visualização clara das grades

---

# Como Executar

```bash
./gradlew run
```

Ou:

```bash
javac -d out src/main/java/**/*.java
java -cp out app.Main
```

---

# Testes

```bash
./gradlew test
```

---

# Tecnologias
- Java 17  
- Swing  
- Gradle  
- JUnit 5  
- Log4j2  

---

# Próximos Passos
- Modularizar UI  
- Validações mais completas  
- Suporte a todos os cursos da UFRGS  
- Otimização do gerador  

---

# Autores
- Arthur Chagas
- Bruno Mengue
- João Clementel
- Mariana Ercolani 
- Vicente Veiga