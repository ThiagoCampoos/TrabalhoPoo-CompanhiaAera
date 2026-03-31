package companhia;

import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompanhiaAereaDaojdbc implements Repositorio<CompanhiaAerea> {

    @Override
    public CompanhiaAerea create(CompanhiaAerea companhia) {
        String sql = """
                INSERT INTO companhia_aerea (nome, abreviacao, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, companhia.getNome());
            ps.setString(2, companhia.getAbreviacao());
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.setTimestamp(4, Timestamp.valueOf(agora));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    companhia.setId(rs.getInt(1));
                }
            }
            companhia.setDataCriacao(agora);
            companhia.setDataModificacao(agora);
            return companhia;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar companhia aerea", e);
        }
    }

    @Override
    public CompanhiaAerea findById(int id) {
        String sql = "SELECT * FROM companhia_aerea WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar companhia aerea", e);
        }
    }

    @Override
    public List<CompanhiaAerea> findAll() {
        String sql = "SELECT * FROM companhia_aerea";
        List<CompanhiaAerea> companhias = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                companhias.add(mapear(rs));
            }
            return companhias;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar companhias aereas", e);
        }
    }

    @Override
    public CompanhiaAerea update(CompanhiaAerea companhia) {
        String sql = """
                UPDATE companhia_aerea
                   SET nome = ?, abreviacao = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, companhia.getNome());
            ps.setString(2, companhia.getAbreviacao());
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.setInt(4, companhia.getId());
            ps.executeUpdate();

            companhia.setDataModificacao(agora);
            return companhia;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar companhia aerea", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM companhia_aerea WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir companhia aerea", e);
        }
    }

    @Override
    public CompanhiaAerea findByDocumento(String abreviacao) {
        String sql = "SELECT * FROM companhia_aerea WHERE abreviacao = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, abreviacao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar companhia aerea por abreviacao", e);
        }
    }

    @Override
    public CompanhiaAerea findByLogin(String nome) {
        String sql = "SELECT * FROM companhia_aerea WHERE nome = ?";
        try (Connection conn = ConnectionFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar companhia aerea por nome", e);
        }
    }

    private CompanhiaAerea mapear(ResultSet rs) throws SQLException {
        CompanhiaAerea companhia = new CompanhiaAerea(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("abreviacao"), null, null);
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) {
            companhia.setDataCriacao(criacao.toLocalDateTime());
        }
        if (modificacao != null) {
            companhia.setDataModificacao(modificacao.toLocalDateTime());
        }
        return companhia;
    }
}