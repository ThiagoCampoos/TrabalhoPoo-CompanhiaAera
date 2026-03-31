package user;

import java.time.LocalDateTime;

public class SessaoUser {
    private final User usuario;
    private final LocalDateTime InicioLogin;

    public SessaoUser(User usuario, LocalDateTime InicioLogin) {
        this.usuario = usuario;
        this.InicioLogin = InicioLogin;
    }

    public User getUsuario() {
        return usuario;
    }

    public LocalDateTime getInicioLogin() {
        return InicioLogin;
    }

    public boolean possuiPerfil(Perfil perfil) {
        return usuario != null && usuario.getPerfil() == perfil;
    }
}
