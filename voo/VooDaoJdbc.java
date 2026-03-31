package voo;

import companhia.CompanhiaAerea;
import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VooDaoJdbc implements Repositorio<Voo> {

    @Override
    public Voo create(Voo voo) {
        String sql = """
                INSERT INTO voo (origem, destino, data, horario, duracao, capacidade, estado,
                                 ida, volta, companhia_id, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(ps, voo, agora);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) voo.setId(rs.getInt(1));
            }
            voo.setDataCriacao(agora);
            voo.setDataModificacao(agora);
            return voo;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar voo"+ e.getMessage(), e);
        }
    }

    @Override
    public Voo findById(int id) {
        String sql = "SELECT * FROM voo WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar voo por id", e);
        }
    }

    @Override
    public List<Voo> findAll() {
        String sql = "SELECT * FROM voo";
        List<Voo> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar voos", e);
        }
    }

    @Override
    public Voo update(Voo voo) {
        String sql = """
                UPDATE voo SET origem=?, destino=?, data=?, horario=?, duracao=?, capacidade=?,
                               estado=?, ida=?, volta=?, companhia_id=?, data_modificacao=?
                 WHERE id=?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voo.getOrigem());
            ps.setString(2, voo.getDestino());
            ps.setDate(3, Date.valueOf(voo.getData()));
            ps.setTime(4, Time.valueOf(voo.getHorario()));
            ps.setTime(5, Time.valueOf(voo.getDuracao()));
            ps.setInt(6, voo.getCapacidade());
            ps.setString(7, voo.getEstado().name());
            ps.setString(8, voo.getIda());
            ps.setString(9, voo.getVolta());
            ps.setInt(10, voo.getCompanhiaAerea().getId());
            ps.setTimestamp(11, Timestamp.valueOf(agora));
            ps.setInt(12, voo.getId());

            ps.executeUpdate();
            voo.setDataModificacao(agora);
            return voo;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar voo", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM voo WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir voo", e);
        }
    }

    @Override
    public Voo findByDocumento(String ignored) {
        return null;
    }

    @Override
    public Voo findByLogin(String ignored) {
        return null;
    }

    public List<Voo> findByOrigem(String origem) {
        return buscarLista("SELECT * FROM voo WHERE origem = ?", origem);
    }

    public List<Voo> findByDestino(String destino) {
        return buscarLista("SELECT * FROM voo WHERE destino = ?", destino);
    }

    public List<Voo> findByData(LocalDate data) {
        return buscarLista("SELECT * FROM voo WHERE data = ?", Date.valueOf(data));
    }

    public List<Voo> findByOrigemDestinoData(String origem, String destino, LocalDate data) {
        String sql = "SELECT * FROM voo WHERE origem = ? AND destino = ? AND data = ?";
        return buscarLista(sql, origem, destino, Date.valueOf(data));
    }

    public List<Voo> findByOrigemDestinoDataHorario(String origem, String destino, LocalDate data, LocalTime horario) {
        String sql = "SELECT * FROM voo WHERE origem=? AND destino=? AND data=?"
                + (horario != null ? " AND horario=?" : "");
        return horario == null
                ? buscarLista(sql, origem, destino, Date.valueOf(data))
                : buscarLista(sql, origem, destino, Date.valueOf(data), Time.valueOf(horario));
    }

    private void preencherStatement(PreparedStatement ps, Voo voo, LocalDateTime agora) throws SQLException {
        ps.setString(1, voo.getOrigem());
        ps.setString(2, voo.getDestino());
        ps.setDate(3, Date.valueOf(voo.getData()));
        ps.setTime(4, Time.valueOf(voo.getHorario()));
        ps.setTime(5, Time.valueOf(voo.getDuracao()));
        ps.setInt(6, voo.getCapacidade());
        ps.setString(7, voo.getEstado().name());
        ps.setString(8, voo.getIda());
        ps.setString(9, voo.getVolta());
        ps.setInt(10, voo.getCompanhiaAerea().getId());
        ps.setTimestamp(11, Timestamp.valueOf(agora));
        ps.setTimestamp(12, Timestamp.valueOf(agora));
    }

    private Voo mapear(ResultSet rs) throws SQLException {
        CompanhiaAerea companhia = new CompanhiaAerea();
        companhia.setId(rs.getInt("companhia_id"));

        Voo voo = new Voo();
        voo.setId(rs.getInt("id"));
        voo.setOrigem(rs.getString("origem"));
        voo.setDestino(rs.getString("destino"));
        voo.setData(rs.getDate("data").toLocalDate());
        voo.setHorario(rs.getTime("horario").toLocalTime());
        voo.setDuracao(rs.getTime("duracao").toLocalTime());
        voo.setCapacidade(rs.getInt("capacidade"));
        voo.setEstado(EstadoVoo.valueOf(rs.getString("estado")));
        voo.setIda(rs.getString("ida"));
        voo.setVolta(rs.getString("volta"));
        voo.setCompanhiaAerea(companhia);

        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) voo.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) voo.setDataModificacao(modificacao.toLocalDateTime());
        return voo;
    }

    private List<Voo> buscarLista(String sql, Object... params) {
        List<Voo> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                Object p = params[i];
                if (p instanceof Date d) {
                    ps.setDate(i + 1, d);
                } else if (p instanceof Time t) {
                    ps.setTime(i + 1, t);
                } else if (p instanceof Integer n) {
                    ps.setInt(i + 1, n);
                } else {
                    ps.setString(i + 1, (String) p);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar busca de voos", e);
        }
    }
}