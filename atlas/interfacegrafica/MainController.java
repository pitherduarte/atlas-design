package atlas.interfacegrafica;

import atlas.dados.PedidosExemplo;
import atlas.modelo.Pedido;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Navegação e dados da sessão de demonstração. */
public class MainController {
    @FXML private VBox sidebar;
    @FXML private HBox brandRow;
    @FXML private Label logoLabel;
    @FXML private StackPane contentHost;
    @FXML private Button collapseButton;
    @FXML private Button logoutButton;
    @FXML private Button homeButton;
    @FXML private Button ordersButton;
    @FXML private Button hallButton;
    @FXML private Button menuButton;
    @FXML private Button staffButton;
    @FXML private Button reportsButton;
    @FXML private Button historyButton;
    @FXML private Button settingsButton;

    private final List<Pedido> pedidos = PedidosExemplo.criar();
    private final GestaoController gestao = new GestaoController(pedidos);
    private final SalaoController salao = new SalaoController(pedidos);
    private List<Button> navegacao;
    private Button ativo;
    private boolean recolhida;
    private Parent telaPedidos;
    private PedidosController pedidosController;

    @FXML
    private void initialize() {
        navegacao = List.of(homeButton, ordersButton, hallButton, menuButton,
            staffButton, reportsButton, historyButton, settingsButton);
        List<String> icones = List.of("home", "orders", "hall", "menu", "staff", "reports", "history", "settings");
        for (int i = 0; i < navegacao.size(); i++) {
            Button botao = navegacao.get(i);
            botao.setUserData(icones.get(i));
            botao.setAccessibleText(botao.getText());
            botao.setTooltip(new Tooltip(botao.getText()));
        }
        logoLabel.setGraphic(AtlasVisuals.icone("logo", 150));
        logoLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        logoutButton.setGraphic(AtlasVisuals.icone("logout", 18));
        logoutButton.setTooltip(new Tooltip("Sair"));
        atualizarSidebar();
        showDashboard();
    }

    @FXML
    private void toggleSidebar() {
        recolhida = !recolhida;
        atualizarSidebar();
    }

    private void atualizarSidebar() {
        double largura = recolhida ? 76 : 240;
        sidebar.setMinWidth(largura);
        sidebar.setPrefWidth(largura);
        sidebar.setMaxWidth(largura);
        sidebar.getStyleClass().remove("collapsed");
        if (recolhida) {
            sidebar.getStyleClass().add("collapsed");
        }

        logoLabel.setManaged(!recolhida);
        logoLabel.setVisible(!recolhida);
        brandRow.setAlignment(recolhida ? Pos.CENTER : Pos.CENTER_LEFT);
        ImageView direcao = AtlasVisuals.icone("sidebar-direction", 8);
        direcao.setTranslateX(2);
        StackPane controle = new StackPane(AtlasVisuals.icone("sidebar", 20), direcao);
        controle.setScaleX(recolhida ? -1 : 1);
        collapseButton.setGraphic(controle);
        String acao = recolhida ? "Expandir navegação" : "Recolher navegação";
        collapseButton.setAccessibleText(acao);
        collapseButton.setTooltip(new Tooltip(acao));
        navegacao.forEach(botao -> botao.setContentDisplay(recolhida ? ContentDisplay.GRAPHIC_ONLY : ContentDisplay.LEFT));
    }

    private void mostrar(Button aba, Node tela) {
        for (Button botao : navegacao) {
            if (botao == ativo) {
                botao.getStyleClass().remove("active");
            }
            boolean selecionado = botao == aba;
            if (selecionado) {
                botao.getStyleClass().add("active");
            }
            String icone = (String) botao.getUserData();
            botao.setGraphic(AtlasVisuals.icone(icone + (selecionado ? "-active" : ""), 20));
        }
        ativo = aba;
        contentHost.getChildren().setAll(tela);
        contentHost.requestFocus();
    }

    private <T> void abrirFXML(Button aba, String arquivo, Consumer<T> configurar) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recursos/telas/" + arquivo));
            Parent tela = loader.load();
            configurar.accept(loader.getController());
            mostrar(aba, tela);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível abrir " + arquivo, exception);
        }
    }

    @FXML
    private void showDashboard() {
        this.<DashboardController>abrirFXML(homeButton, "Dashboard.fxml", controller ->
            controller.configurar(pedidos, List.of(this::showHall, this::showOrders,
                this::showService, this::showFinance, this::showProduction, this::showCleaning)));
    }

    @FXML
    private void showOrders() {
        if (telaPedidos == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recursos/telas/Pedidos.fxml"));
            try {
                telaPedidos = loader.load();
                pedidosController = loader.getController();
                pedidosController.configurar(pedidos);
            } catch (IOException exception) {
                throw new IllegalStateException("Não foi possível abrir os pedidos.", exception);
            }
        }
        pedidosController.voltar();
        mostrar(ordersButton, telaPedidos);
    }

    private void showOrders(String estacao) {
        showOrders();
        pedidosController.abrirFila(estacao);
    }

    @FXML private void showHall() { showHall(0); }
    private void showHall(int mesa) { mostrar(hallButton, salao.visualizar(mesa)); }
    @FXML private void showMenu() { mostrar(menuButton, gestao.menu()); }
    @FXML private void showStaff() { mostrar(staffButton, gestao.funcionarios()); }
    @FXML private void showReports() { mostrar(reportsButton, gestao.relatorios()); }
    @FXML private void showHistory() { mostrar(historyButton, gestao.historico()); }
    @FXML private void showSettings() { mostrar(settingsButton, gestao.configuracoes(this::showStaff)); }
    private void showService() { mostrar(homeButton, salao.atendimento(this::showDashboard, this::showHall)); }
    private void showFinance() { mostrar(homeButton, salao.financeiro(this::showDashboard, this::showHall, this::showReports)); }
    private void showProduction() { mostrar(homeButton, salao.producao(this::showDashboard, this::showOrders)); }
    private void showCleaning() { mostrar(homeButton, salao.higienizacao(this::showDashboard, this::showHall)); }

    @FXML
    private void logout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(contentHost.getScene().getWindow());
        alert.setTitle("Atlas Desktop");
        alert.setHeaderText("Sessão demonstrativa");
        alert.setContentText("O acesso de Marina Costa é simulado nesta versão.");
        alert.showAndWait();
    }
}
