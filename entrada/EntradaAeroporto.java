package entrada;

import comum.EntidadeBase;

public class EntradaAeroporto extends EntidadeBase {
    private int id;
    private int ticketId;
    private String area; // área reservada

    public EntradaAeroporto(int id, int ticketId, String area) {
        this.id = id;
        this.ticketId = ticketId;
        this.area = area;
    }

    public int getId() {
        return id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getArea() {
        return area;
    }

    @Override
    public String toString() {
        return "EntradaAeroporto{id=" + id + ", ticketId=" + ticketId + ", area=" + area + "}";
    }

    @Override
    public boolean validar() {
        return id > 0 && ticketId > 0 && area != null && !area.trim().isEmpty();
    }
}