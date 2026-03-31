package user;

import comum.SystemClock;
import java.time.LocalDateTime;
import passageiro.Passageiro;
import passageiro.PassageiroDaoJdbc;

public class AuthService {
    private final UserService userService;
    private final PassageiroDaoJdbc passageiroDao;
    private final SystemClock clock;
    private SessaoUser sessaoAtual;

    public AuthService(UserService userService, PassageiroDaoJdbc passageiroDao, SystemClock clock) {
        this.userService = userService;
        this.passageiroDao = passageiroDao;
        this.clock = clock;
    }

    public SessaoUser autenticar(String login, String senha) {
        if (login == null || senha == null || login.isBlank()) {
            throw new IllegalArgumentException("Credenciais invalidas.");
        }

        User usuario = tentarAutenticarUsuario(login, senha);
        if (usuario != null) {
            return registrarSessao(usuario);
        }

        User cliente = tentarAutenticarPassageiro(login, senha);
        if (cliente != null) {
            return registrarSessao(cliente);
        }

        throw new IllegalArgumentException("Login ou senha incorretos.");
    }

    public void logout() {
        sessaoAtual = null;
    }

    public SessaoUser getSessaoAtual() {
        return sessaoAtual;
    }

    public void exigirPerfil(Perfil... perfisPermitidos) {
        if (sessaoAtual == null) {
            throw new IllegalStateException("Usuario nao autenticado.");
        }
        for (Perfil perfil : perfisPermitidos) {
            if (sessaoAtual.getUsuario().getPerfil() == perfil) {
                return;
            }
        }
        throw new IllegalArgumentException("Permissao negada para o perfil atual.");
    }

    private User tentarAutenticarUsuario(String login, String senha) {
        try {
            return userService.autenticar(login, senha);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private User tentarAutenticarPassageiro(String login, String senha) {
        Passageiro passageiro = passageiroDao.findByLogin(login.trim());
        if (passageiro == null || passageiro.getSenha() == null) {
            return null;
        }
        if (!passageiro.getSenha().equals(senha)) {
            return null;
        }
        User wrapper = new User();
        wrapper.setId(passageiro.getId());
        wrapper.setNome(passageiro.getNome());
        wrapper.setLogin(passageiro.getLogin());
        wrapper.setPerfil(Perfil.PASSAGEIRO);
        wrapper.setSenhaHash(""); // credenciais já validadas
        return wrapper;
    }

    private SessaoUser registrarSessao(User usuario) {
        LocalDateTime agora = clock.now();
        sessaoAtual = new SessaoUser(usuario, agora);
        return sessaoAtual;
    }
}