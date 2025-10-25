package comum;

public abstract class DaoBase<T extends EntidadeBase> {

    public T[] data;
    public int size = 0;
    public int nextId = 1;

    public DaoBase(int initialCapacity) {
        this.data = createArray(initialCapacity);
    }

    protected abstract T[] createArray(int size);

    protected abstract T cloneEntity(T entity);

    public final T create(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entidade não pode ser nula");
        }

        if (entity instanceof EntidadeNome) {
            ((EntidadeNome) entity).formatarCampos();
        }
        return doCreate(entity);
    }

    protected T doCreate(T entity) {
        if (entity.getId() == 0) {
            entity.setId(nextId++);
        }
        ensureCapacity(size + 1);
        data[size++] = cloneEntity(entity);
        return cloneEntity(entity);
    }

    public T update(T entity) {
        if (entity == null)
            return null;

        if (entity instanceof EntidadeNome) {
            ((EntidadeNome) entity).formatarCampos();
        }

        int idx = indexOfId(entity.getId());
        if (idx == -1)
            return null;
        data[idx] = cloneEntity(entity);
        return cloneEntity(entity);
    }

    public boolean deleteById(int id) {
        int idx = indexOfId(id);
        if (idx == -1)
            return false;

        for (int i = idx; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null;
        size--;
        return true;
    }

    public T findById(int id) {
        int idx = indexOfId(id);
        if (idx == -1)
            return null;
        return cloneEntity(data[idx]);
    }

    public T[] findAll() {
        T[] result = createArray(size);
        for (int i = 0; i < size; i++) {
            result[i] = cloneEntity(data[i]);
        }
        return result;
    }

    public int count() {
        return size;
    }

    protected int indexOfId(int id) {
        for (int i = 0; i < size; i++) {
            T entity = data[i];
            if (entity != null && entity.getId() == id)
                return i;
        }
        return -1;
    }

    protected void ensureCapacity(int minCapacity) {
        if (data.length >= minCapacity)
            return;

        int newCap = data.length * 2;
        if (newCap < minCapacity)
            newCap = minCapacity;

        T[] novo = createArray(newCap);
        System.arraycopy(data, 0, novo, 0, size);
        data = novo;
    }
}