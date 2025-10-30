package admin;

import companhia.CompanhiaAerea;
import comum.SystemClock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import passageiro.Passageiro;
import passageiro.PassageiroDao;
import ticket.Ticket;
import ticket.TicketDao;
import voo.Voo;
import voo.VooDao;

public class AdminService {
    private final TicketDao ticketDao;
    private final VooDao vooDao;
    private final PassageiroDao passageiroDao;
    private final SystemClock clock;

    public AdminService(TicketDao ticketDao, VooDao vooDao, PassageiroDao passageiroDao, SystemClock clock) {
        this.ticketDao = ticketDao; this.vooDao = vooDao; this.passageiroDao = passageiroDao; this.clock = clock;
    }

    // 1 Passageiros que deixaram um determinado aeroporto (origem)
    public Passageiro[] passageirosQueSaíramDe(String codigoAeroporto) {
        List<Passageiro> result = new ArrayList<>();
        for (Ticket t : ticketDao.findAll()) {
            if (t == null || t.getVoo() == null) continue;
            Voo v = t.getVoo();
            if (codigoAeroporto.equalsIgnoreCase(v.getOrigem())) {
                Passageiro p = t.getPassageiro();
                if (p != null) result.add(p);
            }
        }
        return result.toArray(new Passageiro[0]);
    }

    // 2 Passageiros que chegaram em um determinado aeroporto (destino)
    public Passageiro[] passageirosQueChegaramEm(String codigoAeroporto) {
        List<Passageiro> result = new ArrayList<>();
        for (Ticket t : ticketDao.findAll()) {
            if (t == null || t.getVoo() == null) continue;
            Voo v = t.getVoo();
            if (codigoAeroporto.equalsIgnoreCase(v.getDestino())) {
                Passageiro p = t.getPassageiro();
                if (p != null) result.add(p);
            }
        }
        return result.toArray(new Passageiro[0]);
    }

    // 3 Valor arrecadado por companhia num periodo [inicio, fim] (data de voo)
    public double arrecadacaoPorCompanhia(CompanhiaAerea companhia, LocalDate inicio, LocalDate fim) {
        double total = 0.0;
        for (Ticket t : ticketDao.findAll()) {
            if (t == null || t.getVoo() == null) continue;
            Voo v = t.getVoo();
            if (v.getCompanhiaAerea() != null && v.getCompanhiaAerea().getId() == companhia.getId()) {
                LocalDate data = v.getData();
                if ((data.isEqual(inicio) || data.isAfter(inicio)) && (data.isEqual(fim) || data.isBefore(fim))) {
                    total += t.getValor();
                }
            }
        }
        return total;
    }
}