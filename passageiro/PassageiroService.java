package passageiro;

import comum.SystemClock;
import java.time.LocalDate;
import java.time.LocalDateTime;
public class PassageiroService {

    private final PassageiroDao dao;
    private final SystemClock clock;

    public PassageiroService(PassageiroDao dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Passageiro criar(String nome, LocalDate nascimento, String documento, String login, String senha) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento é obrigatório.");
        }
        if (dao.findByDocumento(documento) != null) {
            throw new IllegalArgumentException("Documento já cadastrado.");
        }
        if (login != null && !login.trim().isEmpty() && dao.findByLogin(login) != null) {
            throw new IllegalArgumentException("Login já cadastrado.");
        }

        LocalDateTime agora = clock.now();
        Passageiro p = new Passageiro(0, nome, nascimento, documento, login, senha, agora, agora);
        return dao.create(p);
    }

    public Passageiro atualizar(int id, String nome, LocalDate nascimento, String documento, String login,
            String senha) {
        Passageiro existente = dao.findById(id);
        if (existente == null) {
            throw new IllegalArgumentException("Passageiro não encontrado.");
        }

        if (documento != null && !documento.equals(existente.getDocumento())) {
            Passageiro outro = dao.findByDocumento(documento);
            if (outro != null && outro.getId() != id) {
                throw new IllegalArgumentException("Documento já cadastrado para outro passageiro.");
            }
            existente.setDocumento(documento);
        }

        if (login != null && !login.equals(existente.getLogin())) {
            Passageiro outroLogin = dao.findByLogin(login);
            if (outroLogin != null && outroLogin.getId() != id) {
                throw new IllegalArgumentException("Login já cadastrado para outro passageiro.");
            }
            existente.setLogin(login);
        }

        if (nome != null)
            existente.setNome(nome);
        if (nascimento != null)
            existente.setNascimento(nascimento);
        if (senha != null)
            existente.setSenha(senha);

        existente.setDataModificacao(clock.now());
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

