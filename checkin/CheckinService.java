package checkin;

import comum.SystemClock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import ticket.Ticket;
import ticket.TicketService;
import voo.Voo;

public class CheckinService {

    private final CheckinDao dao;
    private final TicketService ticketService;
    private final SystemClock clock;

    public CheckinService(CheckinDao dao, TicketService ticketService, SystemClock clock) {
        this.dao = dao;
        this.ticketService = ticketService;
        this.clock = clock;
    }

    public Checkin criar(int ticketId, String documento, String assento) {

        Ticket ticket = ticketService.buscarPorId(ticketId);

        if (ticket == null) {
            throw new IllegalArgumentException("Ticket nao possui passageiro associado.o");
        }
        if (documento == null || !documento.equals(ticket.getPassageiro().getDocumento())) {
            throw new IllegalArgumentException("Documento informado nao corresponde ao passageiro do ticket.");
        }
        if (dao.findByTicketId(ticketId) != null) {
            throw new IllegalArgumentException("Check-in ja realizado para este ticket.");
        }
        Voo voo = ticket.getVoo();
        if (voo == null || voo.getData() == null) {
            throw new IllegalArgumentException("Voo associado ao ticket invalido.");
        }
        LocalTime horarioVoo = voo.getHorario() != null ? voo.getHorario() : LocalTime.MIDNIGHT;
        LocalDateTime partida = voo.getData().atTime(horarioVoo);

        LocalDateTime agora = clock.now();
        LocalDateTime inicioJanela = partida.minusHours(24);
        if (agora.isBefore(inicioJanela)) {
            throw new IllegalArgumentException("Checkin so pode ser realizado a partir de 24 horas antes do voo.");
        }
        int vendidos = 0;
        Ticket[] todos = ticketService.listarTodos();
        if (todos != null) {
            for (Ticket t : todos) {
                if (t != null && t.getVoo() != null && voo.getId() == t.getVoo().getId()) {
                    vendidos++;
                }
            }
        }
        if (voo.getCapacidade() > 0 && vendidos >= voo.getCapacidade()) {
            throw new IllegalArgumentException("Voo lotado. Nao ha assentos disponiveis.");
        }

        String finalAssento = (assento == null || assento.trim().isEmpty()) ? "Livre" : assento.trim();

        Checkin c = new Checkin();
        c.setTicketId(ticketId);
        c.setDocumento(documento.trim());
        c.setAssento(finalAssento);
        c.auditar(clock);

        return dao.create(c);
    }

    public Checkin buscarPorId(int id) {
        return dao.findById(id);
    }

    public Checkin[] listarTodos() {
        return dao.findAll();
    }

    public boolean excluir(int id) {
        return dao.deleteById(id);
    }

    public Checkin buscarPorTicketId(int ticketId) {
        Checkin[] todos = listarTodos();
        if (todos == null)
            return null;
        for (Checkin c : todos) {
            if (c == null)
                continue;
            if (c.getTicketId() == ticketId) {
                return c;
            }
        }
        return null;
    }

    public String gerarBoardingPass(Checkin checkin) {
        if (checkin == null)
            return null;
        Ticket ticket = ticketService.buscarPorId(checkin.getTicketId());
        if (ticket == null)
            return null;
        Voo voo = ticket.getVoo();

        String nome = ticket.getPassageiro() != null ? ticket.getPassageiro().getNome() : "N/A";
        String documento = checkin.getDocumento() != null ? checkin.getDocumento() : "N/A";
        int ticketId = ticket.getId();
        String vooId = voo != null ? String.valueOf(voo.getId()) : "N/A";
        String rota = voo != null ? voo.getOrigem() + " -> " + voo.getDestino() : "N/A";
        String partida = voo != null ? (voo.getData() + " " + (voo.getHorario() != null ? voo.getHorario() : "00:00"))
                : "N/A";
        String assento = checkin.getAssento() != null ? checkin.getAssento() : "N/A";
        String emitido = checkin.getDataCriacao() != null ? checkin.getDataCriacao().toString() : "N/A";

        return String.format(
                "----- BOARDING PASS -----%n" +
                        "Nome: %s%n" +
                        "Documento: %s%n" +
                        "Ticket ID: %d%n" +
                        "Voo ID: %s%n" +
                        "Origem -> Destino: %s%n" +
                        "Partida: %s%n" +
                        "Assento: %s%n" +
                        "Emitido em: %s%n" +
                        "-------------------------\n",
                nome, documento, ticketId, vooId, rota, partida, assento, emitido);
    }
}
