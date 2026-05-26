package ticket;

public class TaxaAeroportoDecorator extends AbstractTicketDecorator {
    private final double percentual; // ex: 5 para 5%

    public TaxaAeroportoDecorator(ITicketComponent inner, double percentual) {
        super(inner);
        this.percentual = percentual;
    }

    @Override
    public double getPrecoTotal() {
        double base = super.getPrecoTotal();
        return base + (base * (percentual / 100.0));
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " + TaxaAeroporto(" + percentual + "%)";
    }
}
