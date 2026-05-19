package main;

import comum.Repositorio;
import comum.SystemClock;
import java.util.ArrayList;
import java.util.List;
import user.Perfil;
import user.User;
import user.UserService;

public class DemoInversaoDependencia {

    public static void main(String[] args) {
        Repositorio<User> repositorio = new RepositorioMemoriaUsuario();
        UserService service = new UserService(repositorio, SystemClock.getInstance());

        User criado = service.criar("Thiago Campoos", "thiago", "123456", Perfil.ADMIN);
        User autenticado = service.autenticar("thiago", "123456");

        System.out.println("=== Demo de Inversao de Dependencia ===");
        System.out.println("Criado: " + criado.getNome() + " | login: " + criado.getLogin());
        System.out.println("Autenticado: " + autenticado.getNome() + " | perfil: " + autenticado.getPerfil());
        System.out.println("Total de usuarios no repositorio: " + repositorio.findAll().size());
    }

    private static final class RepositorioMemoriaUsuario implements Repositorio<User> {
        private final List<User> dados = new ArrayList<>();
        private int proximoId = 1;

        @Override
        public User create(User entity) {
            User copia = clonar(entity);
            if (copia.getId() == 0) {
                copia.setId(proximoId++);
            }
            dados.add(copia);
            return clonar(copia);
        }

        @Override
        public User findById(int id) {
            for (User user : dados) {
                if (user.getId() == id) {
                    return clonar(user);
                }
            }
            return null;
        }

        @Override
        public List<User> findAll() {
            List<User> resultado = new ArrayList<>();
            for (User user : dados) {
                resultado.add(clonar(user));
            }
            return resultado;
        }

        @Override
        public User update(User entity) {
            if (entity == null) {
                return null;
            }
            for (int i = 0; i < dados.size(); i++) {
                if (dados.get(i).getId() == entity.getId()) {
                    dados.set(i, clonar(entity));
                    return clonar(entity);
                }
            }
            return null;
        }

        @Override
        public boolean deleteById(int id) {
            return dados.removeIf(user -> user.getId() == id);
        }

        @Override
        public User findByDocumento(String documento) {
            if (documento == null) {
                return null;
            }
            for (User user : dados) {
                if (user.getLogin() != null && user.getLogin().equalsIgnoreCase(documento)) {
                    return clonar(user);
                }
            }
            return null;
        }

        @Override
        public User findByLogin(String login) {
            return findByDocumento(login);
        }

        private User clonar(User original) {
            if (original == null) {
                return null;
            }
            User copia = new User();
            copia.setId(original.getId());
            copia.setNome(original.getNome());
            copia.setLogin(original.getLogin());
            copia.setSenhaHash(original.getSenhaHash());
            copia.setPerfil(original.getPerfil());
            copia.setDataCriacao(original.getDataCriacao());
            copia.setDataModificacao(original.getDataModificacao());
            return copia;
        }
    }
}