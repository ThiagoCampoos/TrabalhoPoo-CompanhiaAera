package reserva;

import comum.DaoBase;

public class ReservaDao extends DaoBase<Reserva> {
    public ReservaDao() {
        super(10);
    }

    @Override
    protected Reserva[] createArray(int size) {
        return new Reserva[size];
    }

    @Override
    protected Reserva cloneEntity(Reserva r) {
        if (r == null) return null;
        return new Reserva(r.getId(), r.getCodigo(), r.getSobrenomePassageiro(), r.getTicketIds());
    }
}