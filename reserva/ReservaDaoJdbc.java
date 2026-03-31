package reserva;

import comum.ConnectionFactory;
import comum.Repositorio;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDaoJdbc implements Repositorio<Reserva> {

    @Override
    public Reserva create(Reserva reserva) {
        String sql = """
                INSERT INTO reserva (codigo, sobrenome_passageiro, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, reserva.getCodigo());
            ps.setString(2, reserva.getSobrenomePassageiro());
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.setTimestamp(4, Timestamp.valueOf(agora));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) reserva.setId(rs.getInt(1));
            }
            reserva.setDataCriacao(agora);
            reserva.setDataModificacao(agora);
            salvarTickets(conn, reserva);
            return reserva;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar reserva", e);
        }
    }

    @Override
    public Reserva findById(int id) {
        String sql = "SELECT * FROM reserva WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reserva reserva = mapear(rs);
                reserva.setTicketIds(buscarTicketIds(conn, reserva.getId()));
                return reserva;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reserva por id", e);
        }
    }

    @Override
    public List<Reserva> findAll() {
        String sql = "SELECT * FROM reserva";
        List<Reserva> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reserva reserva = mapear(rs);
                reserva.setTicketIds(buscarTicketIds(conn, reserva.getId()));
                lista.add(reserva);
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar reservas", e);
        }
    }

    @Override
    public Reserva update(Reserva reserva) {
        String sql = """
                UPDATE reserva
                   SET codigo = ?, sobrenome_passageiro = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, reserva.getCodigo());
            ps.setString(2, reserva.getSobrenomePassageiro());
            ps.setTimestamp(3, Timestamp.valueOf(agora));
            ps.setInt(4, reserva.getId());
            ps.executeUpdate();

            reserva.setDataModificacao(agora);
            removerTickets(conn, reserva.getId());
            salvarTickets(conn, reserva);
            return reserva;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar reserva", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM reserva WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            removerTickets(conn, id);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir reserva", e);
        }
    }

    @Override
    public Reserva findByDocumento(String codigo) {
        String sql = "SELECT * FROM reserva WHERE codigo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reserva reserva = mapear(rs);
                reserva.setTicketIds(buscarTicketIds(conn, reserva.getId()));
                return reserva;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reserva por codigo", e);
        }
    }

    @Override
    public Reserva findByLogin(String sobrenome) {
        String sql = "SELECT * FROM reserva WHERE sobrenome_passageiro = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sobrenome);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reserva reserva = mapear(rs);
                reserva.setTicketIds(buscarTicketIds(conn, reserva.getId()));
                return reserva;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reserva por sobrenome", e);
        }
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        Reserva reserva = new Reserva(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("sobrenome_passageiro"),
                null);
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) reserva.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) reserva.setDataModificacao(modificacao.toLocalDateTime());
        return reserva;
    }

    private void salvarTickets(Connection conn, Reserva reserva) throws SQLException {
        if (reserva.getTicketIds() == null) return;
        String sql = "INSERT INTO reserva_ticket (reserva_id, ticket_id) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int ticketId : reserva.getTicketIds()) {
                ps.setInt(1, reserva.getId());
                ps.setInt(2, ticketId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void removerTickets(Connection conn, int reservaId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reserva_ticket WHERE reserva_id = ?")) {
            ps.setInt(1, reservaId);
            ps.executeUpdate();
        }
    }

    private int[] buscarTicketIds(Connection conn, int reservaId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ticket_id FROM reserva_ticket WHERE reserva_id = ?")) {
            ps.setInt(1, reservaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        }
        return ids.stream().mapToInt(Integer::intValue).toArray();
    }
}