package atlas.modelo;

/**
 * Modelo inicial de mesa do salao.
 */
public class Mesa {

    private int numero;
    private int capacidade;
    private String status;

    public Mesa(int numero, int capacidade, String status) {
        this.numero = numero;
        this.capacidade = capacidade;
        this.status = status;
    }

    public int getNumero() { return numero; }
    public int getCapacidade() { return capacidade; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }
}
