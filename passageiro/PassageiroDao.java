package passageiro;

import comum.DaoBase;

public class PassageiroDao extends DaoBase<Passageiro> {

    public PassageiroDao() {
        super(16);
    }

    @Override
    public Passageiro[] createArray(int size) {
        return new Passageiro[size];
    }

    @Override
    public Passageiro cloneEntity(Passageiro p) {
        if (p == null) return null;
        Passageiro c = new Passageiro();
        c.setId(p.getId());
        c.setNome(p.getNome());
        c.setNascimento(p.getNascimento());
        c.setDocumento(p.getDocumento());
        c.setLogin(p.getLogin());
        c.setSenha(p.getSenha());
        c.setDataCriacao(p.getDataCriacao());
        c.setDataModificacao(p.getDataModificacao());
        return c;
    }

    public Passageiro findByDocumento(String documento) {
        for (int i = 0; i < size; i++) {
            Passageiro p = data[i];
            if (p != null && p.getDocumento() != null && p.getDocumento().equals(documento)) {
                return cloneEntity(p);
            }
        }
        return null;
    }

    public Passageiro findByLogin(String login) {
        for (int i = 0; i < size; i++) {
            Passageiro p = data[i];
            if (p != null && p.getLogin() != null && p.getLogin().equals(login)) {
                return cloneEntity(p);
            }
        }
        return null;
    }
}