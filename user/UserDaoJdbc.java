package user;

import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJdbc implements Repositorio<User> {

    @Override
    public User create(User user) {
        String sql = """
                INSERT INTO User (nome, login, senha_hash, perfil, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getNome());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getSenhaHash());
            ps.setString(4, user.getPerfil().name());
            ps.setTimestamp(5, Timestamp.valueOf(agora));
            ps.setTimestamp(6, Timestamp.valueOf(agora));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getInt(1));
            }
            user.setDataCriacao(agora);
            user.setDataModificacao(agora);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar User: " + e.getMessage(), e);
        }
    }

    @Override
    public User findById(int id) {
        return buscarUnico("SELECT * FROM User WHERE id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<User> findAll() {
        return buscarLista("SELECT * FROM User");
    }

    @Override
    public User update(User user) {
        String sql = """
                UPDATE User
                   SET nome = ?, login = ?, senha_hash = ?, perfil = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getNome());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getSenhaHash());
            ps.setString(4, user.getPerfil().name());
            ps.setTimestamp(5, Timestamp.valueOf(agora));
            ps.setInt(6, user.getId());
            ps.executeUpdate();

            user.setDataModificacao(agora);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar User: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM User WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir User: " + e.getMessage(), e);
        }
    }

    @Override
    public User findByDocumento(String login) {
        return buscarUnico("SELECT * FROM User WHERE login = ?", ps -> ps.setString(1, login));
    }

    @Override
    public User findByLogin(String ignored) {
        return null;
    }

    private User buscarUnico(String sql, SqlBinder binder) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar User: " + e.getMessage(), e);
        }
    }

    private List<User> buscarLista(String sql) {
        List<User> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar Users: " + e.getMessage(), e);
        }
    }

    private User mapear(ResultSet rs) throws SQLException {
        User User = new User();
        User.setId(rs.getInt("id"));
        User.setNome(rs.getString("nome"));
        User.setLogin(rs.getString("login"));
        User.setSenhaHash(rs.getString("senha_hash"));
        User.setPerfil(Perfil.valueOf(rs.getString("perfil")));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) User.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) User.setDataModificacao(modificacao.toLocalDateTime());
        return User;
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

}
