package entrada;

import comum.SystemClock;

public class EntradaService {
    private final EntradaDao entradaDao;
    private final EntradaAviaoDao aviaoDao;
    private final SystemClock clock;

    public EntradaService(EntradaDao entradaDao, EntradaAviaoDao aviaoDao, SystemClock clock) {
        this.entradaDao = entradaDao;
        this.aviaoDao = aviaoDao;
        this.clock = clock;
    }

    public EntradaAeroporto registrarEntradaAeroporto(int ticketId, String area) {
        int id = entradaDao.nextId++;
        EntradaAeroporto e = new EntradaAeroporto(id, ticketId, area);
        e.auditar(clock);
        return entradaDao.create(e);
    }

    public EntradaAviao registrarEntradaAviao(int ticketId) {
        int id = aviaoDao.nextId++;
        EntradaAviao e = new EntradaAviao(id, ticketId);
        e.auditar(clock);
        return aviaoDao.create(e);
    }
}