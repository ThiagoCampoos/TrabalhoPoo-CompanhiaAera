package entrada;

import comum.DaoBase;

public class EntradaAviaoDao extends DaoBase<EntradaAviao> {
    public EntradaAviaoDao() {
        super(10);
        
    }

    @Override
    protected EntradaAviao[] createArray(int size) {
        return new EntradaAviao[size];
    }

    @Override
    protected EntradaAviao cloneEntity(EntradaAviao e) {
        if (e == null)
            return null;
        return new EntradaAviao(e.getId(), e.getTicketId());
    }
}