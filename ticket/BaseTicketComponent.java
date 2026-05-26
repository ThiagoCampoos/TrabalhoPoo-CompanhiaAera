package ticket;

public class BaseTicketComponent implements ITicketComponent {
    private final Ticket ticket;

    public BaseTicketComponent(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public double getPrecoTotal() {
        return ticket.getValor();
    }

    @Override
    public String getDescricao() {
        return "Base";
    }
}
