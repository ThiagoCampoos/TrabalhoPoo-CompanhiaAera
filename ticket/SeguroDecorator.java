package ticket;

public class SeguroDecorator extends AbstractTicketDecorator {
    private final double taxaFixa;

    public SeguroDecorator(ITicketComponent inner, double taxaFixa) {
        super(inner);
        this.taxaFixa = taxaFixa;
    }

    @Override
    public double getPrecoTotal() {
        return super.getPrecoTotal() + taxaFixa;
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " + Seguro(" + taxaFixa + ")";
    }
}
