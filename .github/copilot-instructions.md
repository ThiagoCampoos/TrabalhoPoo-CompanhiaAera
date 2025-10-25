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
