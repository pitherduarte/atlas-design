package atlas.dados;

import atlas.modelo.Pedido;

import java.time.LocalDate;
import java.util.List;

/** Recorte dos pedidos do protótipo do Figma. */
public final class PedidosExemplo {
    public static final LocalDate DATA = LocalDate.of(2026, 11, 15);

    private PedidosExemplo() { }

    public static List<Pedido> criar() {
        return List.of(
            pedido("Rafael Nunes", "110", "12:35", "Retirada", "Cozinha", "Novo",
                item("Filé à Piquiras", 1, 84), item("Robalo ao Jade", 1, 78), item("Chá mate artesanal", 1, 22)),
            pedido("Patrícia Martins", "109", "12:33", "Retirada", "Cozinha", "Em preparo",
                item("Medalhão Gratin", 2, 80), item("Robalo ao Jade", 1, 78), item("Água mineral com gás", 2, 14)),
            pedido("Beatriz Rocha", "108", "12:32", "Retirada", "Padaria", "Pronto",
                item("Filé Especial", 2, 34), item("Café espresso duplo", 1, 19), item("Filé ao Grana Padano", 1, 35)),
            pedido("Marcelo Pires", "107", "12:31", "Retirada", "Bar", "Pronto",
                item("Chá mate artesanal", 2, 22), item("Suco natural de laranja", 1, 25), item("Água mineral com gás", 1, 14)),
            pedido("Lívia Ramos", "106", "12:30", "Mesa 06", "Cozinha", "Entregue",
                item("Filé à Parmegiana", 2, 150), item("Chá mate artesanal", 1, 22), item("Petit Havana", 1, 22)),
            pedido("Henrique Reis", "105", "12:29", "Mesa 03", "Cozinha", "Entregue",
                item("Medalhão Gratin", 1, 80), item("Robalo ao Jade", 1, 78), item("Suco natural de laranja", 1, 25)),
            pedido("Atendimento 04", "104", "12:28", "Mesa 04", "Cozinha", "Em preparo",
                item("Picanha ao Biro-Biro", 2, 87)),
            pedido("Atendimento 04", "103", "12:24", "Mesa 04", "Bar", "Entregue",
                item("Soda italiana de morango", 2, 29)),
            pedido("Atendimento 07", "102", "12:22", "Mesa 07", "Cozinha", "Pronto",
                item("Medalhão Gratin", 1, 80)),
            pedido("Camila Rodrigues", "100", "12:16", "Entrega", "Entrega", "Entregue",
                item("Filé à Piquiras", 1, 84)),
            new Pedido("Atendimento 02", "PED-066", "20:12", "Mesa 02", "Cozinha", "Entregue", DATA.minusDays(1),
                item("Medalhão Gratin", 1, 80), item("Água mineral com gás", 1, 14)),
            new Pedido("Atendimento 05", "PED-065", "19:40", "Mesa 05", "Bar", "Entregue", DATA.minusDays(1),
                item("Chá mate artesanal", 2, 22))
        );
    }

    private static Pedido pedido(String cliente, String numero, String hora, String origem, String estacao,
                                 String status, Pedido.Item... itens) {
        return new Pedido(cliente, "PED-" + numero, hora, origem, estacao, status, DATA, itens);
    }

    private static Pedido.Item item(String produto, int quantidade, int precoEmReais) {
        return new Pedido.Item(produto, quantidade, precoEmReais);
    }
}
