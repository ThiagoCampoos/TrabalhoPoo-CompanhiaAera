package ticket;

import comum.Repositorio;
import comum.SystemClock;
import java.time.LocalDateTime;
import java.util.List;
import passageiro.Passageiro;
import voo.Voo;

public class TicketService {
    private final Repositorio<Ticket> dao;
    private final SystemClock clock;

    public TicketService(Repositorio<Ticket> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public Ticket criar(double valor, Voo voo, Passageiro passageiro,
            LocalDateTime dataCriacao, LocalDateTime dataModificacao, String assento) {
        return criar(valor, voo, passageiro, dataCriacao, dataModificacao, assento, false, 0, 0.0);
    }

    public Ticket criar(double valor, Voo voo, Passageiro passageiro,
            LocalDateTime dataCriacao, LocalDateTime dataModificacao, String assento,
            boolean seguro, int extraBagagens, double promocaoPercent) {
        if (passageiro == null)
            throw new IllegalArgumentException("Ticket nao possui passageiro associado.");
        if (voo == null)
            throw new IllegalArgumentException("Ticket nao possui voo associado.");
        if (valor < 0)
            throw new IllegalArgumentException("Valor do ticket invalido.");
        if (extraBagagens < 0)
            throw new IllegalArgumentException("Quantidade de bagagens extra invalida.");
        if (promocaoPercent < 0 || promocaoPercent > 100)
            throw new IllegalArgumentException("Percentual de promocao invalido.");

        Ticket ticket = new Ticket();
        if (dao instanceof TicketDao memDao) {
            ticket.setId(memDao.nextId++);
        }
        ticket.setValor(valor);
        ticket.setVoo(voo);
        ticket.setPassageiro(passageiro);
        ticket.setCodigo(gerarCodigo(voo));
        ticket.setAssento(assento != null ? assento.trim() : null);
        ticket.setSeguro(seguro);
        ticket.setExtraBagagens(extraBagagens);
        ticket.setPromocaoPercent(promocaoPercent);

        // Calcular preco total usando pattern Decorator.
        double precoTotal = calcularPrecoComDecorators(ticket);
        ticket.setPrecoTotal(precoTotal);
        ticket.setDescricaoExtras(ticket.getDescricaoExtras() != null ? ticket.getDescricaoExtras() : "");
        ticket.auditar(clock);
        if (dataCriacao != null)
            ticket.setDataCriacao(dataCriacao);
        if (dataModificacao != null)
            ticket.setDataModificacao(dataModificacao);
        return dao.create(ticket);
    }

    public double calcularPrecoComDecorators(Ticket ticket) {
        ITicketComponent comp = new BaseTicketComponent(ticket);
        // aplicar taxa de aeroporto padrão (ex: 5%) - decisão: aplicada sempre
        comp = new TaxaAeroportoDecorator(comp, 5.0);

        // seguro opcional
        if (ticket.isSeguro()) {
            comp = new SeguroDecorator(comp, 15.0); // taxa fixa exemplo
        }

        // bagagens extras
        if (ticket.getExtraBagagens() > 0) {
            comp = new ExtraBagDecorator(comp, ticket.getExtraBagagens(), 40.0);
        }

        // promocao (percentual)
        if (ticket.getPromocaoPercent() > 0) {
            comp = new PromocaoDecorator(comp, ticket.getPromocaoPercent());
        }

        // popular descricao
        ticket.setDescricaoExtras(comp.getDescricao());
        return comp.getPrecoTotal();
    }

    public Ticket atribuirAssento(int ticketId, String assento) {
        if (assento == null || assento.trim().isEmpty())
            throw new IllegalArgumentException("Assento invalido.");
        Ticket ticket = dao.findById(ticketId);
        if (ticket == null)
            throw new IllegalArgumentException("Ticket nao encontrado.");
        ticket.setAssento(assento.trim());
        ticket.auditar(clock);
        return dao.update(ticket);
    }

    public Ticket buscarPorId(int id) {
        return dao.findById(id);
    }

    public Ticket buscarPorCodigo(String codigo) {
        return (codigo == null) ? null : dao.findByDocumento(codigo.trim());
    }

    public Ticket[] listarTodos() {
        List<Ticket> todos = dao.findAll();
        return todos.toArray(Ticket[]::new);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }

    private String gerarCodigo(Voo voo) {
        String prefixo = (voo != null && voo.getId() > 0) ? "VOO" + voo.getId() : "VOO0";
        return prefixo + "-" + Long.toHexString(System.nanoTime()).toUpperCase();
    }
}