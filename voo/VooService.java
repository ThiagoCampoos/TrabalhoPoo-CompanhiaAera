package voo;

import companhia.CompanhiaAerea;
import comum.Repositorio;
import comum.SystemClock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

public class VooService {
    private final Repositorio<Voo> dao;
    private final SystemClock clock;

    public VooService(Repositorio<Voo> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Voo criar(int id, String origem, String destino, LocalDate data, LocalTime duracao,
            LocalTime horario, CompanhiaAerea companhiaAerea, int capacidade,
            EstadoVoo estado, String ida, String volta) {

        validarDadosObrigatorios(origem, destino, data, duracao, horario, companhiaAerea, capacidade, estado);

        Voo voo = new Voo();
        if (id > 0)
            voo.setId(id);
        voo.setOrigem(origem.trim().toUpperCase());
        voo.setDestino(destino.trim().toUpperCase());
        voo.setData(data);
        voo.setDuracao(duracao);
        voo.setHorario(horario);
        voo.setCompanhiaAerea(companhiaAerea);
        voo.setCapacidade(capacidade);
        voo.setEstado(estado);
        voo.setIda(ida != null ? ida.trim() : null);
        voo.setVolta(volta != null ? volta.trim() : null);
        voo.auditar(clock);
        return dao.create(voo);
    }

    public Voo atualizar(int id, String origem, String destino, LocalDate data, LocalTime duracao,
            LocalTime horario, CompanhiaAerea companhiaAerea, int capacidade,
            EstadoVoo estado, String ida, String volta) {

        Voo existente = dao.findById(id);
        if (existente == null) {
            throw new IllegalArgumentException("Voo nao encontrado para o ID: " + id);
        }

        if (origem != null && !origem.trim().isEmpty())
            existente.setOrigem(origem.trim().toUpperCase());
        if (destino != null && !destino.trim().isEmpty())
            existente.setDestino(destino.trim().toUpperCase());
        if (data != null)
            existente.setData(data);
        if (duracao != null)
            existente.setDuracao(duracao);
        if (horario != null)
            existente.setHorario(horario);
        if (companhiaAerea != null)
            existente.setCompanhiaAerea(companhiaAerea);
        if (capacidade > 0)
            existente.setCapacidade(capacidade);
        if (estado != null)
            existente.setEstado(estado);
        if (ida != null)
            existente.setIda(ida.trim());
        if (volta != null)
            existente.setVolta(volta.trim());

        existente.auditar(clock);
        return dao.update(existente);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public Voo buscarPorId(int id) {
        return dao.findById(id);
    }

    public Voo[] listarTodos() {
        List<Voo> todos = dao.findAll();
        return todos.toArray(new Voo[0]);
    }

    public Voo[] buscarPorOrigem(String origem) {
        if (origem == null || origem.trim().isEmpty())
            return new Voo[0];
        if (dao instanceof VooDao memDao) {
            return memDao.findByOrigem(origem.trim().toUpperCase());
        }
        if (dao instanceof VooDaoJdbc jdbc) {
            return jdbc.findByOrigem(origem.trim().toUpperCase()).toArray(new Voo[0]);
        }
        return filtrar(v -> origem.trim().equalsIgnoreCase(v.getOrigem()));
    }

    public Voo[] buscarPorDestino(String destino) {
        if (destino == null || destino.trim().isEmpty())
            return new Voo[0];
        if (dao instanceof VooDao memDao) {
            return memDao.findByDestino(destino.trim().toUpperCase());
        }
        if (dao instanceof VooDaoJdbc jdbc) {
            return jdbc.findByDestino(destino.trim().toUpperCase()).toArray(new Voo[0]);
        }
        return filtrar(v -> destino.trim().equalsIgnoreCase(v.getDestino()));
    }

    public Voo[] buscarPorData(LocalDate data) {
        if (data == null)
            return new Voo[0];
        if (dao instanceof VooDao memDao) {
            return memDao.findByData(data);
        }
        if (dao instanceof VooDaoJdbc jdbc) {
            return jdbc.findByData(data).toArray(new Voo[0]);
        }
        return filtrar(v -> data.equals(v.getData()));
    }

    public Voo[] buscarPorDataHorario(LocalDate data, LocalTime horario) {
        if (data == null || horario == null)
            return new Voo[0];
        if (dao instanceof VooDao memDao) {
            return memDao.findByDataHorario(data, horario);
        }
        return filtrar(v -> data.equals(v.getData()) && horario.equals(v.getHorario()));
    }

    public Voo[] buscarPorOrigemDestinoData(String origem, String destino, LocalDate data) {
        if (origem == null || destino == null || data == null)
            return new Voo[0];
        if (dao instanceof VooDao memDao) {
            return memDao.findByOrigemDestinoData(origem.trim().toUpperCase(), destino.trim().toUpperCase(), data);
        }
        if (dao instanceof VooDaoJdbc jdbc) {
            return jdbc.findByOrigemDestinoData(origem.trim().toUpperCase(),
                    destino.trim().toUpperCase(),
                    data).toArray(new Voo[0]);
        }
        return filtrar(v -> origem.trim().equalsIgnoreCase(v.getOrigem())
                && destino.trim().equalsIgnoreCase(v.getDestino())
                && data.equals(v.getData()));
    }

    public Voo[] buscarPorOrigemDestinoDataHorario(String origem, String destino,
            LocalDate data, LocalTime horario) {
        if (origem == null || destino == null || data == null || horario == null)
            return new Voo[0];
        if (dao instanceof VooDao memDao) {
            return memDao.findByOrigemDestinoDataHorario(origem.trim().toUpperCase(),
                    destino.trim().toUpperCase(),
                    data, horario);
        }
        if (dao instanceof VooDaoJdbc jdbc) {
            return jdbc.findByOrigemDestinoDataHorario(origem.trim().toUpperCase(),
                    destino.trim().toUpperCase(),
                    data, horario).toArray(new Voo[0]);
        }
        return filtrar(v -> origem.trim().equalsIgnoreCase(v.getOrigem())
                && destino.trim().equalsIgnoreCase(v.getDestino())
                && data.equals(v.getData())
                && horario.equals(v.getHorario()));
    }

    public Voo[] buscarPorIdaVolta(String ida, String volta) {
        if ((ida == null || ida.trim().isEmpty()) && (volta == null || volta.trim().isEmpty())) {
            return new Voo[0];
        }
        if (dao instanceof VooDao memDao) {
            return memDao.findByIdaVolta(ida, volta);
        }
        return filtrar(v -> (ida == null || ida.trim().equalsIgnoreCase(v.getIda()))
                && (volta == null || volta.trim().equalsIgnoreCase(v.getVolta())));
    }

    public void alterarEstado(int id, EstadoVoo novoEstado) {
        if (novoEstado == null) {
            throw new IllegalArgumentException("Estado do voo nao pode ser nulo.");
        }
        Voo voo = dao.findById(id);
        if (voo == null) {
            throw new IllegalArgumentException("Voo nao encontrado para o ID: " + id);
        }
        voo.setEstado(novoEstado);
        voo.auditar(clock);
        dao.update(voo);
    }

    private void validarDadosObrigatorios(String origem, String destino, LocalDate data, LocalTime duracao,
            LocalTime horario, CompanhiaAerea companhiaAerea,
            int capacidade, EstadoVoo estado) {

        if (origem == null || origem.trim().isEmpty()) {
            throw new IllegalArgumentException("Origem invalida.");
        }
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("Destino invalido.");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data do voo obrigatoria.");
        }
        if (duracao == null) {
            throw new IllegalArgumentException("Duracao do voo obrigatoria.");
        }
        if (horario == null) {
            throw new IllegalArgumentException("Horario do voo obrigatorio.");
        }
        if (companhiaAerea == null || companhiaAerea.getId() <= 0) {
            throw new IllegalArgumentException("Companhia aerea invalida.");

        }
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade do voo deve ser positiva.");
        }
        if (estado == null) {
            throw new IllegalArgumentException("Estado do voo obrigatorio.");
        }
    }

    private Voo[] filtrar(Predicate<Voo> predicate) {
        return dao.findAll()
                .stream()
                .filter(voo -> voo != null && predicate.test(voo))
                .toArray(Voo[]::new);
    }
}