package entrada;

import comum.Repositorio;
import comum.SystemClock;
import java.util.List;

public class EntradaService {
    private final Repositorio<EntradaAeroporto> entradaDao;
    private final Repositorio<EntradaAviao> aviaoDao;
    private final SystemClock clock;

    public EntradaService(Repositorio<EntradaAeroporto> entradaDao,
            Repositorio<EntradaAviao> aviaoDao,
            SystemClock clock) {
        this.entradaDao = entradaDao;
        this.aviaoDao = aviaoDao;
        this.clock = clock;
    }

    public EntradaAeroporto registrarEntradaAeroporto(int ticketId, String area) {
        validarTicket(ticketId);
        if (area == null || area.trim().isEmpty()) {
            throw new IllegalArgumentException("Area de acesso invalida.");
        }
        EntradaAeroporto entrada = new EntradaAeroporto(ticketId, ticketId, area);
        entrada.setId(ticketId);
        entrada.auditar(clock);
        return entradaDao.create(entrada);
    }

    public EntradaAviao registrarEntradaAviao(int ticketId) {
        validarTicket(ticketId);
        EntradaAviao entrada = new EntradaAviao(ticketId, ticketId);
        entrada.setId(ticketId);
        entrada.auditar(clock);
        return aviaoDao.create(entrada);
    }

    public EntradaAeroporto buscarEntradaAeroporto(int id) {
        return entradaDao.findById(id);
    }

    public EntradaAviao buscarEntradaAviao(int id) {
        return aviaoDao.findById(id);
    }

    public EntradaAeroporto[] listarEntradasAeroporto() {
        List<EntradaAeroporto> todas = entradaDao.findAll();
        return todas.toArray(new EntradaAeroporto[0]);
    }

    public EntradaAviao[] listarEntradasAviao() {
        List<EntradaAviao> todas = aviaoDao.findAll();
        return todas.toArray(new EntradaAviao[0]);
    }

    public EntradaAeroporto buscarEntradaAeroportoPorTicket(int ticketId) {
        if (entradaDao instanceof EntradaDaoJdbc jdbc) {
            return jdbc.findByTicketId(ticketId);
        }
        return entradaDao.findAll().stream()
                .filter(e -> e != null && e.getTicketId() == ticketId)
                .findFirst()
                .orElse(null);
    }

    public EntradaAviao buscarEntradaAviaoPorTicket(int ticketId) {
        if (aviaoDao instanceof EntradaAviaoDaoJdbc jdbc) {
            return jdbc.findByTicketId(ticketId);
        }
        return aviaoDao.findAll().stream()
                .filter(e -> e != null && e.getTicketId() == ticketId)
                .findFirst()
                .orElse(null);
    }

    private void validarTicket(int ticketId) {
        if (ticketId <= 0) {
            throw new IllegalArgumentException("Ticket invalido.");
        }
    }
}