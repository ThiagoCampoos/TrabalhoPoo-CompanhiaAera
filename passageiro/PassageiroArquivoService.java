package passageiro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PassageiroArquivoService {
    private final PassageiroService passageiroService;

    public PassageiroArquivoService(PassageiroService passageiroService) {
        this.passageiroService = passageiroService;
    }

    public ResultadoImportacaoPassageiros importar(Path arquivo) {
        if (arquivo == null || !Files.isRegularFile(arquivo)) {
            throw new IllegalArgumentException("Arquivo invalido.");
        }

        int total = 0;
        int sucesso = 0;
        List<String> erros = new ArrayList<>();

        try {
            for (String linha : Files.readAllLines(arquivo)) {
                if (linha == null || linha.isBlank() || linha.trim().startsWith("#")) continue;
                total++;
                String[] partes = linha.split(";");
                if (partes.length < 5) {
                    erros.add("Linha " + total + ": formato 'nome;yyyy-MM-dd;documento;login;senha'.");
                    continue;
                }
                try {
                    String nome = partes[0].trim();
                    LocalDate nascimento = LocalDate.parse(partes[1].trim());
                    String documento = partes[2].trim();
                    String login = partes[3].trim();
                    String senha = partes[4].trim();
                    passageiroService.criar(nome, nascimento, documento, login, senha);
                    sucesso++;
                } catch (Exception e) {
                    erros.add("Linha " + total + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler arquivo: " + e.getMessage(), e);
        }

        return new ResultadoImportacaoPassageiros(total, sucesso, erros);
    }
}