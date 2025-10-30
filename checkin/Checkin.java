package checkin;

import comum.EntidadeBase;
import java.util.Objects;

public class Checkin extends EntidadeBase {

    private int ticketId;
    private String documento;
    private String assento;

    public Checkin() {
    }

    public Checkin(int id, int ticketId, String documento, String assento) {
        super(id, null, null);
        this.ticketId = ticketId;
        this.documento = documento;
        this.assento = assento;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getAssento() {
        return assento;
    }

    public void setAssento(String assento) {
        this.assento = assento;
    }

    @Override
    public boolean validar() {
        return ticketId > 0 && documento != null && !documento.trim().isEmpty()
                && assento != null && !assento.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Checkin{" +
                "id=" + id +
                ", ticketId=" + ticketId +
                ", documento='" + documento + '\'' +
                ", assento='" + assento + '\'' +
                '}';
    }
    @Override
    public Object clone() {
        return new Checkin(getId(), ticketId, documento, assento);
    }
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof Checkin)) return false;
        Checkin checkin = (Checkin) o;
        return ticketId == checkin.ticketId &&
                id == checkin.id &&
                documento.equals(checkin.documento) &&
                assento.equals(checkin.assento);
    }
    @Override
    public int hashCode(){
        return Objects.hash(getId());
    }
}
