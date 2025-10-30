# Instruções do Copilot para este repositório

Resumo curto (para uma IA dev/intern): este projeto é uma aplicação Java pura (CLI) que simula gerenciamento de aeroporto — entidades, DAOs em memória, services com regras de negócio e um menu em `main/Main.java`.

Pontos essenciais para começar rapidamente
- Linguagem: Java 17+ (projeto usa API java.time). Não há frameworks externos.
- Build / run (rápido):
  - Compilar: `javac -d bin comum/*.java companhia/*.java voo/*.java passageiro/*.java ticket/*.java checkin/*.java main/*.java`
  - Executar: `java -cp bin main.Main`

Arquitetura e responsabilidades (por pasta)
- `comum/` — classes base (ex: `EntidadeBase`, `DaoBase`, `SystemClock`). Fornece auditoria, clonagem e CRUD em memória.
- `companhia/`, `passageiro/`, `voo/`, `ticket/`, `checkin/` — cada pacote tem: entidade, DAO (herda `DaoBase<T>`), e Service (regras de negócio + validações).
- `main/Main.java` — CLI que orquestra Services e DAOs; é o ponto de entrada do app.

Padrões e convenções do projeto (importantes para alterações e para a IA seguir)
- DAO em memória: todos os DAOs herdam `comum.DaoBase<T>` e implementam `createArray(int)` e `cloneEntity(T)`. Sempre retornar clones, nunca a referência direta do array.
- Entidade base: `EntidadeBase` provê `auditar()` para setar `dataCriacao`/`dataModificacao`. Padrão: Services devem chamar `entidade.auditar()` antes de persistir ou ao atualizar.
- Services: contêm validações e regras. Lançam `IllegalArgumentException` quando a validação falha; a camada de UI (menus) captura e imprime a mensagem.
- Construtores de entidade: preferir construtores sem timestamps e usar `auditar()` na Service. Mantêm compatibilidade com overloads que aceitam timestamps quando necessário.
- Formatos de entrada: `LocalDate` usa `yyyy-MM-dd` (ex.: `2025-12-05`), `LocalTime` usa `HH:mm`. Preferir parsing centralizado quando adicionar novos prompts.

Regras de negócio notáveis (exemplos no código)
- Ticket: criado via `ticket/TicketService`; valida voo, passageiro e valor. Código do ticket pode ser gerado automaticamente (veja `TicketService`).
- Check‑in: só é permitido a partir de 24 horas antes da partida do voo — Service `checkin/CheckinService` calcula `partida = voo.getData().atTime(voo.getHorario())` (fallback meia-noite) e compara com `SystemClock`.
- Auditoria: Services chamam `entidade.auditar()` (ex.: `PassageiroService`, `VooService`) para manter `dataCriacao` e `dataModificacao` consistentes.

Arquivos-chave para leituras rápidas
- `comum/EntidadeBase.java` — entender `auditar()` e campos de auditoria
- `comum/DaoBase.java` — ver implementação do CRUD em memória, clonagem e expansão de array
- `passageiro/PassageiroService.java` — exemplo de validação/uniqueness (documento/login)
- `voo/VooDao.java`, `voo/VooService.java` — buscas por origem/destino/data e uso de `horario` para precisão do check-in
- `checkin/CheckinService.java` — regra de 24h e geração de boarding pass (integra com TicketService)
- `main/Main.java` — orquestra menus; copiar padrão de leitura/try-catch ao adicionar novos menus

Fluxos de depuração e comandos úteis
- Para reproduzir um erro de parsing de data: execute a opção do menu que lê datas e insira `2025-12-5` em vez de `2025-12-05` (LocalDate.parse falhará). Documente input esperado nos prompts.
- Para trocar SystemClock para testes, use `comum/SystemClock` (já usado por Services). Atualmente `EntidadeBase.auditar()` usa `LocalDate.now()` — se precisar de hora, considere alterar `EntidadeBase` para `LocalDateTime` ou usar `SystemClock.now()` e propagar.

Pequenas orientações práticas para a IA ao editar código
- Nunca alterar public APIs sem necessidade — preferir overloads e compatibilidade.
- Ao alterar DAOs, mantenha o contrato: métodos `findById`, `findAll`, `create`, `update`, `deleteById` e `cloneEntity` devem existir.
- Ao adicionar menus em `Main.java`, siga o padrão: ler entrada com `Scanner`, usar `sc.nextLine()` após `nextInt()`, envolver com try/catch e imprimir `e.getMessage()`.
- Evite usar try/catch para lógica de validação — lance `IllegalArgumentException` e deixe a UI tratar a mensagem.

