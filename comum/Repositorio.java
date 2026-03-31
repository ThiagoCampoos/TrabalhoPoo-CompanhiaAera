package comum;

import java.util.List;

public interface Repositorio<T> {
    T create(T entity);

    T findById(int id);

    List<T> findAll();

    T update(T entity);

    boolean deleteById(int id);

    T findByDocumento(String documento);

    T findByLogin(String login);
}
