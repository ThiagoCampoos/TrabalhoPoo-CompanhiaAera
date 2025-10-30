package despacho;

import comum.EntidadeBase;

public class Despacho extends EntidadeBase {
    private int id;
    private int ticketId;
    private String documento;
    private double peso;

    public Despacho(int id, int ticketId, String documento, double peso) {
        this.id = id;
        this.ticketId = ticketId;
        this.documento = documento;
        this.peso = peso;
    }

    public int getId() { return id; }
    public int getTicketId() { return ticketId; }
    public String getDocumento() { return documento; }
    public double getPeso() { return peso; }

    public void setDocumento(String documento) { this.documento = documento; }
    public void setPeso(double peso) { this.peso = peso; }

    @Override
    public String toString() {
        return "DespachoBagagem{id=" + id + ", ticketId=" + ticketId + ", doc=" + documento + ", peso=" + peso + "}";
    }

    @Override
    public boolean validar() {
        if (documento == null || documento.trim().isEmpty()) {
            return false;
        }
        if (peso <= 0) {
            return false;
        }
        if (ticketId <= 0) {
            return false;
        }
        return true;
    }
}