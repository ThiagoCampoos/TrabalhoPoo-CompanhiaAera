package ticket;

import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import passageiro.Passageiro;
import voo.Voo;

public class TicketDaoJdbc implements Repositorio<Ticket> {

    @Override
    public Ticket create(Ticket ticket) {
        String sql = """
                INSERT INTO ticket (valor, voo_id, passageiro_id, codigo, assento, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(ps, ticket, agora);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                }
            }
            ticket.setDataCriacao(agora);
            ticket.setDataModificacao(agora);
            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar ticket", e);
        }
    }

    @Override
    public Ticket findById(int id) {
        String sql = "SELECT * FROM ticket WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ticket por id", e);
        }
    }

    @Override
    public List<Ticket> findAll() {
        String sql = "SELECT * FROM ticket";
        List<Ticket> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar tickets", e);
        }
    }

    @Override
    public Ticket update(Ticket ticket) {
        String sql = """
                UPDATE ticket
                   SET valor = ?, voo_id = ?, passageiro_id = ?, codigo = ?, assento = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, ticket.getValor());
            ps.setInt(2, ticket.getVoo().getId());
            ps.setInt(3, ticket.getPassageiro().getId());
            ps.setString(4, ticket.getCodigo());
            ps.setString(5, ticket.getAssento());
            ps.setTimestamp(6, Timestamp.valueOf(agora));
            ps.setInt(7, ticket.getId());

            ps.executeUpdate();
            ticket.setDataModificacao(agora);
            return ticket;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ticket", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM ticket WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir ticket", e);
        }
    }

    @Override
    public Ticket findByDocumento(String codigo) {
        String sql = "SELECT * FROM ticket WHERE codigo = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ticket por codigo", e);
        }
    }

    @Override
    public Ticket findByLogin(String login) {
        return null; // nao aplicavel para ticket
    }

    private void preencherStatement(PreparedStatement ps, Ticket ticket, LocalDateTime agora) throws SQLException {
        ps.setDouble(1, ticket.getValor());
        ps.setInt(2, ticket.getVoo().getId());
        ps.setInt(3, ticket.getPassageiro().getId());
        ps.setString(4, ticket.getCodigo());
        ps.setString(5, ticket.getAssento());
        ps.setTimestamp(6, Timestamp.valueOf(agora));
        ps.setTimestamp(7, Timestamp.valueOf(agora));
    }

    private Ticket mapear(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setValor(rs.getDouble("valor"));
        ticket.setCodigo(rs.getString("codigo"));
        ticket.setAssento(rs.getString("assento"));

        int passageiroId = rs.getInt("passageiro_id");
        if (!rs.wasNull()) {
            Passageiro passageiro = new Passageiro();
            passageiro.setId(passageiroId);
            ticket.setPassageiro(passageiro);
        }

        int vooId = rs.getInt("voo_id");
        if (!rs.wasNull()) {
            Voo voo = new Voo();
            voo.setId(vooId);
            ticket.setVoo(voo);
        }

        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) ticket.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) ticket.setDataModificacao(modificacao.toLocalDateTime());

        return ticket;
    }
}