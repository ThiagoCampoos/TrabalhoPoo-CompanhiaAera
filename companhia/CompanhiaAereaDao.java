package companhia;

import comum.DaoBase;

public class CompanhiaAereaDao extends DaoBase<CompanhiaAerea> {
    
    public CompanhiaAereaDao(){
        super(10);
    }

    @Override
    public CompanhiaAerea[] createArray(int size) {
        return new CompanhiaAerea[size];
    }

    @Override
    public CompanhiaAerea cloneEntity(CompanhiaAerea entity) {
        if (entity == null) return null;
        CompanhiaAerea clone = new CompanhiaAerea();
        clone.setId(entity.getId());
        clone.setNome(entity.getNome());
        clone.setAbreviacao(entity.getAbreviacao());
        clone.setDataCriacao(entity.getDataCriacao());
        clone.setDataModificacao(entity.getDataModificacao());
        return clone;
    }
    public CompanhiaAerea findByAbreviacao(String abrev) {
        for (int i = 0; i < size; i++) {
            if (data[i] != null && abrev.equalsIgnoreCase(data[i].getAbreviacao())) {
                return cloneEntity(data[i]);
            }
        }
        return null;
    }
}
