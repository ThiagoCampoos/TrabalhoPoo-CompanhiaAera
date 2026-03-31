package checkin;

import comum.Repositorio;
import comum.SystemClock;
import passageiro.Passageiro;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import ticket.Ticket;
import ticket.TicketService;
import voo.Voo;
import voo.VooService;

public class CheckinService {

    private final Repositorio<Checkin> dao;
    private final TicketService ticketService;
    private final VooService vooService;
    private final SystemClock clock;

    public CheckinService(Repositorio<Checkin> dao, TicketService ticketService,
                          VooService vooService, SystemClock clock) {
        this.dao = dao;
        this.ticketService = ticketService;
        this.vooService = vooService;
        this.clock = clock;
    }

    public Checkin criar(int ticketId, int passageiroIdConfirmacao, String assento) {
        Ticket ticket = ticketService.buscarPorId(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket nao encontrado.");
        }

        Passageiro passageiro = ticket.getPassageiro();
        if (passageiro == null) {
            throw new IllegalArgumentException("Ticket nao possui passageiro associado.");
        }
        if (passageiro.getId() != passageiroIdConfirmacao) {
            throw new IllegalArgumentException("Passageiro informado nao corresponde ao ticket.");
        }

        if (buscarPorTicketId(ticketId) != null) {
            throw new IllegalArgumentException("Check-in ja realizado para este ticket.");
        }

        Voo vooBase = ticket.getVoo();
        if (vooBase == null || vooBase.getId() <= 0) {
            throw new IllegalArgumentException("Ticket nao possui voo associado.");
        }
        Voo voo = vooService.buscarPorId(vooBase.getId());
        if (voo == null || voo.getData() == null) {
            throw new IllegalArgumentException("Voo associado ao ticket invalido.");
        }

        LocalTime horarioVoo = voo.getHorario() != null ? voo.getHorario() : LocalTime.MIDNIGHT;
        LocalDateTime partida = voo.getData().atTime(horarioVoo);
        LocalDateTime agora = clock.now();

        if (agora.isBefore(partida.minusHours(24))) {
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
        c.setDocumento(passageiro.getDocumento());
        c.setAssento(finalAssento);
        c.auditar(clock);

        return dao.create(c);
    }

    public Checkin buscarPorId(int id) {
        return dao.findById(id);
    }

    public Checkin[] listarTodos() {
        List<Checkin> todos = dao.findAll();
        return todos.toArray(new Checkin[0]);
    }

    public boolean excluir(int id) {
        return dao.deleteById(id);
    }

    public Checkin buscarPorTicketId(int ticketId) {
        if (dao instanceof CheckinDaoJdbc jdbc) {
            return jdbc.findByTicketId(ticketId);
        }
        return dao.findAll().stream()
                .filter(c -> c != null && c.getTicketId() == ticketId)
                .findFirst()
                .orElse(null);
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
