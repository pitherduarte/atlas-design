package atlas.modelo;

/**
 * Modelo inicial de produto do cardapio.
 */
public class Produto {

    private String nome;
    private String categoria;
    private double preco;
    private boolean disponivel;

    public Produto(String nome, String categoria, double preco) {
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.disponivel = true;
    }

    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getPreco() { return preco; }
    public boolean isDisponivel() { return disponivel; }

    public void setNome(String nome) { this.nome = nome; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public void setPreco(double preco) { this.preco = preco; }
    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
