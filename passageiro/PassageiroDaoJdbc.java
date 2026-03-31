package passageiro;

import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PassageiroDaoJdbc implements Repositorio<Passageiro> {

    @Override
    public Passageiro create(Passageiro passageiro) {
        String sql = """
                INSERT INTO passageiro
                (nome, nascimento, documento, login, senha, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime agora = LocalDateTime.now();
            preencherStatement(ps, passageiro, agora);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    passageiro.setId(rs.getInt(1));
                }
            }
            passageiro.setDataCriacao(agora);
            passageiro.setDataModificacao(agora);
            return passageiro;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar passageiro", e);
        }
    }

    @Override
    public Passageiro findById(int id) {
        String sql = "SELECT * FROM passageiro WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar passageiro", e);
        }
    }

    @Override
    public List<Passageiro> findAll() {
        String sql = "SELECT * FROM passageiro";
        List<Passageiro> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar passageiros", e);
        }
    }

    @Override
    public Passageiro update(Passageiro passageiro) {
        String sql = """
                UPDATE passageiro
                SET nome=?, nascimento=?, documento=?, login=?, senha=?, data_modificacao=?
                WHERE id=?
                """;
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDateTime agora = LocalDateTime.now();
            ps.setString(1, passageiro.getNome());
            ps.setDate(2, Date.valueOf(passageiro.getNascimento()));
            ps.setString(3, passageiro.getDocumento());
            ps.setString(4, passageiro.getLogin());
            ps.setString(5, passageiro.getSenha());
            ps.setTimestamp(6, Timestamp.valueOf(agora));
            ps.setInt(7, passageiro.getId());

            ps.executeUpdate();
            passageiro.setDataModificacao(agora);
            return passageiro;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar passageiro", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM passageiro WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir passageiro", e);
        }
    }

    private void preencherStatement(PreparedStatement ps, Passageiro passageiro, LocalDateTime agora)
            throws SQLException {
        ps.setString(1, passageiro.getNome());
        ps.setDate(2, Date.valueOf(passageiro.getNascimento()));
        ps.setString(3, passageiro.getDocumento());
        ps.setString(4, passageiro.getLogin());
        ps.setString(5, passageiro.getSenha());
        ps.setTimestamp(6, Timestamp.valueOf(agora));
        ps.setTimestamp(7, Timestamp.valueOf(agora));
    }

    private Passageiro mapear(ResultSet rs) throws SQLException {
        Passageiro passageiro = new Passageiro(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getDate("nascimento").toLocalDate(),
                rs.getString("documento"),
                rs.getString("login"),
                rs.getString("senha"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) {
            passageiro.setDataCriacao(criacao.toLocalDateTime());
        }
        if (modificacao != null) {
            passageiro.setDataModificacao(modificacao.toLocalDateTime());
        }
        return passageiro;
    }

    @Override
    public Passageiro findByDocumento(String documento) {
        String sql = "SELECT * FROM passageiro WHERE documento = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar passageiro por documento", e);
        }
    }

    @Override
    public Passageiro findByLogin(String login) {
        String sql = "SELECT * FROM passageiro WHERE login = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar passageiro por login", e);
        }
    }
}