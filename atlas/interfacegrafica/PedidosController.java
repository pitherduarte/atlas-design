package atlas.interfacegrafica;

import atlas.dados.PedidosExemplo;
import atlas.modelo.Pedido;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/** Pesquisa, seleção e acompanhamento dos pedidos da sessão. */
public class PedidosController {
    static final LocalDate DATA_OPERACAO = PedidosExemplo.DATA;
    private static final DateTimeFormatter DIA =
        DateTimeFormatter.ofPattern("d MMM", AtlasVisuals.BRASIL);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final String TODOS = "Todos";
    private static final String TODAS = "Todas";
    private static final String[] SITUACOES = {
        TODOS, "Novo", "Em preparo", "Pronto", "Entregue", "Cancelado"
    };
    private static final String[] FILTROS = {
        TODOS, "Novos", "Em preparo", "Prontos", "Entregues", "Cancelados"
    };
    private static final int[] LARGURAS_FILTROS = {98, 97, 131, 108, 124, 134};
    private static final List<String> ESTACOES = List.of(TODAS, "Cozinha", "Bar", "Padaria", "Entrega");

    @FXML private VBox listPage;
    @FXML private VBox detailPage;
    @FXML private ScrollPane detailScroll;
    @FXML private VBox toolbar;
    @FXML private HBox toolbarRow;
    @FXML private HBox orderActions;
    @FXML private FlowPane statusFilters;
    @FXML private HBox stationFilters;
    @FXML private Button searchButton;
    @FXML private Button dateButton;
    @FXML private TextField searchField;
    @FXML private ScrollPane ordersScroll;
    @FXML private TilePane ordersContainer;
    @FXML private Label emptyLabel;
    @FXML private Label totalOrdersLabel;

    private final ToggleGroup situacoes = new ToggleGroup();
    private final ToggleGroup estacoes = new ToggleGroup();
    private final Map<String, Label> contagens = new LinkedHashMap<>();
    private final Map<String, ToggleButton> botoesEstacao = new LinkedHashMap<>();
    private List<Pedido> pedidos = List.of();
    private LocalDate data = DATA_OPERACAO;
    private String filtro = TODOS;
    private String estacao = TODAS;

    @FXML
    private void initialize() {
        searchButton.setGraphic(AtlasVisuals.icone("search", 17));
        searchButton.setGraphicTextGap(8);
        for (int i = 0; i < SITUACOES.length; i++) {
            String status = SITUACOES[i];
            ToggleButton botao = new ToggleButton(FILTROS[i]);
            botao.setMinWidth(LARGURAS_FILTROS[i]);
            botao.setPrefWidth(LARGURAS_FILTROS[i]);
            botao.setPrefHeight(44);
            botao.getStyleClass().add("filter-button");
            botao.getStyleClass().add("filter-" + classeStatus(status).replace("status-", ""));
            botao.setToggleGroup(situacoes);
            botao.setSelected(TODOS.equals(status));
            botao.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
            Label contagem = AtlasVisuals.rotulo("00", "filter-count", classeStatus(status));
            HBox conteudo = new HBox(8, AtlasVisuals.rotulo(FILTROS[i], "filter-label"), contagem);
            conteudo.setAlignment(Pos.CENTER);
            botao.setGraphic(conteudo);
            botao.setOnAction(event -> {
                botao.setSelected(true);
                filtro = status;
                renderizar(true);
            });
            contagens.put(status, contagem);
            statusFilters.getChildren().add(botao);
        }
        listPage.widthProperty().addListener((observable, antes, largura) -> ajustarBarra(largura.doubleValue()));

        for (String opcao : ESTACOES) {
            ToggleButton botao = new ToggleButton(opcao);
            botao.getStyleClass().add("station-chip");
            botao.setToggleGroup(estacoes);
            botao.setSelected(opcao.equals(TODAS));
            botao.setOnAction(event -> filtrarEstacao(opcao));
            botoesEstacao.put(opcao, botao);
            stationFilters.getChildren().add(botao);
        }
        searchField.textProperty().addListener((observable, antes, novo) -> renderizar(true));
        ordersScroll.viewportBoundsProperty().addListener((observable, antes, tamanho) ->
            ajustarGrade(tamanho.getWidth()));
        ajustarGrade(1152);
        atualizarData();
    }

    void configurar(List<Pedido> pedidos) {
        this.pedidos = pedidos;
        renderizar(true);
    }

