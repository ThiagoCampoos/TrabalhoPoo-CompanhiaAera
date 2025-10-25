package voo;

import comum.DaoBase;

public class VooAssentoDao extends DaoBase<VooAssento> {
    public VooAssentoDao() {
        super(10);
    }

    @Override
    public VooAssento[] createArray(int size) {
        return new VooAssento[size];
    }

    @Override
    public VooAssento cloneEntity(VooAssento entity) {
        return entity == null ? null : entity.clone();
    }

    public VooAssento findByCodigoAssento(String codigo) {
        if (codigo == null)
            return null;
        codigo = codigo.trim().toUpperCase();
        for (int i = 0; i < size; i++) {
            if (data[i] != null && codigo.equals(data[i].getCodigoAssento())) {
                return cloneEntity(data[i]);
            }
        }
        return null;
    }

    public VooAssento[] findByVoo(int idVoo) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && data[i].getvoo() != null && data[i].getvoo().getId() == idVoo) {
                count++;
            }
        }
        VooAssento[] result = new VooAssento[count];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && data[i].getvoo() != null && data[i].getvoo().getId() == idVoo) {
                result[idx++] = cloneEntity(data[i]);
            }
        }
        return result;

    }

    public VooAssento[] findByOcupado(boolean ocupado) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && data[i].getOcupado() == ocupado) {
                count++;
            }
        }
        VooAssento[] result = new VooAssento[count];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && data[i].getOcupado() == ocupado) {
                result[idx++] = cloneEntity(data[i]);
            }
        }
        return result;
    }
}
