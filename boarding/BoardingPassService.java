package boarding;

import comum.Repositorio;
import comum.SystemClock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class BoardingPassService {
    private final Repositorio<BoardingPass> dao;
    private final SystemClock clock;

    public BoardingPassService(Repositorio<BoardingPass> dao, SystemClock clock) {
        this.dao = dao;
        this.clock = clock;
    }

    public BoardingPass criarParaTicket(int ticketId, String conteudo) {
        BoardingPass boardingPass = new BoardingPass(0, ticketId, conteudo);
        boardingPass.auditar(clock);
        return dao.create(boardingPass);
    }

    public BoardingPass buscarPorTicketId(int ticketId) {
        if (dao instanceof BoardingPassDaoJdbc jdbcDao) {
            return jdbcDao.findByTicketId(ticketId);
        }
        return dao.findAll().stream()
                .filter(bp -> bp != null && bp.getTicketId() == ticketId)
                .findFirst()
                .orElse(null);
    }

    public BoardingPass[] listarTodos() {
        List<BoardingPass> todos = dao.findAll();
        return todos.toArray(new BoardingPass[0]);
    }

    public boolean remover(int id) {
        return dao.deleteById(id);
    }
    
    public Path gerarBoardingPassPdf(int ticketId, Path destino) {
        BoardingPass pass = buscarPorTicketId(ticketId);
        if (pass == null) {
            throw new IllegalArgumentException("Boarding pass nao encontrado para o ticket informado.");
        }

        Path arquivo = (destino == null || destino.toString().isBlank())
                ? Path.of("boarding-pass-" + ticketId + ".pdf")
                : destino;
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            String conteudo = """
                    BOARDING PASS
                    Ticket: %s
                    conteudo: %s
                    Data de Criação: %s
                    Voo: %s
                    Gerado em: %s
                    """.formatted(
                    pass.getTicketId(),
                    pass.getConteudo(),
                    pass.getDataCriacao(),
                    pass.getId(),
                    clock.now()
            );
            Files.writeString(arquivo, conteudo, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return arquivo;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao gerar arquivo de boarding pass: " + e.getMessage(), e);
        }
    }
}