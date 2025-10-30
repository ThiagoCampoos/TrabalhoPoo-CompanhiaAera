package reserva;

import comum.EntidadeBase;

public class Reserva extends EntidadeBase {
    private int id;
    private String codigo; 
    private String sobrenomePassageiro;
    private int[] ticketIds; 

    public Reserva(int id, String codigo, String sobrenomePassageiro, int[] ticketIds) {
        this.id = id;
        this.codigo = codigo;
        this.sobrenomePassageiro = sobrenomePassageiro;
        this.ticketIds = ticketIds == null ? new int[0] : ticketIds.clone();
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getSobrenomePassageiro() { return sobrenomePassageiro; }
    public int[] getTicketIds() { return ticketIds.clone(); }

    public void setTicketIds(int[] ticketIds) { this.ticketIds = ticketIds == null ? new int[0] : ticketIds.clone(); }

    @Override
    public String toString() {
        return "Reserva{id=" + id + ", codigo='" + codigo + "', sobrenome='" + sobrenomePassageiro + "'}";
    }

    @Override
    public boolean validar() {
        return codigo != null && !codigo.trim().isEmpty() && sobrenomePassageiro != null && !sobrenomePassageiro.trim().isEmpty();
    }
}