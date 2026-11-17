package atlas.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Pedido de demonstração, com itens e histórico de alterações da sessão. */
public class Pedido {
    public record Item(String produto, int quantidade, BigDecimal unitario) {
        public Item(String produto, int quantidade, int precoEmReais) {
            this(produto, quantidade, BigDecimal.valueOf(precoEmReais));
        }

        public BigDecimal subtotal() {
            return unitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }

    public record Alteracao(LocalDateTime instante, String status) { }

    private final String cliente;
    private final String codigo;
    private final String horario;
    private final String tipo;
    private final String estacao;
    private final int quantidadeItens;
    private final BigDecimal total;
    private final LocalDate data;
    private final List<Item> itens;
    private final List<Alteracao> historico = new ArrayList<>();
    private String status;

    public Pedido(String cliente, String codigo, String horario, String tipo, String estacao,
                  int quantidadeItens, double total, String status) {
        this(cliente, codigo, horario, tipo, estacao, quantidadeItens, BigDecimal.valueOf(total),
            status, LocalDate.now(), List.of());
    }

    public Pedido(String cliente, String codigo, String horario, String tipo, String estacao,
                  String status, LocalDate data, Item... itens) {
        this(cliente, codigo, horario, tipo, estacao,
            List.of(itens).stream().mapToInt(Item::quantidade).sum(),
            List.of(itens).stream().map(Item::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add),
            status, data, List.of(itens));
    }

    private Pedido(String cliente, String codigo, String horario, String tipo, String estacao,
                   int quantidadeItens, BigDecimal total, String status, LocalDate data, List<Item> itens) {
        this.cliente = cliente;
        this.codigo = codigo;
        this.horario = horario;
        this.tipo = tipo;
        this.estacao = estacao;
        this.quantidadeItens = quantidadeItens;
        this.total = total;
        this.status = status;
        this.data = data;
        this.itens = itens;
    }

    public String getCliente() { return cliente; }
    public String getCodigo() { return codigo; }
    public String getHorario() { return horario; }
    public String getTipo() { return tipo; }
    public String getEstacao() { return estacao; }
    public int getQuantidadeItens() { return quantidadeItens; }
    public double getTotal() { return total.doubleValue(); }
    public String getStatus() { return status; }
    public LocalDate getData() { return data; }
    public List<Item> getItens() { return itens; }
    public BigDecimal getValorTotal() { return total; }
    public List<Alteracao> getHistorico() { return List.copyOf(historico); }

    public boolean estaEmAndamento() {
        return proximoStatus() != null;
    }

    private String proximoStatus() {
        return switch (status) {
            case "Novo" -> "Em preparo";
            case "Em preparo" -> "Pronto";
            case "Pronto" -> "Entregue";
            default -> null;
        };
    }

    public void avancarStatus() {
        String proximo = proximoStatus();
        if (proximo != null) {
            status = proximo;
            historico.add(new Alteracao(LocalDateTime.now(), status));
        }
    }
}
