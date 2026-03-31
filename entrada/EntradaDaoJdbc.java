package entrada;

import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntradaDaoJdbc implements Repositorio<EntradaAeroporto> {

    @Override
    public EntradaAeroporto create(EntradaAeroporto entrada) {
        String sql = """
                INSERT INTO entrada_aeroporto (ticket_id, area, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entrada.getTicketId());
            ps.setString(2, entrada.getArea());
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.setTimestamp(4, Timestamp.valueOf(agora));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entrada.setId(rs.getInt(1));
            }
            entrada.setDataCriacao(agora);
            entrada.setDataModificacao(agora);
            return entrada;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar entrada no aeroporto", e);
        }
    }

    @Override
    public EntradaAeroporto findById(int id) {
        String sql = "SELECT * FROM entrada_aeroporto WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar entrada no aeroporto", e);
        }
    }

    @Override
    public List<EntradaAeroporto> findAll() {
        String sql = "SELECT * FROM entrada_aeroporto";
        List<EntradaAeroporto> entradas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) entradas.add(mapear(rs));
            return entradas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar entradas no aeroporto", e);
        }
    }

    @Override
    public EntradaAeroporto update(EntradaAeroporto entrada) {
        String sql = """
                UPDATE entrada_aeroporto
                   SET ticket_id = ?, area = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, entrada.getTicketId());
            ps.setString(2, entrada.getArea());
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.setInt(4, entrada.getId());
            ps.executeUpdate();

            entrada.setDataModificacao(agora);
            return entrada;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar entrada no aeroporto", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM entrada_aeroporto WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir entrada no aeroporto", e);
        }
    }

    @Override
    public EntradaAeroporto findByDocumento(String documento) {
        return null;
    }

    @Override
    public EntradaAeroporto findByLogin(String login) {
        return null;
    }

    public EntradaAeroporto findByTicketId(int ticketId) {
        String sql = "SELECT * FROM entrada_aeroporto WHERE ticket_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar entrada por ticket", e);
        }
    }

    private EntradaAeroporto mapear(ResultSet rs) throws SQLException {
        EntradaAeroporto entrada = new EntradaAeroporto(
                rs.getInt("id"),
                rs.getInt("ticket_id"),
                rs.getString("area"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) entrada.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) entrada.setDataModificacao(modificacao.toLocalDateTime());
        return entrada;
    }
}