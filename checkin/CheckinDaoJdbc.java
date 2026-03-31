package checkin;

import comum.ConnectionFactory;
import comum.Repositorio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckinDaoJdbc implements Repositorio<Checkin> {

    @Override
    public Checkin create(Checkin checkin) {
        String sql = """
                INSERT INTO checkin (ticket_id, documento, assento, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(ps, checkin, agora);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) checkin.setId(rs.getInt(1));
            }
            checkin.setDataCriacao(agora);
            checkin.setDataModificacao(agora);
            return checkin;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar check-in", e);
        }
    }

    @Override
    public Checkin findById(int id) {
        String sql = "SELECT * FROM checkin WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar check-in por id", e);
        }
    }

    @Override
    public List<Checkin> findAll() {
        String sql = "SELECT * FROM checkin";
        List<Checkin> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar check-ins", e);
        }
    }

    @Override
    public Checkin update(Checkin checkin) {
        String sql = """
                UPDATE checkin
                   SET ticket_id = ?, documento = ?, assento = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, checkin.getTicketId());
            ps.setString(2, checkin.getDocumento());
            ps.setString(3, checkin.getAssento());
            ps.setTimestamp(4, Timestamp.valueOf(agora));
            ps.setInt(5, checkin.getId());
            ps.executeUpdate();

            checkin.setDataModificacao(agora);
            return checkin;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar check-in", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM checkin WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir check-in", e);
        }
    }

    @Override
    public Checkin findByDocumento(String documento) {
        String sql = "SELECT * FROM checkin WHERE documento = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar check-in por documento", e);
        }
    }

    @Override
    public Checkin findByLogin(String login) {
        return null; // não se aplica
    }

    public Checkin findByTicketId(int ticketId) {
        String sql = "SELECT * FROM checkin WHERE ticket_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar check-in por ticket", e);
        }
    }

    private void preencherStatement(PreparedStatement ps, Checkin checkin, LocalDateTime agora) throws SQLException {
        ps.setInt(1, checkin.getTicketId());
        ps.setString(2, checkin.getDocumento());
        ps.setString(3, checkin.getAssento());
        ps.setTimestamp(4, Timestamp.valueOf(agora));
        ps.setTimestamp(5, Timestamp.valueOf(agora));
    }

    private Checkin mapear(ResultSet rs) throws SQLException {
        Checkin checkin = new Checkin(
                rs.getInt("id"),
                rs.getInt("ticket_id"),
                rs.getString("documento"),
                rs.getString("assento"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) checkin.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) checkin.setDataModificacao(modificacao.toLocalDateTime());
        return checkin;
    }
}