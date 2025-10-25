package passageiro;

public class PassageiroDao {

    private Passageiro[] data = new Passageiro[16];
    private int size = 0;
    private int nextId = 1;

    public Passageiro create(Passageiro passageiro) {
        if (passageiro.getId() == 0) {
            passageiro.setId(nextId++);
        }
        ensureCapacity(size + 1);
        data[size++] = cloneEntity(passageiro);
        return cloneEntity(passageiro);
    }

    public Passageiro update(Passageiro passageiro) {
        int idx = indexOfId(passageiro.getId());
        if (idx == -1) return null;
        data[idx] = cloneEntity(passageiro);
        return cloneEntity(passageiro);
    }

    public boolean deleteById(int id) {
        int idx = indexOfId(id);
        if (idx == -1) return false;
        for (int i = idx; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
        return true;
    }

    public Passageiro findById(int id) {
        int idx = indexOfId(id);
        if (idx == -1) return null;
        return cloneEntity(data[idx]);
    }

    public Passageiro[] findAll() {
        Passageiro[] result = new Passageiro[size];
        for (int i = 0; i < size; i++) {
            result[i] = cloneEntity(data[i]);
        }
        return result;
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

    private int indexOfId(int id) {
        for (int i = 0; i < size; i++) {
            Passageiro p = data[i];
            if (p != null && p.getId() == id) return i;
        }
        return -1;
    }

    private void ensureCapacity(int minCapacity) {
        if (data.length >= minCapacity) return;
        int newCap = data.length * 2;
        if (newCap < minCapacity) newCap = minCapacity;
        Passageiro[] novo = new Passageiro[newCap];
        System.arraycopy(data, 0, novo, 0, size);
        data = novo;
    }

    private Passageiro cloneEntity(Passageiro p) {
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
}