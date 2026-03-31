package user;

import comum.Repositorio;
import comum.SystemClock;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

public class UserService {
    private final Repositorio<User> dao;
    private final SystemClock clock;

    public UserService(Repositorio<User> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public User criar(String nome, String login, String senha, Perfil perfil) {
        validar(nome, login, senha, perfil);
        if (dao.findByDocumento(login.trim().toLowerCase()) != null) {
            throw new IllegalArgumentException("Login ja utilizado.");
        }
        User user = new User();
        user.setNome(nome.trim());
        user.setLogin(login.trim().toLowerCase());
        user.setSenhaHash(hash(senha));
        user.setPerfil(perfil);
        user.auditar(clock);
        return dao.create(user);
    }

    public User autenticar(String login, String senha) {
        if (login == null || senha == null) throw new IllegalArgumentException("Credenciais invalidas.");
        User User = dao.findByDocumento(login.trim().toLowerCase());
        if (User == null || !User.getSenhaHash().equals(hash(senha))) {
            throw new IllegalArgumentException("Login ou senha incorretos.");
        }
        return User;
    }

    public void garantirAdminPadrao() {
        if (dao.findByDocumento("admin") == null) {
            criar("Administrador", "admin", "admin123", Perfil.ADMIN);
        }
    }

    public User alterarSenha(int id, String senhaAtual, String novaSenha) {
        User User = dao.findById(id);
        if (User == null) throw new IllegalArgumentException("User nao encontrado.");
        if (!User.getSenhaHash().equals(hash(senhaAtual))) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new IllegalArgumentException("Nova senha invalida.");
        }
        User.setSenhaHash(hash(novaSenha));
        User.auditar(clock);
        return dao.update(User);
    }

    public List<User> listarTodos() {
        return dao.findAll();
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    private void validar(String nome, String login, String senha, Perfil perfil) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome obrigatorio.");
        if (login == null || login.trim().isEmpty()) throw new IllegalArgumentException("Login obrigatorio.");
        if (senha == null || senha.length() < 6) throw new IllegalArgumentException("Senha deve ter ao menos 6 caracteres.");
        if (perfil == null) throw new IllegalArgumentException("Perfil obrigatorio.");
    }

    private String hash(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(senha.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash indisponivel.", e);
        }
    }
    
    public User garantirAdministradorInicial(String senhaPadrao) {
        final String loginAdmin = "admin";
        User existente = dao.findByDocumento(loginAdmin);
        if (existente != null) return existente;

        String senha = (senhaPadrao == null || senhaPadrao.isBlank())
                ? "admin123"
                : senhaPadrao;
        return criar("Administrador", loginAdmin, senha, Perfil.ADMIN);
    }
}