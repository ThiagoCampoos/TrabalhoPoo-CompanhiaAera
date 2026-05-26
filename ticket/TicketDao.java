package ticket;

import comum.DaoBase;

public class TicketDao extends DaoBase<Ticket> {

    public TicketDao() {
        super(10);
    }

    @Override
    public Ticket[] createArray(int size) {
        return new Ticket[size];
    }

    @Override
    public Ticket cloneEntity(Ticket entity) {
        if (entity == null)
            return null;
        Ticket copia = new Ticket(
                entity.getId(),
                entity.getValor(),
                entity.getVoo(),
                entity.getPassageiro(),
                entity.getCodigo(), entity.getAssento());
        copia.setPrecoTotal(entity.getPrecoTotal());
        copia.setDescricaoExtras(entity.getDescricaoExtras());
        copia.setSeguro(entity.isSeguro());
        copia.setExtraBagagens(entity.getExtraBagagens());
        copia.setPromocaoPercent(entity.getPromocaoPercent());
        copia.setDataCriacao(entity.getDataCriacao());
        copia.setDataModificacao(entity.getDataModificacao());
        return copia;
    }

    public Ticket findByCodigo(String codigo) {
        for (int i = 0; i < size; i++) {
            if (data[i] != null && codigo.equals(data[i].getCodigo())) {
                return cloneEntity(data[i]);
            }
        }
        return null;
    }
}
