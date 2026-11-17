package atlas.dados;

import atlas.modelo.Produto;

import java.math.BigDecimal;
import java.util.List;

/** Catálogo e comandas fechadas reutilizados dos quadros de Gerência do Figma. */
public final class GestaoExemplo {
    public record Comanda(String codigo, String mesa, String horario, String valor) {
        public BigDecimal total() { return new BigDecimal(valor); }
    }

    private GestaoExemplo() { }

    public static List<Produto> produtos() {
        return List.of(
            new Produto("Filé à Piquiras", "Carnes", 84.00),
            new Produto("Medalhão Gratin", "Carnes", 80.00),
            new Produto("Picanha ao Biro-Biro", "Carnes", 87.00),
            new Produto("Medalhão Grosvone House", "Carnes", 80.00),
            new Produto("Filé à Parmegiana", "Carnes", 150.00),
            new Produto("Medalhão à Piamontesa", "Carnes", 150.00),
            new Produto("Picanha Oswaldo Aranha", "Carnes", 180.00),
            new Produto("Filé Mignon de Sol", "Carnes", 155.00),
            new Produto("Filetto ao Brie", "Massas", 78.00),
            new Produto("Raviolle de Mussarela de Búfala", "Massas", 59.00),
            new Produto("Farfalle ao Grana Padano", "Massas", 78.00),
            new Produto("Fetuccine ao Vinho", "Massas", 80.00),
            new Produto("Robalo ao Jade", "Peixes e frutos do mar", 78.00),
            new Produto("Camarões Piquiras (grelhados)", "Peixes e frutos do mar", 94.00),
            new Produto("Bacalhau do Chefe", "Peixes e frutos do mar", 97.00),
            new Produto("Bacalhau ao Forno", "Peixes e frutos do mar", 185.00),
            new Produto("Lombo de Bacalhau Assado", "Peixes e frutos do mar", 155.00),
            new Produto("Surubim ao Molho Jade", "Peixes e frutos do mar", 85.00),
            new Produto("Camarões Piquiras (empanados)", "Peixes e frutos do mar", 130.00),
            new Produto("Filé ao Grana Padano", "Sanduíches", 35.00),
            new Produto("Filé Especial", "Sanduíches", 34.00),
            new Produto("Cheese Mignon", "Sanduíches", 34.00),
            new Produto("Filé da Maria", "Sanduíches", 34.00),
            new Produto("Filé ao Catupiry", "Sanduíches", 34.00),
            new Produto("Filé ao Trio de Cogumelos", "Sanduíches", 34.00),
            new Produto("Piquiras Flambado", "Sobremesas", 22.00),
            new Produto("Petit Gateau", "Sobremesas", 22.00),
            new Produto("Petit Havana", "Sobremesas", 22.00),
            new Produto("Creme Brûlée", "Sobremesas", 23.00),
            new Produto("Creme de Mamão Papaya", "Sobremesas", 19.00),
            new Produto("Panqueca ao Doce de Leite", "Sobremesas", 22.00),
            new Produto("Chá mate artesanal", "Bebidas", 22.00),
            new Produto("Suco natural de laranja", "Bebidas", 25.00),
            new Produto("Refrigerante lata", "Bebidas", 16.00),
            new Produto("Água mineral com gás", "Bebidas", 14.00),
            new Produto("Café espresso duplo", "Bebidas", 19.00),
            new Produto("Soda italiana de morango", "Bebidas", 29.00),
            new Produto("Limonada suíça", "Bebidas", 26.00),
            new Produto("Suco de uva integral", "Bebidas", 28.00),
            new Produto("Água tônica", "Bebidas", 17.00),
            new Produto("Cappuccino especial", "Bebidas", 24.00)
        );
    }

    public static List<Comanda> comandas() {
        return List.of(
            new Comanda("CMD-006", "Mesa 06", "12:32", "344.00"),
            new Comanda("CMD-003", "Mesa 03", "12:30", "183.00"),
            new Comanda("CMD-002", "Mesa 02", "12:17", "78.00"),
            new Comanda("CMD-180", "Mesa 01", "11:58", "409.00"),
            new Comanda("CMD-179", "Mesa 02", "11:55", "108.00"),
            new Comanda("CMD-178", "Mesa 03", "11:52", "140.00"),
            new Comanda("CMD-177", "Mesa 04", "11:49", "188.00"),
            new Comanda("CMD-176", "Mesa 05", "11:46", "127.00"),
            new Comanda("CMD-175", "Mesa 06", "11:43", "162.00"),
            new Comanda("CMD-174", "Mesa 07", "11:40", "354.00"),
            new Comanda("CMD-173", "Mesa 08", "11:37", "112.00"),
            new Comanda("CMD-172", "Mesa 09", "11:34", "214.00"),
            new Comanda("CMD-171", "Mesa 10", "11:31", "176.00"),
            new Comanda("CMD-170", "Mesa 11", "11:28", "123.00"),
            new Comanda("CMD-169", "Mesa 12", "11:25", "202.00"),
            new Comanda("CMD-168", "Mesa 01", "11:22", "348.00"),
            new Comanda("CMD-167", "Mesa 02", "11:19", "108.00"),
            new Comanda("CMD-166", "Mesa 03", "11:16", "213.00"),
            new Comanda("CMD-165", "Mesa 04", "11:13", "188.00"),
            new Comanda("CMD-164", "Mesa 05", "11:10", "126.00"),
            new Comanda("CMD-163", "Mesa 06", "11:07", "182.00"),
            new Comanda("CMD-162", "Mesa 07", "11:04", "418.00"),
            new Comanda("CMD-161", "Mesa 08", "11:01", "112.00"),
            new Comanda("CMD-160", "Mesa 09", "10:58", "210.00"),
            new Comanda("CMD-159", "Mesa 10", "10:55", "218.00"),
            new Comanda("CMD-158", "Mesa 11", "10:52", "122.00"),
            new Comanda("CMD-157", "Mesa 12", "10:49", "190.00"),
            new Comanda("CMD-156", "Mesa 01", "10:46", "404.00"),
            new Comanda("CMD-155", "Mesa 02", "10:43", "108.00"),
            new Comanda("CMD-154", "Mesa 03", "10:40", "239.00"),
            new Comanda("CMD-153", "Mesa 04", "10:37", "188.00"),
            new Comanda("CMD-152", "Mesa 05", "10:34", "126.00"),
            new Comanda("CMD-151", "Mesa 06", "10:31", "203.00")
        );
    }
}
