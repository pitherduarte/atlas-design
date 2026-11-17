package atlas.modelo;

import java.time.LocalTime;

/** Funcionário e condições de trabalho da demonstração. */
public class Funcionario {
    private String nome;
    private String cargo;
    private boolean ativo = true;
    private double salario;
    private double gorjetas;
    private String horario = "10:00–18:00";
    private String email = "";

    public Funcionario(String nome, String cargo) {
        this.nome = nome;
        this.cargo = cargo;
    }

    public Funcionario(String nome, String cargo, double salario, double gorjetas, String horario, boolean ativo) {
        this(nome, cargo);
        this.salario = salario;
        this.gorjetas = gorjetas;
        this.horario = horario;
        this.ativo = ativo;
    }

    public String getNome() { return nome; }
    public String getCargo() { return cargo; }
    public boolean isAtivo() { return ativo; }
    public double getSalario() { return salario; }
    public double getGorjetas() { return gorjetas; }
    public String getHorario() { return horario; }
    public String getEmail() { return email; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public void atualizar(String nome, String cargo, double salario, double gorjetas, String horario, String email, boolean ativo) {
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
        this.gorjetas = gorjetas;
        this.horario = horario;
        this.email = email;
        this.ativo = ativo;
    }

    public String getExpediente(LocalTime agora) {
        if (!ativo) {
            return "—";
        }
        String[] partes = horario.split("–");
        LocalTime inicio = LocalTime.parse(partes[0]);
        LocalTime fim = LocalTime.parse(partes[1]);
        boolean trabalhando;
        if (inicio.isBefore(fim)) {
            trabalhando = !agora.isBefore(inicio) && agora.isBefore(fim);
        } else {
            trabalhando = !agora.isBefore(inicio) || agora.isBefore(fim);
        }
        return trabalhando ? "Trabalhando agora" : "Fora do expediente";
    }
}
