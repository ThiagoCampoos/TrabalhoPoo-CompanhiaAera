package voo;

public enum EstadoVoo {
    PROGRAMADO("Programado"),
    EMBARQUE("Embarque"),
    DECOLANDO("Decolando"),
    CANCELADO("Cancelado"),
    ATRASADO("Atrasado");

    private final String descricao;

    EstadoVoo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
