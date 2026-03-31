package passageiro;

import java.util.List;

public record ResultadoImportacaoPassageiros(int linhasProcessadas, int importados, List<String> erros) {
    public ResultadoImportacaoPassageiros {
        erros = List.copyOf(erros);
    }

    public int falhas() {
        return linhasProcessadas - importados;
    }

    public boolean possuiFalhas() {
        return !erros.isEmpty();
    }
}