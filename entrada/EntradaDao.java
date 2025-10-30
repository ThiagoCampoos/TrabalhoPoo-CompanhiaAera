package entrada;

import comum.DaoBase;

public class EntradaDao extends DaoBase<EntradaAeroporto> {
    public EntradaDao() {
        super(10);
    }

    @Override
    public EntradaAeroporto[] createArray(int size) {
        return new EntradaAeroporto[size];
    }

    @Override
    public EntradaAeroporto cloneEntity(EntradaAeroporto e) {
        if (e == null)
            return null;
        return new EntradaAeroporto(e.getId(), e.getTicketId(), e.getArea());
    }
}