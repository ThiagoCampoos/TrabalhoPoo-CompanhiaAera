package ticket;

public abstract class AbstractTicketDecorator implements ITicketComponent {
    protected final ITicketComponent inner;

    protected AbstractTicketDecorator(ITicketComponent inner) {
        this.inner = inner;
    }

    @Override
    public double getPrecoTotal() {
        return inner.getPrecoTotal();
    }

    @Override
    public String getDescricao() {
        return inner.getDescricao();
    }
}
