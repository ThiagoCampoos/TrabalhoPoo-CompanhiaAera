package ticket;

public class ExtraBagDecorator extends AbstractTicketDecorator {
    private final double taxaPorBagagem;
    private final int quantidade;

    public ExtraBagDecorator(ITicketComponent inner, int quantidade, double taxaPorBagagem) {
        super(inner);
        this.quantidade = quantidade;
        this.taxaPorBagagem = taxaPorBagagem;
    }

    @Override
    public double getPrecoTotal() {
        return super.getPrecoTotal() + (quantidade * taxaPorBagagem);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " + ExtraBags(" + quantidade + "x" + taxaPorBagagem + ")";
    }
}
