package boarding;

import comum.EntidadeBase;

public class BoardingPass extends EntidadeBase {
    private int id;
    private int ticketId;
    private String conteudo; // texto pronto para impressão/visualizacao

    public BoardingPass(int id, int ticketId, String conteudo) {
        this.id = id;
        this.ticketId = ticketId;
        this.conteudo = conteudo;
    }

    public int getId() {
        return id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String c) {
        this.conteudo = c;
    }

    @Override
    public String toString() {
        return "BoardingPass{id=" + id + ", ticketId=" + ticketId + "}";
    }

    public boolean validar() {
        return id > 0 && ticketId > 0 && conteudo != null && !conteudo.trim().isEmpty();
    }
} 