package atlas.interfacegrafica;

import atlas.modelo.Pedido;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class DashboardController {
    @FXML private GridPane cards;
    @FXML private Label greetingLabel;
    @FXML private Label dateLabel;
    @FXML private Label newOrders;
    @FXML private Label preparingOrders;
    @FXML private Label readyOrders;
    @FXML private Label orderCount;
    @FXML private Label kitchenOrders;
    @FXML private Label barOrders;
    @FXML private Label bakeryOrders;

    @FXML
    private void initialize() {
        atualizarRelogio();
        Timeline relogio = new Timeline(new KeyFrame(Duration.seconds(10), event -> atualizarRelogio()));
        relogio.setCycleCount(Timeline.INDEFINITE);
        dateLabel.sceneProperty().addListener((observable, previous, scene) -> {
            if (scene == null) {
                relogio.stop();
            } else {
                atualizarRelogio();
                relogio.play();
            }
        });
    }

    void configurar(List<Pedido> pedidos, List<Runnable> destinos) {
        List<Node> botoes = cards.getChildren();
        for (int i = 0; i < destinos.size(); i++) {
            Button cartao = (Button) botoes.get(i);
            Runnable destino = destinos.get(i);
            cartao.setOnAction(event -> destino.run());
            cartao.widthProperty().addListener((observable, antes, largura) -> {
                boolean compacto = largura.doubleValue() < 355;
                if (compacto && !cartao.getStyleClass().contains("compact")) {
                    cartao.getStyleClass().add("compact");
                } else if (!compacto) {
                    cartao.getStyleClass().remove("compact");
                }
            });
        }

        List<Pedido> atuais = pedidos.stream()
            .filter(pedido -> pedido.getData().equals(PedidosController.DATA_OPERACAO))
            .toList();
        newOrders.setText(contar(atuais, "Novo"));
        preparingOrders.setText(contar(atuais, "Em preparo"));
        readyOrders.setText(contar(atuais, "Pronto"));
        orderCount.setText(atuais.size() + " pedidos · 15 nov.");
        kitchenOrders.setText(contarEstacao(atuais, "Cozinha"));
        barOrders.setText(contarEstacao(atuais, "Bar"));
        bakeryOrders.setText(contarEstacao(atuais, "Padaria"));
    }

    private String contar(List<Pedido> pedidos, String status) {
        long total = pedidos.stream().filter(pedido -> pedido.getStatus().equals(status)).count();
        return String.format("%02d", total);
    }

    private String contarEstacao(List<Pedido> pedidos, String estacao) {
        long total = pedidos.stream().filter(pedido -> pedido.getEstacao().equals(estacao))
            .filter(pedido -> pedido.estaEmAndamento()).count();
        return String.format("%02d", total);
    }

    private void atualizarRelogio() {
        LocalDateTime agora = LocalDateTime.now();
        int hora = agora.getHour();
        String saudacao = hora < 12 ? "Bom dia" : hora < 18 ? "Boa tarde" : "Boa noite";
        greetingLabel.setText(saudacao + ", Marina.");
        String data = agora.format(DateTimeFormatter.ofPattern(
            "EEEE, d 'de' MMMM 'de' yyyy '·' HH:mm", AtlasVisuals.BRASIL));
        dateLabel.setText(Character.toUpperCase(data.charAt(0)) + data.substring(1));
    }
}
