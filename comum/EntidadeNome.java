package comum;

import java.time.LocalDateTime;

public abstract class EntidadeNome  extends EntidadeBase
{
    public String nome;

    public EntidadeNome() 
    {
        super();
    }
    public EntidadeNome(int id, String nome, LocalDateTime dataCriacao, LocalDateTime dataModificacao)
    {
        super(id, dataCriacao, dataModificacao);
        this.nome = nome;
    }

    @Override
    public boolean validar ()
    {
        return validarNome();
    }
    private boolean validarNome()
    {
        return nome != null && !nome.trim().isEmpty() && nome.trim().length() >= 2;
    }
    private String formatarNome ()
    {
        if(nome == null) return null;
        return nome.trim().toUpperCase();
    }
    public void formatarCampos() 
    {
        if(nome!=null){
            nome = formatarNome();
        }
    }
    
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + getId() +
                ", nome='" + nome + '\'' +
                ", dataCriacao=" + getDataCriacao() +
                ", dataModificacao=" + getDataModificacao() +
                '}';
    }
}