Solicitação de revisão
- Se algo útil estiver faltando (ex.: convenções de nomes de métodos, validações extras, ou formatos de output), diga quais fluxos você quer que eu documente com exemplos reais. Posso iterar o arquivo.

Arquivo gerado automaticamente por IA — revise antes de commitar.
# Copilot Instructions for AI Agents

## Visão Geral da Arquitetura
Este projeto é uma aplicação Java modular para gerenciamento de aeroporto, passageiros, voos, companhias aéreas e tickets. O código está dividido em pacotes principais:
- `aeroporto/`: Entidade Aeroporto
- `companhia/`: CompanhiaAerea, DAO, Service
- `passageiro/`: Passageiro, DAO, Service
- `voo/`: Voo, EstadoVoo, DAO, Service
- `ticket/`: Ticket, DAO, Service
- `comum/`: Classes base abstratas (`EntidadeBase`, `EntidadeNome`, `DaoBase`, `SystemClock`)
- `main/`: CLI principal (menus)

## Fluxo de Dados e Componentes
**Entidades**: Cada módulo tem sua entidade principal (`Passageiro`, `Voo`, `CompanhiaAerea`, `Ticket`, etc.), com atributos, validação e auditoria.
**DAO**: Todos os DAOs herdam de `DaoBase<T>`, que implementa CRUD em memória com arrays expansíveis e métodos de busca por id. Exemplos: `PassageiroDao`, `TicketDao`, `VooDao`.
**Service**: Centralizam regras de negócio, validação, geração de campos (ex: código do ticket), e delegam persistência ao DAO. Usam `SystemClock` para datas de auditoria.
**Herança**: Classes em `comum/` (ex: `EntidadeBase`, `EntidadeNome`) fornecem padrões para entidades, como auditoria (`auditar(clock)`), validação (`validar()`), e formatação de campos.

## Convenções Específicas
**Validação**: Cada entidade implementa `validar()` para garantir integridade dos dados (ex: Ticket exige valor >= 0, voo e passageiro não nulos, código não vazio).
**Auditoria**: Campos `dataCriacao` e `dataModificacao` são atualizados via `auditar(clock)` em todas entidades.
**Formatação**: Nomes são formatados para maiúsculas em `EntidadeNome`.
**CRUD**: Operações de criação, atualização e remoção usam cópia/clonagem (`cloneEntity`) para evitar mutação direta.
**Geração de código de ticket**: O campo `codigo` do ticket pode ser `id-voo` ou 5 letras aleatórias (ver `TicketService`).

## Build, Execução e Debug
**Compilação**: Compile todos os arquivos para `bin/`:
  ```bash
  javac -d bin comum/*.java companhia/*.java voo/*.java passageiro/*.java ticket/*.java main/*.java
  ```
**Execução**: Rode o CLI principal:
  ```bash
  java -cp bin main.Main
  ```
**Debug**: Não há testes automatizados ou frameworks de logging presentes. Debug é feito por prints e inspeção manual (ex: prints nos menus e services).

## Padrões de Projeto
**Separação de responsabilidades**: DAO para persistência, Service para lógica, entidades para dados. Menus no `main/Main.java`.
**Não há uso de frameworks externos**: O projeto é puro Java, sem dependências externas.
**Arrays expansíveis**: Persistência em memória usa arrays com expansão manual (`DaoBase`).

## Exemplos de Uso
### Criar passageiro
```java
PassageiroService service = new PassageiroService(new PassageiroDao(), new SystemClock());
service.criar("João", LocalDate.of(1990,1,1), "123456", "joao", "senha");
```
### Criar ticket
```java
TicketService ticketService = new TicketService(new TicketDao(), new SystemClock());
ticketService.criar(250.0, voo, passageiro);
```
### Buscar voos por origem/destino/data
```java
Voo[] voos = vooService.buscarPorOrigemDestinoData("GRU", "SDU", LocalDate.of(2025,12,5));
```

## Pontos de Atenção
O nome da classe principal pode precisar ser ajustado para execução.
O projeto não possui testes automatizados, scripts de build ou integração contínua.
Não há integração com banco de dados ou APIs externas.
Os menus do CLI (`main/Main.java`) integram todos os serviços e DAOs, e são o ponto de entrada para fluxos de compra, busca e CRUD.

## Referências de Arquivos
Exemplos de padrões: `ticket/TicketService.java`, `ticket/TicketDao.java`, `comum/DaoBase.java`, `comum/EntidadeBase.java`, `voo/VooService.java`, `companhia/CompanhiaAereaService.java`
Fluxo principal: `main/Main.java` (menus e integração)

---

Seções incompletas ou dúvidas sobre fluxos podem ser detalhadas conforme necessidade do usuário.