    private void ajustarBarra(double larguraPagina) {
        double larguraInterna = larguraPagina - listPage.getInsets().getLeft() - listPage.getInsets().getRight();
        if (larguraInterna <= 0) {
            return;
        }
        double larguraAcoes = orderActions.prefWidth(-1) + toolbarRow.getSpacing();
        boolean compacta = larguraInterna < statusFilters.getPrefWrapLength() + larguraAcoes;
        if (compacta && orderActions.getParent() == toolbarRow) {
            toolbarRow.getChildren().remove(orderActions);
            toolbar.getChildren().add(orderActions);
        } else if (!compacta && orderActions.getParent() == toolbar) {
            toolbar.getChildren().remove(orderActions);
            toolbarRow.getChildren().add(orderActions);
        }
        double larguraFiltros = larguraInterna - (compacta ? 0 : larguraAcoes);
        double alturaFiltros = statusFilters.prefHeight(larguraFiltros);
        toolbarRow.setMinHeight(alturaFiltros);
        toolbarRow.setPrefHeight(alturaFiltros);
    }

    void filtrarEstacao(String nome) {
        estacao = nome;
        botoesEstacao.get(nome).setSelected(true);
        renderizar(true);
    }

    void abrirFila(String nome) {
        data = DATA_OPERACAO;
        filtro = TODOS;
        situacoes.getToggles().getFirst().setSelected(true);
        searchField.clear();
        searchField.setVisible(false);
        searchField.setManaged(false);
        atualizarData();
        filtrarEstacao(nome);
    }

    private void ajustarGrade(double largura) {
        if (largura <= 0) {
            return;
        }
        int colunas = Math.clamp((int) ((largura + 16) / 255), 1, 4);
        ordersContainer.setPrefColumns(colunas);
        // Uma fração de pixel de folga evita a quebra da última coluna por arredondamento.
        ordersContainer.setPrefTileWidth(Math.floor((largura - 16 * (colunas - 1)) / colunas));
    }

    @FXML
    private void alternarBusca() {
        boolean abrir = !searchField.isVisible();
        searchField.setManaged(abrir);
        searchField.setVisible(abrir);
        if (abrir) {
            searchField.requestFocus();
        } else {
            searchField.clear();
        }
    }

    @FXML
    private void selecionarData() {
        ContextMenu datas = new ContextMenu();
        for (LocalDate dia : List.of(DATA_OPERACAO, DATA_OPERACAO.minusDays(1))) {
            MenuItem opcao = new MenuItem(dia.format(DIA) + " de " + dia.getYear());
            opcao.setOnAction(event -> {
                data = dia;
                atualizarData();
                renderizar(true);
            });
            datas.getItems().add(opcao);
        }
        datas.show(dateButton, Side.BOTTOM, 0, 4);
    }

    private void atualizarData() {
        String texto = data.format(DIA);
        HBox dataRotulo = new HBox(8, AtlasVisuals.icone("calendar", 17), AtlasVisuals.rotulo(texto, "small-strong"));
        dataRotulo.setAlignment(Pos.CENTER_LEFT);
        HBox conteudo = AtlasVisuals.linha(dataRotulo, AtlasVisuals.icone("caret", 15));
        conteudo.setPrefWidth(136);
        dateButton.setGraphic(conteudo);
        dateButton.setAccessibleText("Data dos pedidos: " + texto + " de " + data.getYear());
    }

    private void renderizar(boolean voltarAoTopo) {
        String busca = AtlasVisuals.normalizarBusca(searchField.getText());
        List<Pedido> correspondentes = pedidos.stream()
            .filter(pedido -> pedido.getData().equals(data))
            .filter(pedido -> TODAS.equals(estacao) || estacao.equals(pedido.getEstacao()))
            .filter(pedido -> AtlasVisuals.normalizarBusca(pedido.getCliente() + " " + pedido.getCodigo()
                + " " + pedido.getTipo()).contains(busca))
            .toList();

        contagens.forEach((status, rotulo) -> rotulo.setText(String.format("%02d",
            correspondentes.stream().filter(pedido -> TODOS.equals(status) || pedido.getStatus().equals(status)).count())));
        List<Pedido> visiveis = correspondentes.stream()
            .filter(pedido -> TODOS.equals(filtro) || pedido.getStatus().equals(filtro)).toList();

        ordersContainer.getChildren().setAll(visiveis.stream().map(this::criarCartao).toList());
        boolean vazio = visiveis.isEmpty();
        emptyLabel.setText("Cancelado".equals(filtro) && busca.isBlank()
            ? "Nenhum pedido cancelado" : "Nenhum pedido para os filtros selecionados");
        emptyLabel.setVisible(vazio);
        emptyLabel.setManaged(vazio);
        totalOrdersLabel.setText(visiveis.size() + (visiveis.size() == 1 ? " pedido" : " pedidos"));
        if (voltarAoTopo) {
            ordersScroll.setVvalue(0);
        }
    }

