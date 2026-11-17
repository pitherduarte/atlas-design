package atlas.interfacegrafica;

import atlas.dados.PedidosExemplo;
import atlas.modelo.Mesa;
import atlas.modelo.Pedido;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Mapa do salão e filas operacionais acessadas pelo panorama. */
final class SalaoController {
    private static final String LIVRE = "Livre";
    private static final String OCUPADA = "Ocupada";
    private static final String CONTA = "Conta solicitada";
    private static final String LIMPEZA = "Aguardando higienização";

    private record MesaSalao(Mesa mesa, Set<Integer> lugares) { }

    private final List<Pedido> pedidos;
    private final List<MesaSalao> mesas = List.of(
        mesa(1, 2, LIVRE), mesa(2, 4, LIVRE), mesa(3, 2, LIMPEZA),
        mesa(4, 4, CONTA, 1, 2, 4), mesa(5, 4, LIVRE), mesa(6, 2, LIMPEZA),
        mesa(7, 4, OCUPADA, 1, 2), mesa(8, 6, CONTA, 1, 2, 4, 5),
        mesa(9, 4, OCUPADA, 1), mesa(10, 2, LIVRE), mesa(11, 6, OCUPADA, 1, 3, 5),
        mesa(12, 4, LIVRE)
    );

    SalaoController(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    private static MesaSalao mesa(int numero, int capacidade, String status, Integer... lugares) {
        return new MesaSalao(new Mesa(numero, capacidade, status), Set.of(lugares));
    }

    Node visualizar(int inicial) {
        ModuleController pagina = ModuleController.abrir("Salão",
            "Acompanhe a ocupação e selecione uma mesa ou uma cadeira.");
        pagina.categoria("OPERAÇÃO");
        pagina.conteudo().getStyleClass().add("hall-page");

        StackPane area = new StackPane();
        area.setAlignment(Pos.TOP_LEFT);
        VBox detalhes = new VBox(16);
        detalhes.getStyleClass().add("panel");
        detalhes.setPrefWidth(295);
        detalhes.setMinWidth(225);
        BorderPane corpo = new BorderPane(area);
        corpo.setRight(detalhes);
        BorderPane.setMargin(detalhes, new Insets(0, 0, 0, 22));
        pagina.tela().viewportBoundsProperty().addListener((observable, antes, tamanho) -> {
            boolean compacta = tamanho.getWidth() < 970;
            if (compacta && corpo.getRight() != null) {
                corpo.setRight(null);
                corpo.setBottom(detalhes);
                BorderPane.setMargin(detalhes, new Insets(16, 0, 0, 0));
                detalhes.getStyleClass().add("hall-details-compact");
            } else if (!compacta && corpo.getBottom() != null) {
                corpo.setBottom(null);
                corpo.setRight(detalhes);
                BorderPane.setMargin(detalhes, new Insets(0, 0, 0, 22));
                detalhes.getStyleClass().remove("hall-details-compact");
            }
        });

        ToggleGroup formato = new ToggleGroup();
        ToggleButton mapa = alternador("Mapa", formato, "station-chip");
        ToggleButton lista = alternador("Lista", formato, "station-chip");
        mapa.setSelected(true);
        HBox formatos = new HBox(2, mapa, lista);
        formatos.getStyleClass().add("view-modes");

        ToggleGroup situacoes = new ToggleGroup();
        FlowPane filtros = new FlowPane(8, 6);
        filtros.setPrefWrapLength(572);
        List<String> opcoes = List.of("Todas · 12", "Livres", "Ocupadas", CONTA, "Higienização · 02");
        List<String> valores = List.of("", LIVRE, OCUPADA, CONTA, LIMPEZA);
        for (int i = 0; i < opcoes.size(); i++) {
            ToggleButton opcao = alternador(opcoes.get(i), situacoes, "station-chip");
            opcao.setUserData(valores.get(i));
            filtros.getChildren().add(opcao);
        }
        situacoes.getToggles().getFirst().setSelected(true);

        TextField busca = new TextField();
        busca.setPromptText("Buscar número da mesa");
        busca.getStyleClass().add("search-field");
        busca.setPrefWidth(238);

        Consumer<MesaSalao> mostrarMesa = mesa -> mostrarDetalhes(detalhes, mesa, 0);
        Runnable renderizar = () -> {
            String situacao = (String) situacoes.getSelectedToggle().getUserData();
            String numero = busca.getText().strip();
            List<MesaSalao> visiveis = mesas.stream()
                .filter(mesa -> situacao.isEmpty() || situacao.equals(mesa.mesa().getStatus()))
                .filter(mesa -> nome(mesa).endsWith(numero))
                .toList();
            if (mapa.isSelected()) {
                area.getChildren().setAll(mapa(visiveis, detalhes));
            } else {
                area.getChildren().setAll(lista(visiveis, mostrarMesa));
            }
        };
        ativarAlternadores(formato, renderizar);
        ativarAlternadores(situacoes, renderizar);
        busca.textProperty().addListener((observable, antes, agora) -> renderizar.run());
        HBox barra = AtlasVisuals.linha(filtros, busca);
        HBox.setHgrow(filtros, Priority.ALWAYS);
        pagina.conteudo().getChildren().addAll(barra, corpo);
        pagina.acaoNoTitulo(formatos);

        renderizar.run();
        if (inicial == 0) {
            legenda(detalhes);
        } else {
            mostrarMesa.accept(mesas.get(inicial - 1));
        }
        return pagina.tela();
    }

    private Node mapa(List<MesaSalao> visiveis, VBox detalhes) {
        GridPane grade = colunas(4, 0);
        grade.setVgap(9);
        for (int i = 0; i < mesas.size(); i++) {
            MesaSalao mesa = mesas.get(i);
            if (visiveis.contains(mesa)) {
                grade.add(desenharMesa(mesa, detalhes), i % 4, i / 4);
            }
        }
        VBox mapa = new VBox(12, AtlasVisuals.rotulo("SALÃO PRINCIPAL", "table-heading"),
            grade, AtlasVisuals.rotulo("Entrada", "table-heading"));
        mapa.getStyleClass().add("hall-map");
        if (visiveis.isEmpty()) {
            mapa.getChildren().set(1, AtlasVisuals.rotulo("Nenhuma mesa encontrada", "page-subtitle"));
        }
        return mapa;
    }

    private Node desenharMesa(MesaSalao mesa, VBox detalhes) {
        int capacidade = mesa.mesa().getCapacidade();
        boolean pequena = capacidade == 2;
        HBox cima = new HBox(25, assento(mesa, 1, detalhes));
        HBox baixo = new HBox(25);
        HBox centro = new HBox(4);
        cima.setAlignment(Pos.CENTER);
        baixo.setAlignment(Pos.CENTER);
        centro.setAlignment(Pos.CENTER);

        Button tampo = AtlasVisuals.botao("", "table-top", () -> mostrarDetalhes(detalhes, mesa, 0));
        tampo.setAccessibleText(nome(mesa));
        VBox identificacao = new VBox(2, AtlasVisuals.rotulo(nome(mesa), "small-strong"),
            AtlasVisuals.rotulo(ocupacao(mesa), "seat-count"));
        identificacao.setAlignment(Pos.CENTER);
        tampo.setGraphic(identificacao);
        if (pequena) {
            tampo.getStyleClass().add("round-table-top");
            baixo.getChildren().add(assento(mesa, 2, detalhes));
        } else {
            cima.getChildren().add(assento(mesa, 2, detalhes));
            baixo.getChildren().addAll(assento(mesa, capacidade == 6 ? 5 : 3, detalhes), assento(mesa, 4, detalhes));
        }
        if (capacidade == 6) {
            centro.getChildren().addAll(assento(mesa, 6, detalhes), tampo, assento(mesa, 3, detalhes));
        } else {
            centro.getChildren().add(tampo);
        }

        VBox bloco = new VBox(3, cima, centro, baixo, situacaoMesa(mesa.mesa().getStatus()));
        bloco.getStyleClass().add("map-table");
        bloco.setAlignment(Pos.TOP_CENTER);
        return bloco;
    }

    private Button assento(MesaSalao mesa, int numero, VBox detalhes) {
        Button lugar = new Button(String.format("%02d", numero));
        lugar.getStyleClass().add("seat");
        lugar.setOnAction(evento -> {
            if (detalhes.getUserData() instanceof Button anterior) {
                anterior.getStyleClass().remove("selected-seat");
            }
            lugar.getStyleClass().add("selected-seat");
            detalhes.setUserData(lugar);
            mostrarDetalhes(detalhes, mesa, numero);
        });
        lugar.setAccessibleText(nome(mesa) + ", cadeira " + numero
            + (mesa.lugares().contains(numero) ? ", ocupada" : ", vazia"));
        if (mesa.lugares().contains(numero)) {
            lugar.getStyleClass().add("occupied-seat");
        }
        return lugar;
    }

    private AtlasTabela lista(List<MesaSalao> visiveis, Consumer<MesaSalao> abrir) {
        AtlasTabela tabela = new AtlasTabela(new double[] {24, 21, 33, 22}, "Mesa", "Ocupação", "Situação", "Ação");
        for (MesaSalao mesa : visiveis) {
            tabela.adicionar(forte(nome(mesa)), suave(ocupacao(mesa)), situacaoMesa(mesa.mesa().getStatus()),
                AtlasVisuals.botao("Ver mesa", "secondary-button", () -> abrir.accept(mesa)));
        }
        if (visiveis.isEmpty()) {
            tabela.vazio("Nenhuma mesa encontrada");
        }
        return tabela;
    }

    private void legenda(VBox detalhes) {
        Label texto = AtlasVisuals.rotulo("Selecione o tampo para abrir a mesa ou clique direto em uma cadeira.", "small-muted");
        texto.setWrapText(true);
        detalhes.getChildren().setAll(AtlasVisuals.rotulo("Mesa e assentos", "section-title"), texto, linha(),
            forte("Como ler o mapa"), legendaAssento("Cadeira vazia", ""),
            legendaAssento("Cadeira ocupada", "occupied-seat"),
            legendaAssento("Cadeira selecionada", "selected-seat"), linha(), forte("Estados da mesa"),
            new FlowPane(9, 8, situacaoMesa(LIVRE), situacaoMesa(OCUPADA),
                situacaoMesa(CONTA), situacaoMesa(LIMPEZA)));
    }

    private Node legendaAssento(String texto, String classe) {
        Label amostra = AtlasVisuals.rotulo("", "seat");
        if (!classe.isBlank()) {
            amostra.getStyleClass().add(classe);
        }
        HBox linha = new HBox(12, amostra, AtlasVisuals.rotulo(texto, "small-strong"));
        linha.setAlignment(Pos.CENTER_LEFT);
        return linha;
    }

    private void mostrarDetalhes(VBox detalhes, MesaSalao mesa, int cadeira) {
        if (cadeira == 0 && detalhes.getUserData() instanceof Button anterior) {
            anterior.getStyleClass().remove("selected-seat");
            detalhes.setUserData(null);
        }
        detalhes.getChildren().setAll(AtlasVisuals.rotulo(nome(mesa), "section-title"),
            situacaoMesa(mesa.mesa().getStatus()), suave(ocupacao(mesa)), linha());
        if (cadeira > 0) {
            detalhes.getChildren().add(forte("Cadeira " + String.format("%02d", cadeira)
                + " · " + (mesa.lugares().contains(cadeira) ? "Ocupada" : "Vazia")));
        }
        detalhes.getChildren().add(forte("Pedidos vinculados"));
        List<Pedido> vinculados = pedidos.stream()
            .filter(pedido -> pedido.getData().equals(PedidosExemplo.DATA))
            .filter(pedido -> pedido.getTipo().equals(nome(mesa))).toList();
        if (vinculados.isEmpty()) {
            detalhes.getChildren().add(suave("Nenhum pedido vinculado nesta demonstração."));
        }
        for (Pedido pedido : vinculados) {
            detalhes.getChildren().add(AtlasVisuals.linha(forte(pedido.getCodigo()), PedidosController.badge(pedido.getStatus())));
        }
    }

    Node atendimento(Runnable voltar, IntConsumer abrirMesa) {
        ModuleController pagina = fila("Solicitações de atendimento",
            "Acompanhe as solicitações abertas durante o atendimento no salão.", voltar);
        AtlasTabela tabela = new AtlasTabela(new double[] {16, 15, 22, 31, 16},
            "Mesa", "Horário", "Responsável", "Solicitação", "Ação");
        tabela.adicionar(forte("Mesa 07"), suave("12:31"), suave("Igor Santos"),
            suave("Solicitou atendimento"), verMesa(7, abrirMesa));
        tabela.adicionar(forte("Mesa 09"), suave("12:33"), suave("Lucas Melo"),
            suave("Aguarda retorno à mesa"), verMesa(9, abrirMesa));
        pagina.conteudo().getChildren().addAll(AtlasVisuals.rotulo("2 solicitações pendentes", "card-title"), tabela);
        return pagina.tela();
    }

    Node financeiro(Runnable voltar, IntConsumer abrirMesa, Runnable abrirRelatorios) {
        ModuleController pagina = fila("Contas solicitadas",
            "Consulte as contas solicitadas e os valores que aguardam fechamento.", voltar);
        AtlasTabela tabela = new AtlasTabela(new double[] {16, 15, 22, 31, 16},
            "Mesa", "Comanda", "Solicitada às", "Total parcial", "Ação");
        tabela.adicionar(forte("Mesa 04"), suave("CMD-004"), suave("12:34"), suave("R$ 122,00"), verMesa(4, abrirMesa));
        tabela.adicionar(forte("Mesa 08"), suave("CMD-008"), suave("12:32"), suave("R$ 18,00"), verMesa(8, abrirMesa));
        pagina.conteudo().getChildren().addAll(AtlasVisuals.rotulo("2 comandas · R$ 140,00 em aberto", "card-title"),
            tabela, AtlasVisuals.botao("Ver relatórios financeiros", "secondary-button", abrirRelatorios));
        return pagina.tela();
    }

    Node higienizacao(Runnable voltar, IntConsumer abrirMesa) {
        ModuleController pagina = fila("Mesas aguardando limpeza",
            "Confira quais mesas aguardam limpeza antes de liberar um novo atendimento.", voltar);
        AtlasTabela tabela = new AtlasTabela(new double[] {21, 28, 35, 16}, "Mesa", "Desde", "Situação", "Ação");
        tabela.adicionar(forte("Mesa 03"), suave("12:30"), situacaoMesa(LIMPEZA), verMesa(3, abrirMesa));
        tabela.adicionar(forte("Mesa 06"), suave("12:32"), situacaoMesa(LIMPEZA), verMesa(6, abrirMesa));
        pagina.conteudo().getChildren().addAll(AtlasVisuals.rotulo("2 mesas aguardando limpeza", "card-title"), tabela);
        return pagina.tela();
    }

    Node producao(Runnable voltar, Consumer<String> abrirEstacao) {
        ModuleController pagina = fila("Produção por estação",
            "Acompanhe a fila das estações sem sair da operação de pedidos.", voltar);
        GridPane grade = colunas(3, 18);
        List<String> estacoes = List.of("Cozinha", "Bar", "Padaria");
        List<String> icones = List.of("production-kitchen", "production-bar", "production-bakery");
        List<String> tons = List.of("tone-blue", "tone-violet", "tone-amber");
        for (int i = 0; i < estacoes.size(); i++) {
            String estacao = estacoes.get(i);
            List<Pedido> fila = pedidos.stream().filter(pedido -> pedido.getData().equals(PedidosExemplo.DATA))
                .filter(Pedido::estaEmAndamento).filter(pedido -> pedido.getEstacao().equals(estacao)).toList();
            StackPane icone = new StackPane(AtlasVisuals.icone(icones.get(i), 20));
            icone.getStyleClass().addAll("card-icon", tons.get(i));
            HBox titulo = new HBox(10, icone, AtlasVisuals.rotulo(estacao, "card-title"));
            titulo.setAlignment(Pos.CENTER_LEFT);
            VBox painel = new VBox(12, AtlasVisuals.linha(titulo,
                suave(fila.size() + (fila.size() == 1 ? " pedido" : " pedidos"))), linha());
            painel.getStyleClass().add("panel");
            painel.setMinHeight(360);
            for (Pedido pedido : fila) {
                painel.getChildren().addAll(AtlasVisuals.linha(forte(pedido.getCodigo() + " · " + pedido.getTipo()),
                    PedidosController.badge(pedido.getStatus())), linha());
            }
            Region espaco = new Region();
            VBox.setVgrow(espaco, Priority.ALWAYS);
            painel.getChildren().addAll(espaco, AtlasVisuals.botao("Abrir fila de " + estacao.toLowerCase(AtlasVisuals.BRASIL),
                "primary-button", () -> abrirEstacao.accept(estacao)));
            grade.add(painel, i, 0);
        }
        pagina.conteudo().getChildren().add(grade);
        return pagina.tela();
    }

    private ModuleController fila(String titulo, String subtitulo, Runnable voltar) {
        ModuleController pagina = ModuleController.abrir(titulo, subtitulo);
        pagina.conteudo().getChildren().addFirst(AtlasVisuals.botao("Voltar ao início", "secondary-button", voltar));
        return pagina;
    }

    private String nome(MesaSalao mesa) { return String.format("Mesa %02d", mesa.mesa().getNumero()); }
    private String ocupacao(MesaSalao mesa) { return mesa.lugares().size() + " / " + mesa.mesa().getCapacidade() + " clientes"; }
    private Label forte(String texto) { return AtlasVisuals.rotulo(texto, "small-strong"); }
    private Label suave(String texto) {
        Label rotulo = AtlasVisuals.rotulo(texto, "small-muted");
        rotulo.setWrapText(true);
        return rotulo;
    }

    private Button verMesa(int numero, IntConsumer abrir) {
        return AtlasVisuals.botao("Ver mesa", "secondary-button", () -> abrir.accept(numero));
    }

    private Label situacaoMesa(String estado) {
        String classe = switch (estado) {
            case OCUPADA -> "status-ready";
            case CONTA -> "status-preparing";
            case LIMPEZA -> "status-cleaning";
            default -> "status-neutral";
        };
        return AtlasVisuals.rotulo(estado, "status-badge", classe, "table-state");
    }

    private ToggleButton alternador(String texto, ToggleGroup grupo, String estilo) {
        ToggleButton botao = new ToggleButton(texto);
        botao.getStyleClass().add(estilo);
        botao.setToggleGroup(grupo);
        return botao;
    }

    private void ativarAlternadores(ToggleGroup grupo, Runnable atualizar) {
        grupo.getToggles().forEach(item -> {
            ToggleButton botao = (ToggleButton) item;
            botao.setOnAction(event -> {
                botao.setSelected(true);
                atualizar.run();
            });
        });
    }

    private Region linha() {
        Region linha = new Region();
        linha.getStyleClass().add("divider");
        return linha;
    }

    private GridPane colunas(int quantidade, double espaco) {
        GridPane grade = new GridPane();
        grade.setHgap(espaco);
        for (int i = 0; i < quantidade; i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(100.0 / quantidade);
            grade.getColumnConstraints().add(coluna);
        }
        return grade;
    }
}
