package admin;

import companhia.CompanhiaAerea;
import comum.Repositorio;
import comum.SystemClock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import passageiro.Passageiro;
import ticket.Ticket;
import voo.Voo;

public class AdminService {
    private final Repositorio<Ticket> ticketDao;
    private final Repositorio<Voo> vooDao;
    private final Repositorio<Passageiro> passageiroDao;
    private final SystemClock clock;

    public AdminService(Repositorio<Ticket> ticketDao,
                        Repositorio<Voo> vooDao,
                        Repositorio<Passageiro> passageiroDao,
                        SystemClock clock) {
        this.ticketDao = ticketDao;
        this.vooDao = vooDao;
        this.passageiroDao = passageiroDao;
        this.clock = clock;
    }

    public Passageiro[] passageirosQueSairamDe(String codigoAeroporto) {
        if (codigoAeroporto == null || codigoAeroporto.trim().isEmpty()) return new Passageiro[0];
        String origem = codigoAeroporto.trim().toUpperCase();
        List<Passageiro> result = new ArrayList<>();

        for (Ticket ticket : ticketDao.findAll()) {
            Voo voo = carregarVoo(ticket);
            if (voo == null || voo.getOrigem() == null) continue;
            if (origem.equalsIgnoreCase(voo.getOrigem())) {
                Passageiro passageiro = carregarPassageiro(ticket);
                if (passageiro != null) result.add(passageiro);
            }
        }
        return result.toArray(new Passageiro[0]);
    }

    public Passageiro[] passageirosQueChegaramEm(String codigoAeroporto) {
        if (codigoAeroporto == null || codigoAeroporto.trim().isEmpty()) return new Passageiro[0];
        String destino = codigoAeroporto.trim().toUpperCase();
        List<Passageiro> result = new ArrayList<>();

        for (Ticket ticket : ticketDao.findAll()) {
            Voo voo = carregarVoo(ticket);
            if (voo == null || voo.getDestino() == null) continue;
            if (destino.equalsIgnoreCase(voo.getDestino())) {
                Passageiro passageiro = carregarPassageiro(ticket);
                if (passageiro != null) result.add(passageiro);
            }
        }
        return result.toArray(new Passageiro[0]);
    }

    public double arrecadacaoPorCompanhia(CompanhiaAerea companhia, LocalDate inicio, LocalDate fim) {
        if (companhia == null || companhia.getId() <= 0 || inicio == null || fim == null) {
            throw new IllegalArgumentException("Parametros invalidos para arrecadacao.");
        }

        double total = 0.0;
        for (Ticket ticket : ticketDao.findAll()) {
            Voo voo = carregarVoo(ticket);
            if (voo == null || voo.getCompanhiaAerea() == null) continue;
            if (voo.getCompanhiaAerea().getId() != companhia.getId()) continue;

            LocalDate dataVoo = voo.getData();
            if (dataVoo != null && dentroDoPeriodo(dataVoo, inicio, fim)) {
                total += ticket.getValor();
            }
        }
        return total;
    }

    private Voo carregarVoo(Ticket ticket) {
        if (ticket == null || ticket.getVoo() == null) return null;
        Voo voo = ticket.getVoo();
        if (voo.getOrigem() != null && voo.getDestino() != null && voo.getCompanhiaAerea() != null) {
            return voo;
    }
        return vooDao.findById(voo.getId());
    }

    private Passageiro carregarPassageiro(Ticket ticket) {
        if (ticket == null || ticket.getPassageiro() == null) return null;
        Passageiro passageiro = ticket.getPassageiro();
        return (passageiro.getNome() != null) ? passageiro : passageiroDao.findById(passageiro.getId());
    }

    private boolean dentroDoPeriodo(LocalDate data, LocalDate inicio, LocalDate fim) {
        return (data.isEqual(inicio) || data.isAfter(inicio))
                && (data.isEqual(fim) || data.isBefore(fim));
    }
    
    public List <Voo> voosPorDiaAeroporto (LocalDate data, String codigoAeroporto){
        if (data == null) {
            throw new IllegalArgumentException("Data obrigatoria");
        }
        if(codigoAeroporto == null || codigoAeroporto.isBlank()) {
            throw new IllegalArgumentException("Codigo do aeroporto obrigatorio");
        }
        String codigo = codigoAeroporto.trim().toUpperCase();

        List<Voo> resultado = new ArrayList<>();
        for (Voo voo : vooDao.findAll()) {
            if (voo == null || voo.getData() == null) continue;
            boolean mesmaData = data.equals(voo.getData());
            boolean mesmoAeroporto = codigo.equalsIgnoreCase(voo.getOrigem())
                    || codigo.equalsIgnoreCase(voo.getDestino());
            if (mesmaData && mesmoAeroporto) {
                resultado.add(voo);
            }
        }
        return resultado;
    }
    
    public List<Passageiro> listaPassageirosPorVoo(int vooId) {
        if (vooId <= 0) {
            throw new IllegalArgumentException("ID do voo invalido.");
        }
        Set<Integer> vistos = new LinkedHashSet<>();
        List<Passageiro> passageiros = new ArrayList<>();

        for (Ticket ticket : ticketDao.findAll()) {
            if (ticket == null || ticket.getVoo() == null || ticket.getVoo().getId() != vooId) continue;
            Passageiro passageiro = ticket.getPassageiro();
            if (Objects.nonNull(passageiro) && vistos.add(passageiro.getId())) {
                passageiros.add(passageiro);
            }
        }
        return passageiros;
    }
}