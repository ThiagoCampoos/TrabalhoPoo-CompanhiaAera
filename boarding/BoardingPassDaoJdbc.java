package boarding;

import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BoardingPassDaoJdbc implements Repositorio<BoardingPass> {

    @Override
    public BoardingPass create(BoardingPass bp) {
        String sql = """
                INSERT INTO boarding_pass (ticket_id, conteudo, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(ps, bp, agora);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bp.setId(rs.getInt(1));
                }
            }
            bp.setDataCriacao(agora);
            bp.setDataModificacao(agora);
            return bp;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar boarding pass", e);
        }
    }

    @Override
    public BoardingPass findById(int id) {
        String sql = "SELECT * FROM boarding_pass WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar boarding pass por id", e);
        }
    }

    @Override
    public List<BoardingPass> findAll() {
        String sql = "SELECT * FROM boarding_pass";
        List<BoardingPass> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar boarding passes", e);
        }
    }

    @Override
    public BoardingPass update(BoardingPass bp) {
        String sql = """
                UPDATE boarding_pass
                   SET ticket_id = ?, conteudo = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bp.getTicketId());
            ps.setString(2, bp.getConteudo());
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.setInt(4, bp.getId());
            ps.executeUpdate();

            bp.setDataModificacao(agora);
            return bp;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar boarding pass", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM boarding_pass WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir boarding pass", e);
        }
    }

    @Override
    public BoardingPass findByDocumento(String conteudo) {
        String sql = "SELECT * FROM boarding_pass WHERE conteudo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, conteudo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar boarding pass por conteudo", e);
        }
    }

    @Override
    public BoardingPass findByLogin(String login) {
        return null; // não aplicável
    }

    public BoardingPass findByTicketId(int ticketId) {
        String sql = "SELECT * FROM boarding_pass WHERE ticket_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar boarding pass por ticket", e);
        }
    }

    private void preencherStatement(PreparedStatement ps, BoardingPass bp, LocalDateTime agora) throws SQLException {
        ps.setInt(1, bp.getTicketId());
        ps.setString(2, bp.getConteudo());
        ps.setTimestamp(3, Timestamp.valueOf(agora));
        ps.setTimestamp(4, Timestamp.valueOf(agora));
    }

    private BoardingPass mapear(ResultSet rs) throws SQLException {
        BoardingPass bp = new BoardingPass(
                rs.getInt("id"),
                rs.getInt("ticket_id"),
                rs.getString("conteudo"));
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) {
            bp.setDataCriacao(criacao.toLocalDateTime());
        }
        if (modificacao != null) {
            bp.setDataModificacao(modificacao.toLocalDateTime());
        }
        return bp;
    }
}