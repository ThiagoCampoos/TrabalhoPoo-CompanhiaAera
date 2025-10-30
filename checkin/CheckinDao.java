package checkin;

import comum.DaoBase;

public class CheckinDao extends DaoBase<Checkin> {

    public CheckinDao() {
        super(10);

    }

    @Override
    public Checkin[] createArray(int size) {
        return new Checkin[size];
    }

    @Override
    public Checkin cloneEntity(Checkin c) {
        if(c == null) return null;
        Checkin clone = new Checkin();
        clone.setId(c.getId());
        clone.setTicketId(c.getTicketId());
        clone.setAssento(c.getAssento());
        clone.setDataCriacao(c.getDataCriacao());
        clone.setDataModificacao(c.getDataModificacao());
        return clone;
    }

    public Checkin findByTicketId(int ticketId) {
        for (int i = 0; i < size; i++) {
            Checkin c = data[i];
            if (c != null && c.getTicketId() == ticketId) {
                return cloneEntity(c);
            }
        }
        return null;
    }
  public Checkin[] findByDocumento(String documento) {
        if (documento == null) return new Checkin[0];
        documento = documento.trim();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && data[i].getDocumento() != null && documento.equals(data[i].getDocumento())) {
                count++;
            }
        }
        Checkin[] result = createArray(count);
        int idx = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && data[i].getDocumento() != null && documento.equals(data[i].getDocumento())) {
                result[idx++] = cloneEntity(data[i]);
            }
        }
        return result;
    }
}


