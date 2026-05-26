package ticket;

public class PromocaoDecorator extends AbstractTicketDecorator {
    private final double descontoPercent;

    public PromocaoDecorator(ITicketComponent inner, double descontoPercent) {
        super(inner);
        this.descontoPercent = descontoPercent;
    }

    @Override
    public double getPrecoTotal() {
        double base = super.getPrecoTotal();
        return base - (base * (descontoPercent / 100.0));
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " + Promocao(-" + descontoPercent + "%)";
    }
}
