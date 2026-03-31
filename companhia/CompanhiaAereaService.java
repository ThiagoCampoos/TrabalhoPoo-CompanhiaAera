package companhia;

import comum.Repositorio;
import comum.SystemClock;
import java.util.List;

public class CompanhiaAereaService {
    private final Repositorio<CompanhiaAerea> dao;
    private final SystemClock clock;

    public CompanhiaAereaService(Repositorio<CompanhiaAerea> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public CompanhiaAerea criar(String nome, String abreviacao) {
        validarCamposObrigatorios(nome, abreviacao);
        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setNome(nome.trim());
        companhia.setAbreviacao(abreviacao.trim().toUpperCase());
        companhia.auditar(clock);
        return dao.create(companhia);
    }

    public CompanhiaAerea atualizar(int id, String nome, String abreviacao) {
        CompanhiaAerea existente = dao.findById(id);
        if (existente == null) {
            throw new IllegalArgumentException("Companhia aérea não encontrada para o ID: " + id);
        }
        if (nome != null && !nome.trim().isEmpty()) {
            existente.setNome(nome.trim());
        }
        if (abreviacao != null && !abreviacao.trim().isEmpty()) {
            existente.setAbreviacao(abreviacao.trim().toUpperCase());
        }
        existente.auditar(clock);
        return dao.update(existente);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public CompanhiaAerea buscarPorId(int id) {
        return dao.findById(id);
    }

    public CompanhiaAerea[] listarTodos() {
        List<CompanhiaAerea> todas = dao.findAll();
        return todas.toArray(new CompanhiaAerea[0]);
    }

    public CompanhiaAerea buscarPorAbreviacao(String abreviacao) {
        if (abreviacao == null || abreviacao.trim().isEmpty())
            return null;
        String codigo = abreviacao.trim().toUpperCase();

        if (dao instanceof CompanhiaAereaDao memDao) {
            return memDao.findByAbreviacao(codigo);
        }
        if (dao instanceof CompanhiaAereaDaojdbc jdbcDao) {
            return jdbcDao.findByDocumento(codigo);
        }
        return dao.findAll().stream()
                .filter(c -> c != null && codigo.equalsIgnoreCase(c.getAbreviacao()))
                .findFirst()
                .orElse(null);
    }

    private void validarCamposObrigatorios(String nome, String abreviacao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da companhia não pode ser vazio.");
        }
        if (abreviacao == null || abreviacao.trim().length() < 2) {
            throw new IllegalArgumentException("Abreviação deve possuir ao menos 2 caracteres.");
        }
    }
}