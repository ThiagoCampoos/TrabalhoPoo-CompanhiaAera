package voo;

import comum.ConnectionFactory;
import comum.Repositorio;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VooAssentoDaoJdbc implements Repositorio<VooAssento> {

    @Override
    public VooAssento create(VooAssento assento) {
        String sql = """
                INSERT INTO voo_assento (voo_id, codigo_assento, ocupado, data_criacao, data_modificacao)
                VALUES (?, ?, ?, ?, ?)
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(ps, assento, agora);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) assento.setId(rs.getInt(1));
            }
            assento.setDataCriacao(agora);
            assento.setDataModificacao(agora);
            return assento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar assento de voo", e);
        }
    }

    @Override
    public VooAssento findById(int id) {
        return buscarUnico("SELECT * FROM voo_assento WHERE id = ?", ps -> ps.setInt(1, id));
    }

    @Override
    public List<VooAssento> findAll() {
        return buscarLista("SELECT * FROM voo_assento");
    }

    @Override
    public VooAssento update(VooAssento assento) {
        String sql = """
                UPDATE voo_assento
                   SET voo_id = ?, codigo_assento = ?, ocupado = ?, data_modificacao = ?
                 WHERE id = ?
                """;
        LocalDateTime agora = LocalDateTime.now();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assento.getvoo().getId());
            ps.setString(2, assento.getCodigoAssento());
            ps.setBoolean(3, assento.getOcupado());
            ps.setTimestamp(4, Timestamp.valueOf(agora));
            ps.setInt(5, assento.getId());
            ps.executeUpdate();

            assento.setDataModificacao(agora);
            return assento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar assento de voo", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        String sql = "DELETE FROM voo_assento WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir assento de voo", e);
        }
    }

    @Override
    public VooAssento findByDocumento(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return null;
        return buscarUnico("SELECT * FROM voo_assento WHERE codigo_assento = ?",
                ps -> ps.setString(1, codigo.trim().toUpperCase()));
    }

    @Override
    public VooAssento findByLogin(String login) {
        return null;
    }

    public VooAssento findByCodigoAssento(String codigo) {
        return findByDocumento(codigo);
    }

    public List<VooAssento> findByVoo(int vooId) {
        return buscarLista("SELECT * FROM voo_assento WHERE voo_id = ?", ps -> ps.setInt(1, vooId));
    }

    public List<VooAssento> findByOcupado(boolean ocupado) {
        return buscarLista("SELECT * FROM voo_assento WHERE ocupado = ?", ps -> ps.setBoolean(1, ocupado));
    }

    private void preencherStatement(PreparedStatement ps, VooAssento assento, LocalDateTime agora) throws SQLException {
        ps.setInt(1, assento.getvoo().getId());
        ps.setString(2, assento.getCodigoAssento());
        ps.setBoolean(3, assento.getOcupado());
        ps.setTimestamp(4, Timestamp.valueOf(agora));
        ps.setTimestamp(5, Timestamp.valueOf(agora));
    }

    private VooAssento buscarUnico(String sql, SqlBinder binder) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar assento de voo", e);
        }
    }

    private List<VooAssento> buscarLista(String sql) {
        return buscarLista(sql, null);
    }

    private List<VooAssento> buscarLista(String sql, SqlBinder binder) {
        List<VooAssento> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (binder != null) binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar assentos de voo", e);
        }
    }

    private VooAssento mapear(ResultSet rs) throws SQLException {
        VooAssento assento = new VooAssento(0, null, null, null, null, null, false);
        assento.setId(rs.getInt("id"));
        assento.setCodigoAssento(rs.getString("codigo_assento"));
        assento.setOcupado(rs.getBoolean("ocupado"));

        Voo voo = new Voo();
        voo.setId(rs.getInt("voo_id"));
        assento.setVoo(voo);

        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp modificacao = rs.getTimestamp("data_modificacao");
        if (criacao != null) assento.setDataCriacao(criacao.toLocalDateTime());
        if (modificacao != null) assento.setDataModificacao(modificacao.toLocalDateTime());
        return assento;
    }

    @FunctionalInterface
    private interface SqlBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}