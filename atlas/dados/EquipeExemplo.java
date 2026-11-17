package atlas.dados;

import atlas.modelo.Funcionario;

import java.util.List;

/** Funcionários cujas fichas foram consultadas no Figma. */
public final class EquipeExemplo {
    private EquipeExemplo() { }

    public static List<Funcionario> criar() {
        return List.of(
            new Funcionario("Marina Costa", "Gerente", 7150.00, 0.0, "10:00–18:00", true),
            new Funcionario("Joana Lima", "Garçom", 2280.00, 4.0, "11:00–19:00", true),
            new Funcionario("Igor Santos", "Garçom", 2280.00, 4.0, "11:00–19:00", true),
            new Funcionario("Sofia Almeida", "Caixa", 2620.00, 0.0, "10:00–18:00", true),
            new Funcionario("Lucas Melo", "Garçom", 2240.00, 4.0, "11:00–19:00", true),
            new Funcionario("Camila Reis", "Garçom", 2210.00, 4.0, "08:00–16:00", true),
            new Funcionario("Pedro Araújo", "Garçom", 2040.00, 4.0, "18:00–02:00", true),
            new Funcionario("Flávia Mendes", "Garçom", 2120.00, 0.0, "10:00–18:00", false),
            new Funcionario("Ana Cardoso", "Garçom", 2200.00, 4.0, "11:00–19:00", true),
            new Funcionario("Guilherme Tavares", "Garçom", 2280.00, 4.0, "16:00–00:00", true),
            new Funcionario("Júlia Barbosa", "Garçom", 2360.00, 4.0, "18:00–02:00", true),
            new Funcionario("Mateus Castro", "Garçom", 2040.00, 4.0, "08:00–16:00", true),
            new Funcionario("Adriana Rocha", "Garçom", 2120.00, 4.0, "10:00–18:00", true),
            new Funcionario("Nicolas Siqueira", "Garçom", 2200.00, 4.0, "11:00–19:00", true),
            new Funcionario("Clara Dias", "Garçom", 2280.00, 4.0, "16:00–00:00", true),
            new Funcionario("Paulo Pinto", "Garçom", 2360.00, 4.0, "18:00–02:00", true),
            new Funcionario("Eduarda Oliveira", "Garçom", 2040.00, 4.0, "08:00–16:00", true),
            new Funcionario("Felipe Moreira", "Garçom", 2120.00, 4.0, "10:00–18:00", true),
            new Funcionario("Helena Monteiro", "Garçom", 2200.00, 4.0, "11:00–19:00", true),
            new Funcionario("Vinícius Ramos", "Garçom", 2280.00, 4.0, "16:00–00:00", true),
            new Funcionario("Letícia Campos", "Garçom", 2360.00, 0.0, "18:00–02:00", false),
            new Funcionario("Daniel Farias", "Garçom", 2040.00, 4.0, "08:00–16:00", true),
            new Funcionario("Regina Gomes", "Garçom", 2120.00, 4.0, "10:00–18:00", true),
            new Funcionario("Arthur Moura", "Garçom", 2200.00, 4.0, "11:00–19:00", true),
            new Funcionario("André Araújo", "Cozinheiro", 2150.00, 4.0, "08:00–16:00", true),
            new Funcionario("Bárbara Castro", "Barman", 2270.00, 4.0, "10:00–18:00", true),
            new Funcionario("Caio Oliveira", "Padaria", 2390.00, 4.0, "11:00–19:00", true),
            new Funcionario("Débora Farias", "Limpeza", 2510.00, 4.0, "16:00–00:00", true),
            new Funcionario("Elias Cardoso", "Cozinheiro", 2630.00, 4.0, "18:00–02:00", true),
            new Funcionario("Fernanda Siqueira", "Barman", 2750.00, 0.0, "08:00–16:00", true),
            new Funcionario("Gabriel Monteiro", "Padaria", 2870.00, 0.0, "10:00–18:00", true),
            new Funcionario("Isabela Moura", "Limpeza", 2990.00, 0.0, "11:00–19:00", true),
            new Funcionario("Jorge Barbosa", "Cozinheiro", 2150.00, 0.0, "16:00–00:00", true),
            new Funcionario("Karina Pinto", "Barman", 2270.00, 0.0, "18:00–02:00", true),
            new Funcionario("Leonardo Campos", "Padaria", 2390.00, 0.0, "08:00–16:00", true),
            new Funcionario("Mariana Mendes", "Limpeza", 2510.00, 0.0, "10:00–18:00", false),
            new Funcionario("Nuno Rocha", "Cozinheiro", 2630.00, 0.0, "11:00–19:00", true),
            new Funcionario("Olívia Moreira", "Barman", 2750.00, 0.0, "16:00–00:00", true),
            new Funcionario("Priscila Gomes", "Padaria", 2870.00, 0.0, "18:00–02:00", true),
            new Funcionario("Raul Tavares", "Limpeza", 2990.00, 0.0, "08:00–16:00", true),
            new Funcionario("Simone Dias", "Cozinheiro", 2150.00, 0.0, "10:00–18:00", true),
            new Funcionario("André Campos", "Barman", 2270.00, 0.0, "11:00–19:00", true),
            new Funcionario("Bárbara Mendes", "Padaria", 2390.00, 0.0, "16:00–00:00", true),
            new Funcionario("Caio Rocha", "Limpeza", 2510.00, 0.0, "18:00–02:00", true),
            new Funcionario("Débora Moreira", "Cozinheiro", 2630.00, 0.0, "08:00–16:00", true),
            new Funcionario("Elias Gomes", "Barman", 2750.00, 0.0, "10:00–18:00", true),
            new Funcionario("Fernanda Tavares", "Padaria", 2870.00, 0.0, "11:00–19:00", false),
            new Funcionario("Gabriel Dias", "Limpeza", 2990.00, 0.0, "16:00–00:00", true),
            new Funcionario("Isabela Ramos", "Cozinheiro", 2150.00, 0.0, "18:00–02:00", true),
            new Funcionario("Jorge Araújo", "Barman", 2270.00, 0.0, "08:00–16:00", true),
            new Funcionario("Karina Castro", "Padaria", 2390.00, 0.0, "10:00–18:00", true),
            new Funcionario("Leonardo Oliveira", "Limpeza", 2510.00, 0.0, "11:00–19:00", true),
            new Funcionario("Mariana Farias", "Cozinheiro", 2630.00, 0.0, "16:00–00:00", true),
            new Funcionario("Nuno Cardoso", "Barman", 2750.00, 0.0, "18:00–02:00", true),
            new Funcionario("Olívia Siqueira", "Padaria", 2870.00, 0.0, "08:00–16:00", true),
            new Funcionario("Priscila Monteiro", "Limpeza", 2990.00, 0.0, "10:00–18:00", true),
            new Funcionario("Raul Moura", "Cozinheiro", 2150.00, 0.0, "11:00–19:00", true),
            new Funcionario("Simone Barbosa", "Barman", 2270.00, 0.0, "16:00–00:00", false),
            new Funcionario("André Oliveira", "Caixa", 2390.00, 0.0, "18:00–02:00", true),
            new Funcionario("Bárbara Farias", "Caixa", 2510.00, 0.0, "08:00–16:00", true),
            new Funcionario("Caio Cardoso", "Caixa", 2630.00, 0.0, "10:00–18:00", true),
            new Funcionario("Débora Siqueira", "Caixa", 2750.00, 0.0, "11:00–19:00", true),
            new Funcionario("Elias Monteiro", "Caixa", 2870.00, 0.0, "16:00–00:00", true),
            new Funcionario("Fernanda Moura", "Caixa", 2990.00, 0.0, "18:00–02:00", true),
            new Funcionario("Priscila Dias", "Cumim", 2150.00, 0.0, "16:00–00:00", true)
        );
    }
}
