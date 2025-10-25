package comum;

import java.time.LocalDateTime;

public abstract class EntidadeComCodigo extends EntidadeNome {

    protected String codigo;

    public EntidadeComCodigo() {
        super();
    }

    public EntidadeComCodigo(int id, String nome, String codigo, LocalDateTime dataCriacao, LocalDateTime dataModificacao) {
        super(id, nome, dataCriacao, dataModificacao);
        this.codigo = codigo;
    }

    @Override
    public boolean validar() {
        return super.validar() && validarCodigo();
    }

    protected boolean validarCodigo() {
        return codigo != null &&
                !codigo.trim().isEmpty() &&
                codigo.matches(getPatternCodigo());
    }

    protected abstract String getPatternCodigo();

    protected String formatarCodigo() {
        if (codigo == null)
            return null;
        return codigo.trim().toUpperCase();
    }

    @Override
    public final void formatarCampos() {
        super.formatarCampos();
        if (codigo != null) {
            codigo = formatarCodigo();
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", codigo='" + codigo + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataModificacao=" + dataModificacao +
                '}';
    }
}