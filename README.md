Sistema de Gerenciamento de Aeroporto
Este projeto é um sistema simples, feito em Java, para gerenciar passageiros, voos, companhias aéreas e tickets (passagens). Ele simula o funcionamento básico de um aeroporto, permitindo cadastrar pessoas, criar voos, vender passagens e consultar informações. O sistema funciona todo pelo terminal (linha de comando), com menus interativos.

Estrutura do Projeto
O código está organizado em diferentes pastas (pacotes), cada uma com uma responsabilidade:

main/
Contém o arquivo principal (Main.java) que mostra os menus e integra todas as funções do sistema.

companhia/
Classes para companhias aéreas: cadastro, busca, atualização e remoção.

passageiro/
Classes para passageiros: cadastro, busca, atualização e remoção.

voo/
Classes para voos: cadastro, busca por origem/destino/data, atualização e remoção.

ticket/
Classes para tickets (passagens): criação, busca, remoção e regras de negócio.

comum/
Classes base e utilitários, como controle de datas, auditoria e padrões para entidades.

Como funciona cada parte
1. Entidades
Cada módulo tem uma classe principal que representa um "objeto do mundo real":

Passageiro: pessoa que pode comprar passagens.
CompanhiaAerea: empresa responsável pelos voos.
Voo: viagem entre dois aeroportos, com data, duração, companhia e estado (programado, embarque, etc).
Ticket: passagem comprada por um passageiro para um voo.
Essas classes têm atributos (ex: nome, data de nascimento, origem, destino) e métodos para validar os dados e registrar quando foram criadas/modificadas.

2. DAO (Data Access Object)
Cada entidade tem um DAO, que é responsável por guardar os dados em memória (usando arrays).
Exemplo: PassageiroDao, VooDao, TicketDao.
Eles permitem criar, buscar, atualizar e remover objetos.

3. Service
Os serviços são responsáveis pelas regras de negócio.
Exemplo:

Validar se um passageiro pode ser criado.
Gerar o código do ticket (ex: id-voo).
Auditar datas de criação/modificação.
Buscar voos por origem, destino e data.
4. Menus (CLI)
No arquivo principal (Main.java), há menus para cada função do sistema.
O usuário pode escolher opções como:

Gerenciar companhias aéreas
Gerenciar passageiros
Gerenciar voos
Gerenciar tickets
Comprar itinerário de ida e volta
Cada menu permite criar, listar, buscar, atualizar e remover registros.

Fluxo de uso típico
Cadastro de dados iniciais
Ao iniciar, o sistema já cria alguns passageiros, companhias e voos para facilitar os testes.

Navegação pelos menus
O usuário escolhe o que deseja gerenciar (companhias, passageiros, voos, tickets).

Compra de passagens
É possível buscar voos por origem, destino e data, e comprar tickets para ida e volta.

Consulta e remoção
O usuário pode buscar registros por ID ou código, listar todos, atualizar ou remover.

Regras de Negócio
Validação: Não é possível criar tickets sem passageiro, voo ou valor válido.
Auditoria: Toda entidade registra quando foi criada e modificada.
Formatação: Nomes são salvos em maiúsculas.
Código do ticket: É gerado automaticamente como id-voo.
Persistência: Os dados ficam em memória enquanto o programa está rodando (não são salvos em arquivos ou banco de dados).
Como executar
Compilar
No terminal, digite:

Executar

Observações
O sistema não usa banco de dados, tudo é feito em memória.
Não há testes automáticos ou integração com outros sistemas.
O código é todo em Java puro, sem frameworks externos.
Para testar, basta seguir os menus e usar os dados já cadastrados ou criar novos.
