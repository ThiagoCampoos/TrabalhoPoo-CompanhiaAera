package passageiro;

import comum.SystemClock;
import java.time.LocalDate;

public class PassageiroService {

    private final PassageiroDao dao;
    private final SystemClock clock;

    public PassageiroService(PassageiroDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Passageiro criar(String nome, LocalDate nascimento, String documento, String login, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio.");
        }
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento e obrigatorio.");
        }
        if (dao.findByDocumento(documento) != null) {
            throw new IllegalArgumentException("Documento ja cadastrado.");
        }
        if (login != null && !login.trim().isEmpty() && dao.findByLogin(login) != null) {
            throw new IllegalArgumentException("Login ja cadastrado.");
        }

        Passageiro p = new Passageiro();
        p.setNome(nome);
        p.setNascimento(nascimento);
        p.setDocumento(documento);
        p.setLogin(login);
        p.setSenha(senha);
        p.auditar(clock);
        return dao.create(p);
    }

    public Passageiro atualizar(int id, String nome, LocalDate nascimento, String documento, String login,
            String senha) {
        Passageiro existente = dao.findById(id);
        if (existente == null) {
            throw new IllegalArgumentException("Passageiro nao encontrado.");
        }

        if (documento != null && !documento.equals(existente.getDocumento())) {
            Passageiro outro = dao.findByDocumento(documento);
            if (outro != null && outro.getId() != id) {
                throw new IllegalArgumentException("Documento ja cadastrado para outro passageiro.");
            }
            existente.setDocumento(documento);
        }

        if (login != null && !login.equals(existente.getLogin())) {
            Passageiro outroLogin = dao.findByLogin(login);
            if (outroLogin != null && outroLogin.getId() != id) {
                throw new IllegalArgumentException("Login ja cadastrado para outro passageiro.");
            }
            existente.setLogin(login);
        }

        if (nome != null)
            existente.setNome(nome);
        if (nascimento != null)
            existente.setNascimento(nascimento);
        if (senha != null)
            existente.setSenha(senha);

        existente.auditar(clock);
        return dao.update(existente);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    public Passageiro buscarPorId(int id) {
        return dao.findById(id);
    }

    public Passageiro[] listarTodos() {
        return dao.findAll();
    }
}
