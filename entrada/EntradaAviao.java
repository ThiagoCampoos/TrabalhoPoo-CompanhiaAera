package entrada;

import comum.EntidadeBase;

public class EntradaAviao extends EntidadeBase {
    public int id;
    public int ticketId;

    public EntradaAviao(int id, int ticketId) {
        this.id = id;
        this.ticketId = ticketId;
    }

    public int getId() {
        return id;
    }

    public int getTicketId() {
        return ticketId;
    }

    @Override
    public boolean validar() {
        return id > 0 && ticketId > 0;
    }

    @Override
    public String toString() {
        return "EntradaAviao{id=" + id + ", ticketId=" + ticketId + "}";
    }
}