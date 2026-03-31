package entrada;

import comum.ConnectionFactory;
import comum.Repositorio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntradaAviaoDaoJdbc implements Repositorio<EntradaAviao> {

    @Override
    public EntradaAviao create(EntradaAviao entradaAviao) {
        String sql = """
                INSERT INTO entrada_aviao (ticket_id, data_criacao, data_modificacao)
                VALUES (?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entradaAviao.getTicketId());
            ps.setTimestamp(2, Timestamp.valueOf(agora));
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entradaAviao.setId(rs.getInt(1));
            }
            entradaAviao.setDataCriacao(agora);
            entradaAviao.setDataModificacao(agora);
            return entradaAviao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar entrada de avião", e);
        }
    }

    @Override
    public EntradaAviao findById(int id) {
        String sql = "SELECT * FROM entrada_aviao WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar entrada de avião por id", e);
        }
    }

    @Override
    public List<EntradaAviao> findAll() {
        String sql = "SELECT * FROM entrada_aviao";
        List<EntradaAviao> entradas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) entradas.add(mapear(rs));
            return entradas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar entradas de avião", e);
        }
    }

    @Override
    public EntradaAviao update(EntradaAviao entradaAviao) {
        String sql = """
                UPDATE entrada_aviao
                   SET ticket_id = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entradaAviao.getTicketId());
            ps.setTimestamp(2, Timestamp.valueOf(agora));
            ps.setInt(3, entradaAviao.getId());
            ps.executeUpdate();

            entradaAviao.setDataModificacao(agora);
            return entradaAviao;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar entrada de avião", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM entrada_aviao WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir entrada de avião", e);
        }
    }

    @Override
    public EntradaAviao findByDocumento(String documento) {
        return null; // não aplicável
    }

    @Override
    public EntradaAviao findByLogin(String login) {
        return null; // não aplicável
    }

    public EntradaAviao findByTicketId(int ticketId) {
        String sql = "SELECT * FROM entrada_aviao WHERE ticket_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar entrada de avião por ticket", e);
        }
    }

    private EntradaAviao mapear(ResultSet rs) throws SQLException {
        EntradaAviao entrada = new EntradaAviao(
                rs.getInt("id"),
                rs.getInt("ticket_id"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) entrada.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) entrada.setDataModificacao(modificacao.toLocalDateTime());
        return entrada;
    }
}