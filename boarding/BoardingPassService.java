package boarding;

import comum.SystemClock;

public class BoardingPassService {
    private final BoardingPassDao dao;
    private final SystemClock clock;

    public BoardingPassService(BoardingPassDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public BoardingPass criarParaTicket(int ticketId, String conteudo) {
        int id = dao.nextId++;
        BoardingPass b = new BoardingPass(id, ticketId, conteudo);
        b.auditar(clock);
        return dao.create(b);
    }

    public BoardingPass buscarPorTicketId(int ticketId) {
        for (BoardingPass b : dao.findAll()) {
            if (b != null && b.getTicketId() == ticketId)
                return b;
        }
        return null;
    }

    public BoardingPass[] listarTodos() {
        return dao.findAll();
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }
}