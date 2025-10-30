package despacho;

import comum.DaoBase;

public class DespachoDao extends DaoBase<Despacho> {
    public DespachoDao() {
        super(10);
    }

    @Override
    protected Despacho[] createArray(int size) {
        return new Despacho[size];
    }

    @Override
    protected Despacho cloneEntity(Despacho e) {
        if (e == null) return null;
        Despacho c = new Despacho(e.getId(), e.getTicketId(), e.getDocumento(), e.getPeso());
        return c;
    }
    
}