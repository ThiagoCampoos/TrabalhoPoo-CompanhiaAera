package comum;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class EntidadeBase implements Cloneable {
    public  int id;
    public LocalDateTime dataCriacao;
    public LocalDateTime dataModificacao;

    public EntidadeBase() {
    }

    public EntidadeBase(int id, LocalDateTime dataCriacao, LocalDateTime dataModificacao) {
        this.id = id;
        this.dataCriacao = dataCriacao;
        this.dataModificacao = dataModificacao;
    }

    public final void auditar(SystemClock clock) {
        if(clock == null) {
            throw new IllegalArgumentException("Clock nulo nao e permitido");
        }
        LocalDateTime agora = clock.now();
        if (dataCriacao == null) dataCriacao = agora;
        dataModificacao = agora;
    }

    public abstract boolean validar();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataModificacao() {
        return dataModificacao;
    }

    public void setDataModificacao(LocalDateTime dataModificacao) {
        this.dataModificacao = dataModificacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EntidadeBase))
            return false;
        EntidadeBase that = (EntidadeBase) o;
        return id == that.id &&
                Objects.equals(dataCriacao, that.dataCriacao) &&
                Objects.equals(dataModificacao, that.dataModificacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", dataCriacao=" + dataCriacao +
                ", dataModificacao=" + dataModificacao +
                '}';
    }
}
