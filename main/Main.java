package main;

import admin.*;
import boarding.*;
import checkin.Checkin;
import checkin.CheckinDaoJdbc;
import checkin.CheckinService;
import companhia.*;
import comum.SystemClock;
import despacho.*;
import entrada.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import passageiro.*;
import reserva.*;
import ticket.*;
import user.AuthService;
import user.Perfil;
import user.SessaoUser;
import user.UserDaoJdbc;
import user.UserService;
import voo.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SystemClock clock = new SystemClock();
        CompanhiaAereaService companhiaService = new CompanhiaAereaService(new CompanhiaAereaDaojdbc(), clock);
        PassageiroDaoJdbc passageiroDao = new PassageiroDaoJdbc();
        PassageiroService passageiroService = new PassageiroService(passageiroDao, clock);
        PassageiroArquivoService passageiroArquivoService = new PassageiroArquivoService(passageiroService);
        VooService vooService = new VooService(new VooDaoJdbc(), new SystemClock());
        TicketService ticketService = new TicketService(new TicketDaoJdbc(), new SystemClock());
        CheckinService checkinService = new CheckinService(new CheckinDaoJdbc(),
                ticketService, vooService, clock);
        ReservaService reservaService = new ReservaService(new ReservaDaoJdbc(), new SystemClock());
        DespachoService despachoService = new DespachoService(new DespachoDaoJdbc(), new SystemClock());
        EntradaService entradaService = new EntradaService(new EntradaDaoJdbc(), new EntradaAviaoDaoJdbc(),
                new SystemClock());
        BoardingPassService boardingService = new BoardingPassService(new BoardingPassDaoJdbc(), new SystemClock());
        AdminService adminService = new AdminService(new TicketDaoJdbc(), new VooDaoJdbc(), new PassageiroDaoJdbc(),
                new SystemClock());
        UserService userService = new UserService(new UserDaoJdbc(), new SystemClock());
        String senhaPadraoAdmin = System.getProperty("ADMIN_DEFAULT_PASSWORD",
                System.getenv("ADMIN_DEFAULT_PASSWORD"));
        userService.garantirAdministradorInicial(senhaPadraoAdmin);
        AuthService authService = new AuthService(userService, passageiroDao, clock);
        SessaoUser sessaoAtual = realizarLogin(sc, authService);

        while (true) {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("1 - Gerenciar Companhias Aereas");
            System.out.println("2 - Gerenciar Passageiros");
            System.out.println("3 - Gerenciar Voos");
            System.out.println("4 - Gerenciar Tickets");
            System.out.println("5 - Compra de intinerario Ida e Volta");
            System.out.println("6 - Check-in");
            System.out.println("7 - Operacoes Aeroporto (Reserva/Despacho/Entradas/Boarding/Relatorios)");
            System.out.println("8 - Importar Passageiros via Arquivo");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 0)
                break;

            switch (opcao) {
                case 1:
                    authService.exigirPerfil(Perfil.ADMIN);
                    menuCompanhia(sc, companhiaService, sessaoAtual);
                    break;
                case 2:
                    authService.exigirPerfil(Perfil.ADMIN, Perfil.PASSAGEIRO);
                    menuPassageiro(sc, passageiroService, sessaoAtual);
                    break;
                case 3:
                    authService.exigirPerfil(Perfil.ADMIN, Perfil.FUNCIONARIO);
                    menuVoo(sc, vooService, companhiaService, sessaoAtual);
                    break;
                case 4:
                    authService.exigirPerfil(Perfil.ADMIN, Perfil.FUNCIONARIO, Perfil.PASSAGEIRO);
                    menuTicket(sc, ticketService, vooService, passageiroService, sessaoAtual);
                    break;
                case 5:
                    authService.exigirPerfil(Perfil.PASSAGEIRO, Perfil.ADMIN);
                    menuItinerario(sc, vooService, ticketService, passageiroService, sessaoAtual);
                    break;
                case 6:
                    authService.exigirPerfil(Perfil.PASSAGEIRO, Perfil.ADMIN);
                    menuCheckin(sc, checkinService, ticketService, passageiroService, vooService, sessaoAtual);
                    break;
                case 7:
                    authService.exigirPerfil(Perfil.ADMIN, Perfil.FUNCIONARIO);
                    menuOperacoesAeroporto(sc, reservaService, despachoService,
                            entradaService, boardingService, adminService, companhiaService, sessaoAtual);
                    break;
                case 8:
                    authService.exigirPerfil(Perfil.ADMIN);
                    menuImportacaoPassageiros(sc, passageiroArquivoService);
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
        sc.close();
    }

    private static SessaoUser realizarLogin(Scanner sc, AuthService authService) {
        while (true) {
            try {
                System.out.print("Login: ");
                String login = sc.nextLine();
                System.out.print("Senha: ");
                String senha = sc.nextLine();
                SessaoUser sessao = authService.autenticar(login, senha);
                System.out.println("Autenticado como " + sessao.getUsuario().getLogin());
                return sessao;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void menuCheckin(Scanner sc, CheckinService checkinService, TicketService ticketService,
            PassageiroService passageiroService, VooService vooService, SessaoUser sessaoAtual) {
        while (true) {
            System.out.println("\n=== Menu Check-in ===");
            System.out.println("1 - Fazer check-in");
            System.out.println("2 - Gerar boarding pass por ticketId");
            System.out.println("3 - Buscar check-in por ticketId");
            System.out.println("4 - Listar todos");
            System.out.println("5 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 0)
                break;

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("ID do ticket: ");
                        int ticketId = sc.nextInt();
                        sc.nextLine();
                        System.out.print("ID do passageiro: ");
                        int passageiroIdConf = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Assento (enter para AUTO): ");
                        String assento = sc.nextLine();
                        checkin.Checkin criado = checkinService.criar(ticketId, passageiroIdConf, assento);
                        System.out.println("Check-in criado: " + criado);
                        System.out.println(checkinService.gerarBoardingPass(criado));
                        break;
                    case 2:
                        System.out.print("ID do ticket para boarding pass: ");
                        int tId = sc.nextInt();
                        sc.nextLine();
                        checkin.Checkin chk = checkinService.buscarPorTicketId(tId);
                        if (chk == null) {
                            System.out.println("Nenhum check-in encontrado para esse ticket.");
                        } else {
                            System.out.println(checkinService.gerarBoardingPass(chk));
                        }
                        break;
                    case 3:
                        System.out.print("ID do ticket: ");
                        int tBuscar = sc.nextInt();
                        sc.nextLine();
                        checkin.Checkin encontrado = checkinService.buscarPorTicketId(tBuscar);
                        System.out.println(encontrado != null ? encontrado : "Nao encontrado.");
                        break;
                    case 4:
                        Checkin[] todos = checkinService.listarTodos();
                        for (Checkin c : todos) {
                            System.out.println(c);
                        }
                        break;
                    case 5:
                        System.out.print("ID do check-in a remover: ");
                        int idRem = sc.nextInt();
                        sc.nextLine();
                        boolean rem = checkinService.excluir(idRem);
                        System.out.println(rem ? "Removido." : "Nao encontrado.");
                        break;
                    default:
                        System.out.println("Opcao inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuItinerario(Scanner sc, VooService vooService, TicketService ticketService,
            PassageiroService passageiroService, SessaoUser sessaoAtual) {
        System.out.println("\n=== Compra de Itinerario Ida e Volta ===");
        System.out.print("Origem: ");
        String origem = sc.nextLine();
        System.out.print("Destino: ");
        String destino = sc.nextLine();
        System.out.print("Data ida (yyyy-MM-dd): ");
        LocalDate dataIda = LocalDate.parse(sc.nextLine());
        System.out.print("Data volta (yyyy-MM-dd, enter para pular): ");
        String dataVoltaStr = sc.nextLine();
        LocalDate dataVolta = dataVoltaStr.isEmpty() ? null : LocalDate.parse(dataVoltaStr);

        Voo[] voosIda = vooService.buscarPorOrigemDestinoData(origem, destino, dataIda);
        Voo[] voosVolta = dataVolta != null ? vooService.buscarPorOrigemDestinoData(destino, origem, dataVolta)
                : new Voo[0];

        if (voosIda.length == 0) {
            System.out.println("Nenhum voo de ida disponivel.");
            return;
        }
        System.out.println("Voos de ida disponiveis:");
        for (int i = 0; i < voosIda.length; i++) {
            System.out.println((i + 1) + " - " + voosIda[i]);
        }
        System.out.print("Escolha o voo de ida (numero): ");
        int escolhaIda = sc.nextInt() - 1;
        sc.nextLine();
        Voo vooIda = voosIda[escolhaIda];

        Voo vooVolta = null;
        if (dataVolta != null && voosVolta.length > 0) {
            System.out.println("Voos de volta disponiveis:");
            for (int i = 0; i < voosVolta.length; i++) {
                System.out.println((i + 1) + " - " + voosVolta[i]);
            }
            System.out.print("Escolha o voo de volta (numero): ");
            int escolhaVolta = sc.nextInt() - 1;
            sc.nextLine();
            vooVolta = voosVolta[escolhaVolta];
        }

        System.out.print("ID do passageiro: ");
        int idPassageiro = sc.nextInt();
        sc.nextLine();
        Passageiro passageiro = passageiroService.buscarPorId(idPassageiro);

        System.out.print("Valor do ticket de ida: ");
        double valorIda = sc.nextDouble();
        sc.nextLine();
        Ticket ticketIda = ticketService.criar(valorIda, vooIda, passageiro, null, null, "null");
        System.out.println("Ticket de ida criado: " + ticketIda);

        if (vooVolta != null) {
            System.out.print("Valor do ticket de volta: ");
            double valorVolta = sc.nextDouble();
            sc.nextLine();
            Ticket ticketVolta = ticketService.criar(valorVolta, vooVolta, passageiro, null, null, "AUTO");
            System.out.println("Ticket de volta criado: " + ticketVolta);
        }
    }

    private static void menuTicket(Scanner sc, TicketService ticketService, VooService vooService,
            PassageiroService passageiroService, SessaoUser sessaoAtual) {
        while (true) {
            System.out.println("\n=== Menu Ticket ===");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar todos");
            System.out.println("3 - Buscar por ID");
            System.out.println("4 - Buscar por codigo");
            System.out.println("5 - Remover");
            System.out.println("6 - Atribuir assento a ticket");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 0)
                break;

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Valor: ");
                        double valor = sc.nextDouble();
                        sc.nextLine();
                        System.out.print("ID do voo: ");
                        int idVoo = sc.nextInt();
                        sc.nextLine();
                        System.out.print("ID do passageiro: ");
                        int idPassageiro = sc.nextInt();
                        sc.nextLine();
                        Voo voo = vooService.buscarPorId(idVoo);
                        Passageiro passageiro = passageiroService.buscarPorId(idPassageiro);
                        if (voo == null || passageiro == null) {
                            System.out.println("Voo ou passageiro nao encontrado.");
                            break;
                        }
                        Ticket novo = ticketService.criar(valor, voo, passageiro, null, null, "AUTO");
                        System.out.println("Criado: " + novo);
                        break;
                    case 2:
                        for (Ticket t : ticketService.listarTodos()) {
                            System.out.println(t);
                        }
                        break;
                    case 3:
                        System.out.print("ID: ");
                        int idBusca = sc.nextInt();
                        sc.nextLine();
                        Ticket encontrado = ticketService.buscarPorId(idBusca);
                        System.out.println(encontrado != null ? encontrado : "Nao encontrado.");
                        break;
                    case 4:
                        System.out.print("Codigo: ");
                        String codigoBusca = sc.nextLine();
                        Ticket encontradoCodigo = ticketService.buscarPorCodigo(codigoBusca);
                        System.out.println(encontradoCodigo != null ? encontradoCodigo : "Nao encontrado.");
                        break;
                    case 5:
                        System.out.print("ID: ");
                        int idRemover = sc.nextInt();
                        sc.nextLine();
                        boolean removido = ticketService.remover(idRemover);
                        System.out.println(removido ? "Removido." : "Não encontrado.");
                        break;
                    case 6:
                        System.out.print("ID do ticket: ");
                        int ticketParaAssento = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Assento desejado: ");
                        String assentoEscolhido = sc.nextLine();
                        Ticket atualizadoAssento = ticketService.atribuirAssento(ticketParaAssento, assentoEscolhido);
                        System.out.println("Assento atualizado: " + atualizadoAssento);
                        break;
                    default:
                        System.out.println("Opcao inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuVoo(Scanner sc, VooService vooService, CompanhiaAereaService companhiaService,
            SessaoUser sessaoAtual) {
        while (true) {
            System.out.println("\n=== Menu Voo ===");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar todos");
            System.out.println("3 - Buscar por ID");
            System.out.println("4 - Buscar por origem");
            System.out.println("5 - Buscar por destino");
            System.out.println("6 - Buscar por data");
            System.out.println("7 - Buscar por origem, destino e data");
            System.out.println("8 - Atualizar");
            System.out.println("9 - Remover");
            System.out.println("10 - Alterar estado");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 0)
                break;

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Origem: ");
                        String origem = sc.nextLine();
                        System.out.print("Destino: ");
                        String destino = sc.nextLine();
                        System.out.print("Data (yyyy-MM-dd): ");
                        LocalDate data = LocalDate.parse(sc.nextLine());
                        System.out.print("Duracao (HH:mm): ");
                        LocalTime duracao = LocalTime.parse(sc.nextLine());
                        System.out.print("Horario do voo (HH:mm): ");
                        LocalTime horario = LocalTime.parse(sc.nextLine());
                        System.out.print("ID da companhia aerea: ");
                        int idCompanhia = sc.nextInt();
                        sc.nextLine();
                        CompanhiaAerea companhia = companhiaService.buscarPorId(idCompanhia);
                        System.out.print("Capacidade: ");
                        int capacidade = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Estado (PROGRAMADO, ATRASADO, CANCELADO, FINALIZADO): ");
                        EstadoVoo estado = EstadoVoo.valueOf(sc.nextLine().toUpperCase());
                        System.out.print("Data/hora de ida (yyyy-MM-dd HH:mm): ");
                        String ida = sc.nextLine();
                        System.out.print("Data/hora de volta (yyyy-MM-dd HH:mm ou 'null'): ");
                        String volta = sc.nextLine();
                        Voo novo = vooService.criar(id, origem, destino, data, duracao, horario, companhia, capacidade,
                                estado, ida, volta);
                        System.out.println("Criado: " + novo);
                        break;
                    case 2:
                        for (Voo v : vooService.listarTodos()) {
                            System.out.println(v);
                        }
                        break;
                    case 3:
                        System.out.print("ID: ");
                        int idBusca = sc.nextInt();
                        sc.nextLine();
                        Voo encontrado = vooService.buscarPorId(idBusca);
                        System.out.println(encontrado != null ? encontrado : "Não encontrado.");
                        break;
                    case 4:
                        System.out.print("Origem: ");
                        String origemBusca = sc.nextLine();
                        for (Voo v : vooService.buscarPorOrigem(origemBusca)) {
                            System.out.println(v);
                        }
                        break;
                    case 5:
                        System.out.print("Destino: ");
                        String destinoBusca = sc.nextLine();
                        for (Voo v : vooService.buscarPorDestino(destinoBusca)) {
                            System.out.println(v);
                        }
                        break;
                    case 6:
                        System.out.print("Data (yyyy-MM-dd): ");
                        LocalDate dataBusca = LocalDate.parse(sc.nextLine());
                        for (Voo v : vooService.buscarPorData(dataBusca)) {
                            System.out.println(v);
                        }
                        break;
                    case 7:
                        System.out.print("Origem: ");
                        String origemOD = sc.nextLine();
                        System.out.print("Destino: ");
                        String destinoOD = sc.nextLine();
                        System.out.print("Data e hora (yyyy-MM-ddTHH:mm): ");
                        LocalDate dataOD = LocalDate.parse(sc.nextLine());
                        for (Voo v : vooService.buscarPorOrigemDestinoData(origemOD, destinoOD, dataOD)) {
                            System.out.println(v);
                        }
                        break;
                    case 8:
                        System.out.print("ID: ");
                        int idAtualizar = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Nova origem: ");
                        String novaOrigem = sc.nextLine();
                        System.out.print("Novo destino: ");
                        String novoDestino = sc.nextLine();
                        System.out.print("Nova data e hora (yyyy-MM-dd): ");
                        LocalDate novaData = LocalDate.parse(sc.nextLine());
                        System.out.print("Nova duracao (HH:mm): ");
                        LocalTime novaDuracao = LocalTime.parse(sc.nextLine());
                        System.out.print("ID da companhia aerea: ");
                        int idCompanhiaAtualizar = sc.nextInt();
                        sc.nextLine();
                        CompanhiaAerea companhiaAtualizar = companhiaService.buscarPorId(idCompanhiaAtualizar);

                        System.out.print("Nova capacidade: ");
                        int novaCapacidade = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Novo estado (PROGRAMADO, ATRASADO, CANCELADO, FINALIZADO): ");
                        EstadoVoo novoEstado = EstadoVoo.valueOf(sc.nextLine().toUpperCase());
                        System.out.print("Nova data/hora de ida (yyyy-MM-dd HH:mm): ");
                        String novaIda = sc.nextLine();
                        System.out.print("Nova data/hora de volta (yyyy-MM-dd HH:mm ou 'null'): ");
                        String novaVolta = sc.nextLine();
                        System.out.print("Novo horario do voo (HH:mm, enter para manter): ");
                        String novoHorarioStr = sc.nextLine();
                        LocalTime novoHorario = null;
                        if (novoHorarioStr != null && !novoHorarioStr.trim().isEmpty()) {
                            novoHorario = LocalTime.parse(novoHorarioStr.trim());
                        }
                        Voo atualizado = vooService.atualizar(idAtualizar, novaOrigem, novoDestino, novaData,
                                novaDuracao, novoHorario, companhiaAtualizar, novaCapacidade, novoEstado, novaIda,
                                novaVolta);
                        System.out.println("Atualizado: " + atualizado);
                        break;
                    case 9:
                        System.out.print("ID: ");
                        int idRemover = sc.nextInt();
                        sc.nextLine();
                        boolean removido = vooService.remover(idRemover);
                        System.out.println(removido ? "Removido." : "Não encontrado.");
                        break;
                    case 10:
                        System.out.print("ID do voo: ");
                        int idEstado = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Novo estado (PROGRAMADO, ATRASADO, CANCELADO, FINALIZADO): ");
                        EstadoVoo estadoNovo = EstadoVoo.valueOf(sc.nextLine().toUpperCase());
                        vooService.alterarEstado(idEstado, estadoNovo);
                        System.out.println("Estado alterado.");
                        break;
                    default:
                        System.out.println("Opcao inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuPassageiro(Scanner sc, PassageiroService passageiroService, SessaoUser sessaoAtual) {
        while (true) {
            System.out.println("\n=== Menu Passageiro ===");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar por ID");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 0)
                break;

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Nome: ");
                        String nome = sc.nextLine();
                        System.out.print("Data de nascimento (yyyy-MM-dd): ");
                        LocalDate nascimento = LocalDate.parse(sc.nextLine());
                        System.out.print("Documento: ");
                        String documento = sc.nextLine();
                        System.out.print("Login: ");
                        String login = sc.nextLine();
                        System.out.print("Senha: ");
                        String senha = sc.nextLine();
                        Passageiro novo = passageiroService.criar(nome, nascimento, documento, login, senha);
                        System.out.println("Criado: " + novo);
                        break;
                    case 2:
                        Passageiro[] todos = passageiroService.listarTodos();
                        for (Passageiro p : todos) {
                            System.out.println(p);
                        }
                        break;
                    case 3:
                        System.out.print("ID: ");
                        int idBusca = sc.nextInt();
                        sc.nextLine();
                        Passageiro encontrado = passageiroService.buscarPorId(idBusca);
                        System.out.println(encontrado != null ? encontrado : "Não encontrado.");
                        break;
                    case 4:
                        System.out.print("ID: ");
                        int idAtualizar = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Novo nome (enter para manter): ");
                        String novoNome = sc.nextLine();
                        System.out.print("Nova data de nascimento (yyyy-MM-ddTHH:mm, enter para manter): ");
                        String novaDataStr = sc.nextLine();
                        LocalDate novoNascimento = null;
                        if (!novaDataStr.isEmpty()) {
                            novoNascimento = LocalDate.parse(novaDataStr);
                        }
                        System.out.print("Novo documento (enter para manter): ");
                        String novoDocumento = sc.nextLine();
                        System.out.print("Novo login (enter para manter): ");
                        String novoLogin = sc.nextLine();
                        System.out.print("Nova senha (enter para manter): ");
                        String novaSenha = sc.nextLine();
                        Passageiro atualizado = passageiroService.atualizar(
                                idAtualizar,
                                novoNome.isEmpty() ? null : novoNome,
                                novoNascimento,
                                novoDocumento.isEmpty() ? null : novoDocumento,
                                novoLogin.isEmpty() ? null : novoLogin,
                                novaSenha.isEmpty() ? null : novaSenha);
                        System.out.println("Atualizado: " + atualizado);
                        break;
                    case 5:
                        System.out.print("ID: ");
                        int idRemover = sc.nextInt();
                        sc.nextLine();
                        boolean removido = passageiroService.remover(idRemover);
                        System.out.println(removido ? "Removido." : "Não encontrado.");
                        break;
                    default:
                        System.out.println("Opcao inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuOperacoesAeroporto(Scanner sc, ReservaService reservaService,
            DespachoService despachoService, EntradaService entradaService,
            BoardingPassService boardingService, AdminService adminService, CompanhiaAereaService companhiaService,
            SessaoUser sessaoAtual) {
        while (true) {
            System.out.println("\n=== Operacoes Aeroporto ===");
            System.out.println("1 - Reservas (buscar/criar)");
            System.out.println("2 - Despacho de Bagagem (CRUD)");
            System.out.println("3 - Entrada (Aeroporto / Aviao)");
            System.out.println("4 - Boarding Pass (gerar / consultar)");
            System.out.println("5 - Relatorios Administrativos");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opc = sc.nextInt();
            sc.nextLine();
            if (opc == 0)
                break;
            try {
                switch (opc) {
                    case 1:
                        menuReserva(sc, reservaService);
                        break;
                    case 2:
                        menuDespacho(sc, despachoService);
                        break;
                    case 3:
                        menuEntrada(sc, entradaService);
                        break;
                    case 4:
                        menuBoarding(sc, boardingService);
                        break;
                    case 5:
                        menuAdmin(sc, adminService, companhiaService, boardingService);
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuReserva(Scanner sc, ReservaService reservaService) {
        while (true) {
            System.out.println("\n=== Menu Reserva ===");
            System.out.println("1 - Criar reserva");
            System.out.println("2 - Buscar por codigo + sobrenome");
            System.out.println("3 - Listar todas");
            System.out.println("4 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opc = sc.nextInt();
            sc.nextLine();
            if (opc == 0)
                break;
            try {
                switch (opc) {
                    case 1:
                        System.out.print("Codigo reserva: ");
                        String codigo = sc.nextLine();
                        System.out.print("Sobrenome do passageiro: ");
                        String sobrenome = sc.nextLine();
                        Reserva r = reservaService.criar(codigo, sobrenome, null);
                        System.out.println("Criada: " + r);
                        break;
                    case 2:
                        System.out.print("Codigo: ");
                        String c = sc.nextLine();
                        System.out.print("Sobrenome: ");
                        String s = sc.nextLine();
                        Reserva found = reservaService.buscarPorCodigoESobrenome(c, s);
                        System.out.println(found != null ? found : "Nao encontrada.");
                        break;
                    case 3:
                        for (Reserva rr : reservaService.listarTodos())
                            System.out.println(rr);
                        break;
                    case 4:
                        System.out.print("ID para remover: ");
                        int idRem = sc.nextInt();
                        sc.nextLine();
                        boolean rem = reservaService.remover(idRem);
                        System.out.println(rem ? "Removida." : "Nao encontrada.");
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuDespacho(Scanner sc, DespachoService despachoService) {
        while (true) {
            System.out.println("\n=== Menu Despacho Bagagem ===");
            System.out.println("1 - Criar despacho");
            System.out.println("2 - Listar todos");
            System.out.println("3 - Atualizar");
            System.out.println("4 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opc = sc.nextInt();
            sc.nextLine();
            if (opc == 0)
                break;
            try {
                switch (opc) {
                    case 1:
                        System.out.print("Ticket ID: ");
                        int tId = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Documento: ");
                        String doc = sc.nextLine();
                        System.out.print("Peso (kg): ");
                        double peso = Double.parseDouble(sc.nextLine());
                        Despacho d = despachoService.criar(tId, doc, peso);
                        System.out.println("Criado: " + d);
                        break;
                    case 2:
                        for (Despacho db : despachoService.listarTodos())
                            System.out.println(db);
                        break;
                    case 3:
                        System.out.print("ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Novo documento (enter para manter): ");
                        String nd = sc.nextLine();
                        System.out.print("Novo peso: ");
                        double np = Double.parseDouble(sc.nextLine());
                        Despacho at = despachoService.atualizar(id, nd, np);
                        System.out.println("Atualizado: " + at);
                        break;
                    case 4:
                        System.out.print("ID: ");
                        int idr = sc.nextInt();
                        sc.nextLine();
                        boolean r = despachoService.remover(idr);
                        System.out.println(r ? "Removido." : "Nao encontrado.");
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuEntrada(Scanner sc, EntradaService entradaService) {
        while (true) {
            System.out.println("\n=== Menu Entrada ===");
            System.out.println("1 - Registrar entrada na area do aeroporto");
            System.out.println("2 - Registrar entrada no aviao");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opc = sc.nextInt();
            sc.nextLine();
            if (opc == 0)
                break;
            try {
                switch (opc) {
                    case 1:
                        System.out.print("Ticket ID: ");
                        int t = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Area (ex: Sala VIP): ");
                        String area = sc.nextLine();
                        EntradaAeroporto ea = entradaService.registrarEntradaAeroporto(t, area);
                        System.out.println("Registrado: " + ea);
                        break;
                    case 2:
                        System.out.print("Ticket ID: ");
                        int ta = sc.nextInt();
                        sc.nextLine();
                        EntradaAviao ev = entradaService.registrarEntradaAviao(ta);
                        System.out.println("Registrado: " + ev);
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuBoarding(Scanner sc, BoardingPassService boardingService) {
        while (true) {
            System.out.println("\n=== Menu Boarding Pass ===");
            System.out.println("1 - Gerar boarding pass para ticket");
            System.out.println("2 - Buscar boarding pass por ticket");
            System.out.println("3 - Listar todos");
            System.out.println("4 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opc = sc.nextInt();
            sc.nextLine();
            if (opc == 0)
                break;
            try {
                switch (opc) {
                    case 1:
                        System.out.print("Ticket ID: ");
                        int tid = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Conteudo/impressao (texto): ");
                        String conteudo = sc.nextLine();
                        BoardingPass b = boardingService.criarParaTicket(tid, conteudo);
                        System.out.println("Gerado: " + b);
                        break;
                    case 2:
                        System.out.print("Ticket ID: ");
                        int q = sc.nextInt();
                        sc.nextLine();
                        BoardingPass found = boardingService.buscarPorTicketId(q);
                        System.out.println(found != null ? found : "Nao encontrado.");
                        break;
                    case 3:
                        for (BoardingPass bp : boardingService.listarTodos())
                            System.out.println(bp);
                        break;
                    case 4:
                        System.out.print("ID: ");
                        int idr = sc.nextInt();
                        sc.nextLine();
                        boolean rem = boardingService.remover(idr);
                        System.out.println(rem ? "Removido." : "Nao encontrado.");
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuAdmin(Scanner sc, AdminService adminService, CompanhiaAereaService companhiaService,
            BoardingPassService boardingService) {
        while (true) {
            System.out.println("\n=== Menu Admin / Relatorios ===");
            System.out.println("1 - Passageiros que sairam de um aeroporto (origem)");
            System.out.println("2 - Passageiros que chegaram em um aeroporto (destino)");
            System.out.println("3 - Arrecadacao por companhia em periodo");
            System.out.println("4 - Voos por dia em um aeroporto");
            System.out.println("5 - Lista de passageiros por voo");
            System.out.println("6 - Gerar boarding pass em arquivo");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opc = sc.nextInt();
            sc.nextLine();
            if (opc == 0)
                break;
            try {
                switch (opc) {
                    case 1:
                        System.out.print("Codigo aeroporto (origem): ");
                        String orig = sc.nextLine();
                        for (Object p : adminService.passageirosQueSairamDe(orig))
                            System.out.println(p);
                        break;
                    case 2:
                        System.out.print("Codigo aeroporto (destino): ");
                        String dest = sc.nextLine();
                        for (Object p : adminService.passageirosQueChegaramEm(dest))
                            System.out.println(p);
                        break;
                    case 3:
                        System.out.print("ID da companhia: ");
                        int idC = sc.nextInt();
                        sc.nextLine();
                        CompanhiaAerea c = companhiaService.buscarPorId(idC);
                        System.out.print("Data inicio (yyyy-MM-dd): ");
                        LocalDate ini = LocalDate.parse(sc.nextLine());
                        System.out.print("Data fim (yyyy-MM-dd): ");
                        LocalDate fim = LocalDate.parse(sc.nextLine());
                        System.out.println("Total arrecadado: " + adminService.arrecadacaoPorCompanhia(c, ini, fim));
                        break;
                    case 4:
                        System.out.print("Data (yyyy-MM-dd): ");
                        LocalDate data = LocalDate.parse(sc.nextLine());
                        System.out.print("Codigo aeroporto: ");
                        String codigo = sc.nextLine();
                        var voos = adminService.voosPorDiaAeroporto(data, codigo);
                        if (voos.isEmpty()) {
                            System.out.println("Nenhum voo encontrado.");
                        } else {
                            voos.forEach(System.out::println);
                        }
                        break;
                    case 5:
                        System.out.print("ID do voo: ");
                        int vooId = sc.nextInt();
                        sc.nextLine();
                        var passageiros = adminService.listaPassageirosPorVoo(vooId);
                        if (passageiros.isEmpty()) {
                            System.out.println("Nenhum passageiro encontrado para o voo.");
                        } else {
                            passageiros.forEach(System.out::println);
                        }
                        break;
                    case 6:
                        System.out.print("ID do ticket: ");
                        int ticketId = sc.nextInt();
                        sc.nextLine();
                        System.out.print("Arquivo destino (enter para padrao): ");
                        String destino = sc.nextLine();
                        Path arquivo = destino.isBlank() ? null : Paths.get(destino);
                        Path gerado = boardingService.gerarBoardingPassPdf(ticketId, arquivo);
                        System.out.println("Boarding pass salvo em: " + gerado.toAbsolutePath());
                        break;
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void menuCompanhia(Scanner sc, CompanhiaAereaService service, SessaoUser sessaoAtual) {
        while (true) {
            System.out.println("\n=== Menu Companhia Aerea ===");
            System.out.println("1 - Criar");
            System.out.println("2 - Listar");
            System.out.println("3 - Buscar por abreviacao");
            System.out.println("4 - Atualizar");
            System.out.println("5 - Remover");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (opcao == 0)
                break;
            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Abreviacao: ");
                    String abrev = sc.nextLine();
                    CompanhiaAerea ca = service.criar(nome, abrev);
                    System.out.println("Criada: " + ca);
                    break;
                case 2:
                    for (CompanhiaAerea c : service.listarTodos()) {
                        System.out.println(c);
                    }
                    break;
                case 3:
                    System.out.print("Abreviacao: ");
                    String busca = sc.nextLine();
                    CompanhiaAerea encontrada = service.buscarPorAbreviacao(busca);
                    System.out.println(encontrada != null ? encontrada : "Nao encontrada.");
                    break;
                case 4:
                    System.out.print("ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Novo nome: ");
                    String novoNome = sc.nextLine();
                    System.out.print("Nova abreviacao: ");
                    String novaAbrev = sc.nextLine();
                    CompanhiaAerea atualizada = service.atualizar(id, novoNome, novaAbrev);
                    System.out.println("Atualizada: " + atualizada);
                    break;
                case 5:
                    System.out.print("ID: ");
                    int idRemover = sc.nextInt();
                    sc.nextLine();
                    boolean removido = service.remover(idRemover);
                    System.out.println(removido ? "Removido." : "Nao encontrada.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void menuImportacaoPassageiros(Scanner sc, PassageiroArquivoService arquivoService) {
        while (true) {
            System.out.println("\n=== Importar Passageiros ===");
            System.out.println("1 - Ler arquivo .txt (nome;yyyy-MM-dd;documento;login;senha)");
            System.out.println("0 - Voltar");
            System.out.print("Escolha: ");
            int opc = sc.nextInt();
            sc.nextLine();
            if (opc == 0) break;

            try {
                if (opc == 1) {
                    System.out.print("Caminho do arquivo: ");
                    Path arquivo = Paths.get(sc.nextLine());
                    ResultadoImportacaoPassageiros resultado = arquivoService.importar(arquivo);
                    System.out.printf("Linhas: %d | Importados: %d | Falhas: %d%n",
                            resultado.linhasProcessadas(), resultado.importados(), resultado.falhas());
                    if (resultado.possuiFalhas()) {
                        resultado.erros().forEach(msg -> System.out.println(" - " + msg));
                    }
                } else {
                    System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }
}