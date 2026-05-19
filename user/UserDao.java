package user;

import comum.DaoBase;

public class UserDao extends DaoBase<User> {

    public UserDao() {
        super(10);
    }

    @Override
    public User[] createArray(int size) {
        return new User[size];
    }

    @Override
    public User cloneEntity(User user) {
        if (user == null)
            return null;
        User clone = new User();
        clone.setId(user.getId());
        clone.setNome(user.getNome());
        clone.setLogin(user.getLogin());
        clone.setSenhaHash(user.getSenhaHash());
        clone.setPerfil(user.getPerfil());
        clone.setDataCriacao(user.getDataCriacao());
        clone.setDataModificacao(user.getDataModificacao());
        return clone;
    }

    @Override
    public User findByLogin(String login) {
        for (int i = 0; i < size; i++) {
            User u = data[i];
            if (u != null && u.getLogin() != null && u.getLogin().equals(login)) {
                return cloneEntity(u);
            }
        }
        return null;
    }

    @Override
    public User findByDocumento(String documento) {
        // Use documento field to store login for compatibility with UserService
        return findByLogin(documento);
    }
}
