package despacho;

import comum.ConnectionFactory;
import comum.Repositorio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DespachoDaoJdbc implements Repositorio<Despacho> {

    @Override
    public Despacho create(Despacho despacho) {
        String sql = """
                INSERT INTO despacho (ticket_id, documento, peso, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(ps, despacho, agora);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) despacho.setId(rs.getInt(1));
            }
            despacho.setDataCriacao(agora);
            despacho.setDataModificacao(agora);
            return despacho;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar despacho", e);
        }
    }

    @Override
    public Despacho findById(int id) {
        return consultarUnico("SELECT * FROM despacho WHERE id = ?", id);
    }

    @Override
    public List<Despacho> findAll() {
        String sql = "SELECT * FROM despacho";
        List<Despacho> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar despachos", e);
        }
    }

    @Override
    public Despacho update(Despacho despacho) {
        String sql = """
                UPDATE despacho
                   SET ticket_id = ?, documento = ?, peso = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, despacho.getTicketId());
            ps.setString(2, despacho.getDocumento());
            ps.setDouble(3, despacho.getPeso());
            ps.setTimestamp(4, Timestamp.valueOf(agora));
            ps.setInt(5, despacho.getId());
            ps.executeUpdate();

            despacho.setDataModificacao(agora);
            return despacho;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar despacho", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM despacho WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir despacho", e);
        }
    }

    @Override
    public Despacho findByDocumento(String documento) {
        return documento == null ? null
                : consultarUnico("SELECT * FROM despacho WHERE documento = ?", documento.trim());
    }

    @Override
    public Despacho findByLogin(String login) {
        return null;
    }

    public Despacho findByTicketId(int ticketId) {
        return consultarUnico("SELECT * FROM despacho WHERE ticket_id = ?", ticketId);
    }

    private void preencherStatement(PreparedStatement ps, Despacho despacho, LocalDateTime agora) throws SQLException {
        ps.setInt(1, despacho.getTicketId());
        ps.setString(2, despacho.getDocumento());
        ps.setDouble(3, despacho.getPeso());
        ps.setTimestamp(4, Timestamp.valueOf(agora));
        ps.setTimestamp(5, Timestamp.valueOf(agora));
    }

    private Despacho consultarUnico(String sql, Object param) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (param instanceof Integer i) {
                ps.setInt(1, i);
            } else {
                ps.setString(1, param.toString());
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar despacho", e);
        }
    }

    private Despacho mapear(ResultSet rs) throws SQLException {
        Despacho despacho = new Despacho(
                rs.getInt("id"),
                rs.getInt("ticket_id"),
                rs.getString("documento"),
                rs.getDouble("peso"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) despacho.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) despacho.setDataModificacao(modificacao.toLocalDateTime());
        return despacho;
    }
}