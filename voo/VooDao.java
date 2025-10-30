package voo;

import comum.DaoBase;
import java.time.LocalDate;
import java.time.LocalTime;

public class VooDao extends DaoBase<Voo> {

    public VooDao() {
        super(10);

    }

    @Override
    public Voo[] createArray(int size) {
        return new Voo[size];
    }

    @Override
    public Voo cloneEntity(Voo entity) {
        return entity == null ? null : (Voo) entity.clone();
    }

    public Voo[] findByOrigem(String origem) {
        if (origem == null)
            return new Voo[0];
        origem = origem.trim().toUpperCase();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && origem.equals(data[i].getOrigem())) {
                count++;
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && origem.equals(data[i].getOrigem())) {
                result[index++] = data[i];
            }
        }
        return result;
    }

    public Voo[] findByDestino(String destino) {
        if (destino == null)
            return new Voo[0];
        destino = destino.trim().toUpperCase();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && destino.equals(data[i].getDestino())) {
                count++;
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && destino.equals(data[i].getDestino())) {
                result[index++] = data[i];
            }
        }
        return result;

    }

    public Voo[] findByData(LocalDate dataBusca) {
        if (dataBusca == null)
            return new Voo[0];
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && dataBusca.equals(data[i].getData())) {
                count++;
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && dataBusca.equals(data[i].getData())) {
                result[index++] = data[i];
            }
        }
        return result;
    }

    public Voo[] findByOrigemDestinoData(String origem, String destino, LocalDate dataBusca) {
        if (origem == null || destino == null || dataBusca == null)
            return new Voo[0];
        origem = origem.trim().toUpperCase();
        destino = destino.trim().toUpperCase();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && origem.equals(data[i].getOrigem()) &&
                    destino.equals(data[i].getDestino()) &&
                    dataBusca.equals(data[i].getData())) {
                count++;
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && origem.equals(data[i].getOrigem()) &&
                    destino.equals(data[i].getDestino()) &&
                    dataBusca.equals(data[i].getData())) {
                result[index++] = data[i];
            }
        }
        return result;
    }

    public Voo[] findByIdaVolta(String ida, String volta) {
        if (ida == null || volta == null)
            return new Voo[0];
        ida = ida.trim().toUpperCase();
        volta = volta.trim().toUpperCase();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && ida.equals(data[i].ida) &&
                    volta.equals(data[i].volta)) {
                count++;
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && ida.equals(data[i].ida) &&
                    volta.equals(data[i].volta)) {
                result[index++] = data[i];
            }
        }
        return result;
    }

    public Voo[] findByVoosPorOrigem(String origem) {
        if (origem == null)
            return new Voo[0];
        origem = origem.trim().toUpperCase();
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && origem.equals(data[i].getOrigem())) {
                count++;
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && origem.equals(data[i].getOrigem())) {
                result[index++] = data[i];
            }
        }
        return result;
    }

    public Voo[] findByDataHorario(LocalDate dataBusca, LocalTime horario) {
        if (dataBusca == null)
            return new Voo[0];
        if (horario == null)
            return findByData(dataBusca);

        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && dataBusca.equals(data[i].getData())
                    && horario.equals(data[i].getHorario())) {
                count++;
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] != null && dataBusca.equals(data[i].getData())
                    && horario.equals(data[i].getHorario())) {
                result[index++] = data[i];
            }
        }
        return result;
    }

    public Voo[] findByOrigemDestinoDataHorario(String origem, String destino, LocalDate dataBusca, LocalTime horario) {
        if (origem == null || destino == null || dataBusca == null)
            return new Voo[0];
        origem = origem.trim().toUpperCase();
        destino = destino.trim().toUpperCase();

        int count = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] == null)
                continue;
            boolean match = origem.equals(data[i].getOrigem())
                    && destino.equals(data[i].getDestino())
                    && dataBusca.equals(data[i].getData());
            if (match) {
                if (horario == null) {
                    count++;
                } else if (horario.equals(data[i].getHorario())) {
                    count++;
                }
            }
        }
        Voo[] result = createArray(count);
        int index = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] == null)
                continue;
            boolean match = origem.equals(data[i].getOrigem())
                    && destino.equals(data[i].getDestino())
                    && dataBusca.equals(data[i].getData());
            if (match) {
                if (horario == null) {
                    result[index++] = data[i];
                } else if (horario.equals(data[i].getHorario())) {
                    result[index++] = data[i];
                }
            }
        }
        return result;
    }
}
