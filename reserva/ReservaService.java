package reserva;

import comum.Repositorio;
import comum.SystemClock;
import java.util.Arrays;
import java.util.List;

public class ReservaService {
    private final Repositorio<Reserva> dao;
    private final SystemClock clock;

    public ReservaService(Repositorio<Reserva> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Reserva criar(String codigo, String sobrenome, int[] ticketIds) {
        validarCodigo(codigo);
        validarSobrenome(sobrenome);
        if (dao.findByDocumento(codigo.trim()) != null) {
            throw new IllegalArgumentException("Codigo da reserva ja utilizado.");
        }
        Reserva reserva = new Reserva(0, codigo.trim(), sobrenome.trim(), ticketIds);
        reserva.setCodigo(codigo.trim());
        reserva.setSobrenomePassageiro(sobrenome.trim());
        reserva.setTicketIds(ticketIds != null ? Arrays.copyOf(ticketIds, ticketIds.length) : new int[0]);
        reserva.auditar(clock);
        return dao.create(reserva);
    }

    public Reserva buscarPorCodigoESobrenome(String codigo, String sobrenome) {
        if (codigo == null || sobrenome == null) return null;
        Reserva reserva = dao.findByDocumento(codigo.trim());
        if (reserva != null && sobrenome.trim().equalsIgnoreCase(reserva.getSobrenomePassageiro())) {
            return reserva;
        }
        return dao.findByLogin(sobrenome.trim());
    }

    public Reserva buscarPorId(int id) {
        return dao.findById(id);
    }

    public Reserva[] listarTodos() {
        List<Reserva> todas = dao.findAll();
        return todas.toArray(new Reserva[0]);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public Reserva adicionarTicket(int reservaId, int ticketId) {
        Reserva reserva = dao.findById(reservaId);
        if (reserva == null) throw new IllegalArgumentException("Reserva nao encontrada.");
        int[] atuais = reserva.getTicketIds() != null ? reserva.getTicketIds() : new int[0];
        int[] atualizados = Arrays.copyOf(atuais, atuais.length + 1);
        atualizados[atuais.length] = ticketId;
        reserva.setTicketIds(atualizados);
        reserva.auditar(clock);
        return dao.update(reserva);
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Codigo da reserva invalido.");
        }
    }

    private void validarSobrenome(String sobrenome) {
        if (sobrenome == null || sobrenome.trim().isEmpty()) {
            throw new IllegalArgumentException("Sobrenome invalido.");
        }
    }
}