    private VBox criarCartao(Pedido pedido) {
        Label hora = AtlasVisuals.rotulo(pedido.getHorario(), "small-muted");
        hora.setGraphic(AtlasVisuals.icone("clock", 15));
        Label origem = AtlasVisuals.rotulo(pedido.getTipo(), "small-muted");
        origem.setGraphic(AtlasVisuals.icone(pedido.getTipo().startsWith("Mesa") ? "table" : "bag", 15));

        VBox dados = new VBox(5,
            AtlasVisuals.linha(AtlasVisuals.rotulo(pedido.getCliente(), "order-client"), AtlasVisuals.rotulo(pedido.getCodigo(), "small-muted")),
            hora,
            AtlasVisuals.linha(origem, AtlasVisuals.rotulo(pedido.getEstacao(), "order-station")),
            divisoria(),
            AtlasVisuals.linha(AtlasVisuals.rotulo("Itens (" + pedido.getQuantidadeItens() + ")", "small-strong"),
                AtlasVisuals.rotulo(AtlasVisuals.moeda(pedido.getValorTotal()), "order-total"))
        );
        for (Pedido.Item item : pedido.getItens().stream().limit(2).toList()) {
            Label preco = AtlasVisuals.rotulo(AtlasVisuals.moeda(item.unitario()) + " un.", "order-unit");
            preco.setMinWidth(Region.USE_PREF_SIZE);
            HBox linhaItem = AtlasVisuals.linha(
                AtlasVisuals.rotulo(item.quantidade() + "× " + item.produto(), "small-muted"), preco);
            linhaItem.setSpacing(2);
            dados.getChildren().add(linhaItem);
        }
        if (pedido.getItens().size() > 2) {
            Button mais = AtlasVisuals.botao("Ver mais itens", "text-button", () -> abrirItens(pedido));
            mais.setGraphic(AtlasVisuals.icone("arrow", 13));
            mais.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
            HBox linhaMais = new HBox(mais);
            linhaMais.setAlignment(Pos.CENTER);
            dados.getChildren().add(linhaMais);
        }
        dados.getChildren().stream().filter(HBox.class::isInstance).map(HBox.class::cast)
            .forEach(linha -> linha.setMinHeight(20));

        Button abrir = AtlasVisuals.botao("", "order-link", () -> abrirDetalhe(pedido));
        abrir.setAccessibleText("Ver pedido " + pedido.getCodigo());
        abrir.setGraphic(AtlasVisuals.icone("arrow", 16));
        VBox cartao = new VBox(dados, new Region(), AtlasVisuals.linha(badge(pedido.getStatus()), abrir));
        VBox.setVgrow(cartao.getChildren().get(1), Priority.ALWAYS);
        cartao.getStyleClass().add("order-card");
        cartao.setMinWidth(0);
        cartao.setMaxWidth(Double.MAX_VALUE);
        cartao.prefWidthProperty().bind(ordersContainer.prefTileWidthProperty());
        cartao.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            for (Node alvo = event.getPickResult().getIntersectedNode(); alvo != null && alvo != cartao; alvo = alvo.getParent()) {
                if (alvo instanceof Button) {
                    return;
                }
            }
            abrirDetalhe(pedido);
        });
        return cartao;
    }

    private void abrirItens(Pedido pedido) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(listPage.getScene().getWindow());
        dialogo.setTitle("Itens · " + pedido.getCodigo());
        dialogo.getDialogPane().getStylesheets().setAll(listPage.getScene().getRoot().getStylesheets());
        dialogo.getDialogPane().getButtonTypes().add(new ButtonType("Fechar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE));
        VBox conteudo = new VBox(12, AtlasVisuals.rotulo(pedido.getCliente(), "section-title"),
            AtlasVisuals.rotulo(identificacao(pedido), "page-subtitle"),
            badge(pedido.getStatus()), painelItens(pedido),
            AtlasVisuals.linha(AtlasVisuals.rotulo("Total do pedido", "small-strong"),
                AtlasVisuals.rotulo(AtlasVisuals.moeda(pedido.getValorTotal()), "order-total-large")));
        conteudo.setPrefWidth(518);
        dialogo.getDialogPane().setContent(conteudo);
        dialogo.showAndWait();
    }

    private void abrirDetalhe(Pedido pedido) {
        Button voltar = AtlasVisuals.botao("Voltar a pedidos", "secondary-button", this::voltar);
        VBox titulo = new VBox(4, AtlasVisuals.linha(
            AtlasVisuals.rotulo("Pedido " + pedido.getCodigo(), "page-title"), badge(pedido.getStatus())),
            AtlasVisuals.rotulo(pedido.getCliente() + " · " + identificacao(pedido), "page-subtitle"));

        VBox historico = painel("Histórico do pedido");
        historico.getChildren().add(AtlasVisuals.rotulo(pedido.getHorario() + " · Pedido registrado", "small-muted"));
        for (Pedido.Alteracao alteracao : pedido.getHistorico()) {
            historico.getChildren().add(AtlasVisuals.rotulo(alteracao.instante().format(HORA)
                + " · " + alteracao.status() + " · Marina Costa", "small-strong"));
        }

        VBox resumo = painel("Resumo do pedido");
        resumo.getChildren().addAll(
            AtlasVisuals.linha(AtlasVisuals.rotulo("Estação", "small-muted"), AtlasVisuals.rotulo(pedido.getEstacao(), "small-strong")),
            divisoria(),
            AtlasVisuals.linha(AtlasVisuals.rotulo("Total do pedido", "small-strong"),
                AtlasVisuals.rotulo(AtlasVisuals.moeda(pedido.getValorTotal()), "order-total-large")));
        if (pedido.estaEmAndamento()) {
            String acao = switch (pedido.getStatus()) {
                case "Novo" -> "Iniciar preparo";
                case "Em preparo" -> "Marcar como pronto";
                default -> "Marcar como entregue";
            };
            resumo.getChildren().add(AtlasVisuals.botao(acao, "primary-button", () -> {
                pedido.avancarStatus();
                renderizar(false);
                abrirDetalhe(pedido);
            }));
        }

        VBox esquerda = new VBox(20, painelItens(pedido), historico);
        HBox.setHgrow(esquerda, Priority.ALWAYS);
        resumo.setPrefWidth(330);
        HBox secoes = new HBox(20, esquerda, resumo);
        secoes.setAlignment(Pos.TOP_LEFT);
        resumo.setMaxHeight(Region.USE_PREF_SIZE);
        detailPage.getChildren().setAll(voltar, titulo, secoes);
        exibirDetalhe(true);
    }

    void voltar() {
        exibirDetalhe(false);
    }

    private void exibirDetalhe(boolean detalhe) {
        listPage.setVisible(!detalhe);
        listPage.setManaged(!detalhe);
        detailScroll.setVisible(detalhe);
        detailScroll.setManaged(detalhe);
    }

    private String identificacao(Pedido pedido) {
        return pedido.getCodigo() + " · " + pedido.getTipo() + " · " + pedido.getHorario() + " · " + pedido.getEstacao();
    }

    static Label badge(String status) {
        String classe = classeStatus(status);
        Label badge = AtlasVisuals.rotulo(status, "status-badge", classe);
        if (List.of("Novo", "Em preparo", "Pronto", "Entregue").contains(status)) {
            badge.setGraphic(AtlasVisuals.icone(classe, 14));
        }
        return badge;
    }

    private static String classeStatus(String status) {
        return switch (status) {
            case "Novo" -> "status-new";
            case "Em preparo" -> "status-preparing";
            case "Pronto" -> "status-ready";
            case "Entregue" -> "status-delivered";
            default -> "status-neutral";
        };
    }

    private Region divisoria() {
        Region linha = new Region();
        linha.getStyleClass().add("divider");
        return linha;
    }

    private VBox painel(String texto) {
        VBox painel = new VBox(12, AtlasVisuals.rotulo(texto, "card-title"));
        painel.getStyleClass().add("panel");
        return painel;
    }

    private VBox painelItens(Pedido pedido) {
        VBox painel = painel("Itens solicitados");
        GridPane tabela = new GridPane();
        tabela.setHgap(8);
        tabela.setVgap(12);
        for (int percentual : new int[] {44, 11, 20, 25}) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(percentual);
            tabela.getColumnConstraints().add(coluna);
        }
        adicionarLinha(tabela, 0, "table-heading", "Produto", "Qtd.", "Unitário", "Subtotal");
        int linha = 1;
        for (Pedido.Item item : pedido.getItens()) {
            tabela.add(divisoria(), 0, linha++, 4, 1);
            adicionarLinha(tabela, linha++, "small-strong", item.produto(), Integer.toString(item.quantidade()),
                AtlasVisuals.moeda(item.unitario()), AtlasVisuals.moeda(item.subtotal()));
        }
        painel.getChildren().add(tabela);
        return painel;
    }

    private void adicionarLinha(GridPane tabela, int linha, String classe, String... valores) {
        for (int coluna = 0; coluna < valores.length; coluna++) {
            Label celula = AtlasVisuals.rotulo(valores[coluna], classe);
            celula.setWrapText(true);
            tabela.add(celula, coluna, linha);
        }
    }
}
