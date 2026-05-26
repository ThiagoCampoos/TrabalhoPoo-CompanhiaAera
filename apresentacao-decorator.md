# Apresentação textual: uso do padrão Decorator no módulo de Ticket

## 1. Objetivo da mudança

O padrão Decorator foi usado para separar o preço base do ticket dos adicionais que podem ser combinados dinamicamente.

Antes, o ticket tinha um valor único e qualquer regra extra exigiria alterar a entidade principal ou criar vários métodos especiais. Com o Decorator, o preço passa a ser montado por composição.

## 2. Onde o padrão foi aplicado

A implementação ficou concentrada no módulo `ticket`:

- `ITicketComponent`: contrato com `getPrecoTotal()` e `getDescricao()`.
- `BaseTicketComponent`: representa o ticket sem adicionais, devolvendo o valor base.
- `AbstractTicketDecorator`: classe abstrata que delega para o componente interno.
- `TaxaAeroportoDecorator`: aplica uma taxa percentual fixa.
- `SeguroDecorator`: adiciona um valor fixo de seguro.
- `ExtraBagDecorator`: soma o valor de bagagens extras.
- `PromocaoDecorator`: aplica desconto percentual.

O `TicketService` monta a cadeia e grava o resultado final no ticket antes de persistir em memória.

## 3. Como funciona na prática

O fluxo ficou assim:

1. O usuário informa o valor base do ticket.
2. O menu pergunta se haverá seguro, quantas bagagens extras existem e qual é a promoção.
3. O `TicketService` cria um `BaseTicketComponent` com o valor base.
4. Os decorators são empilhados conforme as opções escolhidas.
5. O preço final e a descrição dos extras são gravados em `precoTotal` e `descricaoExtras`.

Exemplo de composição:

- Base: R$ 100
- Taxa de aeroporto: 5%
- Seguro: R$ 15
- 2 bagagens extras: R$ 80
- Promoção: 10%

Resultado aproximado:

- R$ 100 -> R$ 105 com taxa
- - R$ 15 = R$ 120
- - R$ 80 = R$ 200
- - 10% = R$ 180

## 4. Mudanças nos menus

Os menus de criação de ticket foram atualizados para perguntar:

- se o passageiro deseja seguro;
- quantas bagagens extras serão adicionadas;
- se existe promoção e qual o percentual.

Isso foi aplicado no menu de ticket simples e no fluxo de compra de itinerário ida e volta.

## 5. Vantagens da solução

- Facilita adicionar novos adicionais sem mexer na lógica principal do ticket.
- Evita criar subclasses para cada combinação possível.
- Torna o cálculo do preço mais legível e modular.
- Permite reutilizar a mesma lógica em diferentes fluxos do sistema.

## 6. Casos de uso

A solução é útil quando o ticket pode variar por regras opcionais, por exemplo:

- seguro opcional;
- bagagem extra;
- promoções temporárias;
- taxas adicionais por política da companhia.

## 7. Observação sobre persistência

Como o projeto vai usar apenas memória, o sistema grava o preço final calculado no próprio ticket. Isso é suficiente para o uso atual e mantém o comportamento simples.

## 8. Conclusão

O Decorator foi aplicado para transformar o ticket em uma estrutura flexível e extensível. O preço base continua simples, enquanto as regras extras são encaixadas dinamicamente conforme a necessidade do usuário.
