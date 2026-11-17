package atlas.interfacegrafica;

import atlas.dados.EquipeExemplo;
import atlas.dados.GestaoExemplo;
import atlas.dados.PedidosExemplo;
import atlas.modelo.Funcionario;
import atlas.modelo.Pedido;
import atlas.modelo.Produto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Telas de gestão com dados de demonstração mantidos durante a sessão. */
final class GestaoController {
    private static final List<String> CATEGORIAS = List.of("Carnes", "Massas", "Peixes e frutos do mar",
        "Sanduíches", "Sobremesas", "Bebidas");
    private static final List<String> CARGOS = List.of("Garçom", "Caixa", "Cozinheiro", "Barman",
        "Padaria", "Limpeza", "Cumim", "Gerente", "Maître", "Sommelier");
    private static final LocalTime HORARIO_DEMO = LocalTime.of(12, 35);
    private final List<Produto> produtos = new ArrayList<>(GestaoExemplo.produtos());
    private final List<Funcionario> equipe = new ArrayList<>(EquipeExemplo.criar());
    private final List<GestaoExemplo.Comanda> comandas = GestaoExemplo.comandas();
    private final List<Pedido> pedidos;

    GestaoController(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    Node menu() {
        ModuleController pagina = ModuleController.abrir("Menu",
            "31 pratos do cardápio + 10 bebidas ilustrativas. Estoque ilustrativo.");
        StackPane area = new StackPane();
        VBox lista = new VBox(22);
        area.getChildren().add(lista);
        pagina.conteudo().getChildren().add(area);
        Button novo = AtlasVisuals.botao("Novo produto", "primary-button",
            () -> editarProduto(pagina, area, lista, null));
        pagina.acaoNoTitulo(novo);

        ToggleGroup grupo = new ToggleGroup();
        FlowPane categorias = new FlowPane(7, 7);
        categorias.setPrefWrapLength(845);
        List<ToggleButton> botoes = new ArrayList<>();
        List<String> todas = new ArrayList<>(List.of("Todas"));
        todas.addAll(CATEGORIAS);
        for (String nome : todas) {
            ToggleButton botao = alternador(nome, grupo, "station-chip");
            botao.setUserData(nome);
            botoes.add(botao);
            categorias.getChildren().add(botao);
        }
        botoes.getFirst().setSelected(true);
        TextField busca = campo("", "Buscar produto");
        HBox campoBusca = new HBox(8, AtlasVisuals.icone("search-muted", 16), busca);
        campoBusca.getStyleClass().add("search-box");
        campoBusca.setAlignment(Pos.CENTER_LEFT);
        campoBusca.setPrefWidth(245);
        campoBusca.setMinWidth(154);
        HBox.setHgrow(busca, Priority.ALWAYS);
        HBox barra = AtlasVisuals.linha(categorias, campoBusca);
        HBox.setHgrow(categorias, Priority.ALWAYS);

        AtlasTabela tabela = new AtlasTabela(new double[] {29, 20, 14, 22, 15},
            "Produto", "Categoria", "Preço unitário", "Disponibilidade", "Ações");
        Label total = AtlasVisuals.rotulo("", "small-strong", "station-label");
        Runnable atualizar = () -> {
            String categoria = (String) grupo.getSelectedToggle().getUserData();
            String texto = AtlasVisuals.normalizarBusca(busca.getText());
            List<Produto> visiveis = produtos.stream()
                .filter(produto -> "Todas".equals(categoria) || produto.getCategoria().equals(categoria))
                .filter(produto -> AtlasVisuals.normalizarBusca(produto.getNome()).contains(texto)).toList();
            tabela.limpar();
            botoes.forEach(botao -> botao.setText(botao.getUserData() + "  "
                + produtos.stream().filter(produto -> "Todas".equals(botao.getUserData())
                    || produto.getCategoria().equals(botao.getUserData())).count()));
            for (Produto produto : visiveis) {
                tabela.adicionar(forte(produto.getNome()), normal(produto.getCategoria()), forte(dinheiro(produto.getPreco())),
                    AtlasVisuals.rotulo(produto.isDisponivel() ? "•  Disponível" : "Indisponível", "status-badge",
                        produto.isDisponivel() ? "status-delivered" : "status-neutral"),
                    AtlasVisuals.botao("Editar", "secondary-button",
                        () -> editarProduto(pagina, area, lista, produto)));
            }
            if (visiveis.isEmpty()) {
                tabela.vazio("Nenhum produto encontrado");
            }
            total.setText(visiveis.size() + (visiveis.size() == 1 ? " produto" : " produtos"));
        };
        ativar(grupo, atualizar);
        busca.textProperty().addListener((observable, anterior, atual) -> atualizar.run());
        ScrollPane rolagem = rolagem(tabela, 514);
        VBox.setMargin(rolagem, new javafx.geometry.Insets(18, 0, 0, 0));
        lista.getChildren().addAll(barra, rolagem, total);
        atualizar.run();
        lista.visibleProperty().addListener((observable, antes, visivel) -> {
            if (visivel) {
                atualizar.run();
            }
        });
        return pagina.tela();
    }

    private void editarProduto(ModuleController pagina, StackPane area, VBox lista, Produto produto) {
        VBox formulario = new VBox(20);
        Label erro = AtlasVisuals.rotulo("", "form-error");
        TextField nome = campo(produto == null ? "" : produto.getNome(), "Nome do produto");
        ComboBox<String> categoria = selecao(CATEGORIAS, produto == null ? CATEGORIAS.getFirst() : produto.getCategoria());
        TextField preco = campo(produto == null ? "" : numero(produto.getPreco()), "0,00");
        ComboBox<String> disponibilidade = selecao(List.of("Disponível", "Indisponível"),
            produto == null || produto.isDisponivel() ? "Disponível" : "Indisponível");
        GridPane campos = grade(2, 28);
        campos.setVgap(22);
        campos.add(campoRotulado("Nome do produto", nome), 0, 0);
        campos.add(campoRotulado("Categoria", categoria), 1, 0);
        campos.add(campoRotulado("Preço", preco), 0, 1);
        campos.add(campoRotulado("Disponibilidade", disponibilidade), 1, 1);
        Runnable voltar = () -> {
            area.getChildren().setAll(lista);
            lista.setVisible(true);
            pagina.exibirCabecalho(true);
        };
        Button salvar = AtlasVisuals.botao("Salvar produto", "primary-button", () -> {
            try {
                if (nome.getText().isBlank()) {
                    throw new IllegalArgumentException("Informe o nome do produto.");
                }
                double valor = lerDecimal(preco.getText(), "Preço", false);
                if (produto == null) {
                    Produto novo = new Produto(nome.getText().strip(), categoria.getValue(), valor);
                    novo.setDisponivel(disponibilidade.getValue().equals("Disponível"));
                    produtos.add(novo);
                } else {
                    produto.setNome(nome.getText().strip());
                    produto.setCategoria(categoria.getValue());
                    produto.setPreco(valor);
                    produto.setDisponivel(disponibilidade.getValue().equals("Disponível"));
                }
                voltar.run();
            } catch (IllegalArgumentException exception) {
                erro.setText(exception.getMessage());
            }
        });
        HBox acoes = new HBox(8, AtlasVisuals.botao("Cancelar", "secondary-button", voltar), salvar);
        if (produto != null) {
            Button excluir = AtlasVisuals.botao("Excluir produto", "secondary-button", () -> {
                Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Excluir " + produto.getNome() + " do cardápio desta sessão?", ButtonType.CANCEL, ButtonType.OK);
                confirmacao.initOwner(area.getScene().getWindow());
                confirmacao.setHeaderText("Confirmar exclusão");
                confirmacao.showAndWait().filter(ButtonType.OK::equals).ifPresent(resposta -> {
                    produtos.remove(produto);
                    voltar.run();
                });
            });
            acoes.getChildren().add(excluir);
        }
        VBox painel = painel("Dados do produto", suave("Preencha os campos para salvar o cadastro."),
            campos, erro, linha(), acoes);
        painel.setMaxWidth(750);
        formulario.getChildren().addAll(
            new VBox(5, AtlasVisuals.rotulo("CARDÁPIO / " + (produto == null ? "CADASTRO" : "EDIÇÃO"), "kicker"),
                AtlasVisuals.rotulo(produto == null ? "Novo produto" : "Editar produto", "page-title"),
                normal(produto == null ? "Cadastre um produto no catálogo do restaurante."
                    : "Atualize os dados do produto no catálogo do restaurante.")),
            AtlasVisuals.botao("Voltar ao menu", "secondary-button", voltar), painel);
        lista.setVisible(false);
        pagina.exibirCabecalho(false);
        area.getChildren().setAll(formulario);
    }

    Node funcionarios() {
        ModuleController pagina = ModuleController.abrir("Funcionários",
            "Organize a equipe por área e acompanhe a escala do turno demonstrativo.");
        VBox lista = new VBox(18);
        StackPane area = new StackPane(lista);
        pagina.conteudo().getChildren().add(area);
        Button novo = AtlasVisuals.botao("Novo funcionário", "primary-button",
            () -> editarFuncionario(pagina, area, lista, null));
        pagina.acaoNoTitulo(novo);
        ToggleGroup cargos = new ToggleGroup();
        FlowPane filtrosCargo = new FlowPane(8, 8);
        List<String> opcoesCargo = new ArrayList<>(List.of("Todos"));
        opcoesCargo.addAll(CARGOS);
        List<ToggleButton> botoesCargo = new ArrayList<>();
        for (String cargo : opcoesCargo) {
            ToggleButton botao = alternador(cargo, cargos, "filter-button");
            botao.setUserData(cargo);
            filtrosCargo.getChildren().add(botao);
            botoesCargo.add(botao);
        }
        botoesCargo.getFirst().setSelected(true);
        ToggleGroup situacoes = new ToggleGroup();
        FlowPane filtrosSituacao = new FlowPane(8, 8);
        for (String situacao : List.of("Todas as situações", "Ativos", "Trabalhando agora", "Fora do expediente", "Inativo")) {
            ToggleButton botao = alternador(situacao, situacoes, "station-chip");
            botao.setUserData(situacao);
            filtrosSituacao.getChildren().add(botao);
        }
        situacoes.getToggles().getFirst().setSelected(true);
        AtlasTabela tabela = new AtlasTabela(new double[] {16, 14, 10, 10, 14, 10, 18, 8},
            "Nome", "Cargo", "Salário", "Gorjetas (%)", "Horário de trabalho", "Situação", "Estado do expediente", "Ações");
        tabela.getStyleClass().add("staff-table");
        Label total = AtlasVisuals.rotulo("", "small-strong", "station-label");
        HBox rodape = AtlasVisuals.linha(total, suave("Role para ver mais funcionários"));
        rodape.getStyleClass().add("orders-footer");
        Runnable atualizar = () -> {
            String cargo = (String) cargos.getSelectedToggle().getUserData();
            String situacao = (String) situacoes.getSelectedToggle().getUserData();
            List<Funcionario> visiveis = equipe.stream()
                .filter(funcionario -> cargo.equals("Todos") || cargo.equals(funcionario.getCargo()))
                .filter(funcionario -> correspondeSituacao(funcionario, situacao)).toList();
            tabela.limpar();
            for (ToggleButton botao : botoesCargo) {
                long quantidade = equipe.stream()
                    .filter(funcionario -> botao.getUserData().equals("Todos") || botao.getUserData().equals(funcionario.getCargo())).count();
                botao.setGraphic(AtlasVisuals.rotulo(String.format("%02d", quantidade), "filter-count"));
                botao.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
            }
            for (Funcionario funcionario : visiveis) {
                Label situacaoVinculo = AtlasVisuals.rotulo(funcionario.isAtivo() ? "•  Ativo" : "•  Inativo",
                    "status-badge", funcionario.isAtivo() ? "status-delivered" : "status-neutral");
                String expediente = funcionario.getExpediente(HORARIO_DEMO);
                Label estado = AtlasVisuals.rotulo(expediente, "status-badge",
                    expediente.equals("Trabalhando agora") ? "status-ready" : "status-neutral");
                tabela.adicionar(forte(funcionario.getNome()), suave(funcionario.getCargo()),
                    suave(dinheiro(funcionario.getSalario())), forte(numero(funcionario.getGorjetas()) + "%"),
                    suave(funcionario.getHorario()), situacaoVinculo, estado,
                    fichaBotao(() -> ficha(pagina, area, lista, funcionario)));
            }
            if (visiveis.isEmpty()) {
                tabela.vazio("Nenhum funcionário para os filtros selecionados");
            }
            total.setText(visiveis.size() + (visiveis.size() == 1 ? " funcionário" : " funcionários"));
        };
        ativar(cargos, atualizar);
        ativar(situacoes, atualizar);
        lista.getChildren().addAll(filtrosCargo, filtrosSituacao, rolagem(tabela, 458), rodape);
        lista.visibleProperty().addListener((observable, antes, visivel) -> {
            if (visivel) {
                atualizar.run();
            }
        });
        atualizar.run();
        return pagina.tela();
    }

    private boolean correspondeSituacao(Funcionario funcionario, String situacao) {
        return switch (situacao) {
            case "Ativos" -> funcionario.isAtivo();
            case "Inativo" -> !funcionario.isAtivo();
            case "Trabalhando agora", "Fora do expediente" -> funcionario.getExpediente(HORARIO_DEMO).equals(situacao);
            default -> true;
        };
    }

    private Button fichaBotao(Runnable acao) {
        Button botao = AtlasVisuals.botao("Ver ficha", "text-button", acao);
        botao.setGraphic(AtlasVisuals.icone("arrow", 13));
        botao.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
        return botao;
    }

    private void ficha(ModuleController pagina, StackPane area, VBox lista, Funcionario funcionario) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(area.getScene().getWindow());
        dialogo.setTitle("Ficha · " + funcionario.getNome());
        dialogo.getDialogPane().getStylesheets().add(getClass().getResource("/recursos/estilos/Atlas.css").toExternalForm());
        ButtonType editar = new ButtonType("Editar");
        ButtonType fechar = new ButtonType("Fechar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getDialogPane().getButtonTypes().setAll(editar, fechar);
        GridPane dados = grade(2, 28);
        dados.setVgap(22);
        dados.add(campoRotulado("Salário mensal", forte(dinheiro(funcionario.getSalario()))), 0, 0);
        dados.add(campoRotulado("Participação nas gorjetas", forte(numero(funcionario.getGorjetas()) + "%")), 1, 0);
        dados.add(campoRotulado("Horário de trabalho", forte(funcionario.getHorario())), 0, 1);
        dados.add(campoRotulado("Situação do vínculo", forte(funcionario.isAtivo() ? "Ativo" : "Inativo")), 1, 1);
        dados.add(campoRotulado("Estado do expediente", forte(funcionario.getExpediente(HORARIO_DEMO))), 0, 2, 2, 1);
        VBox conteudo = new VBox(20, AtlasVisuals.rotulo(funcionario.getNome(), "section-title"),
            normal(funcionario.getCargo()), linha(), dados, linha(), suave("Participação expressa em porcentagem."));
        conteudo.setPrefWidth(536);
        dialogo.getDialogPane().setContent(conteudo);
        dialogo.showAndWait().filter(editar::equals).ifPresent(resultado -> editarFuncionario(pagina, area, lista, funcionario));
    }

    private void editarFuncionario(ModuleController pagina, StackPane area, VBox lista, Funcionario funcionario) {
        TextField nome = campo(funcionario == null ? "" : funcionario.getNome(), "Nome completo");
        ComboBox<String> cargo = selecao(CARGOS, funcionario == null ? "Garçom" : funcionario.getCargo());
        TextField salario = campo(funcionario == null ? "" : numero(funcionario.getSalario()), "0,00");
        TextField gorjetas = campo(funcionario == null ? "0,0" : numero(funcionario.getGorjetas()), "0,0");
        TextField horario = campo(funcionario == null ? "10:00–18:00" : funcionario.getHorario(), "10:00–18:00");
        TextField email = campo(funcionario == null ? "" : funcionario.getEmail(), "nome@restaurante.example");
        ComboBox<String> situacao = selecao(List.of("Ativo", "Inativo"),
            funcionario == null || funcionario.isAtivo() ? "Ativo" : "Inativo");
        Label erro = AtlasVisuals.rotulo("", "form-error");
        GridPane campos = grade(2, 24);
        campos.setVgap(22);
        campos.add(campoRotulado("Nome completo", nome), 0, 0);
        campos.add(campoRotulado("Cargo", cargo), 1, 0);
        campos.add(campoRotulado("Salário mensal", salario), 0, 1);
        campos.add(campoRotulado("Participação nas gorjetas (%)", gorjetas), 1, 1);
        campos.add(campoRotulado("Horário de trabalho", horario), 0, 2);
        campos.add(campoRotulado("Situação do vínculo", situacao), 1, 2);
        Runnable voltar = () -> {
            area.getChildren().setAll(lista);
            lista.setVisible(true);
            pagina.exibirCabecalho(true);
        };
        Button salvar = AtlasVisuals.botao("Salvar funcionário", "primary-button", () -> {
            try {
                if (nome.getText().isBlank()) {
                    throw new IllegalArgumentException("Informe o nome completo.");
                }
                double valorSalario = lerDecimal(salario.getText(), "Salário", false);
                double percentual = lerDecimal(gorjetas.getText(), "Gorjetas", true);
                if (percentual > 100) {
                    throw new IllegalArgumentException("Informe gorjetas de 0 a 100%.");
                }
                String escala = horario.getText().strip().replace("-", "–");
                if (!escala.matches("\\d{2}:\\d{2}–\\d{2}:\\d{2}")) {
                    throw new IllegalArgumentException("Informe o horário no formato 10:00–18:00.");
                }
                String[] partes = escala.split("–");
                LocalTime inicio;
                LocalTime fim;
                try {
                    inicio = LocalTime.parse(partes[0]);
                    fim = LocalTime.parse(partes[1]);
                } catch (java.time.format.DateTimeParseException exception) {
                    throw new IllegalArgumentException("Informe horários entre 00:00 e 23:59.");
                }
                if (inicio.equals(fim)) {
                    throw new IllegalArgumentException("Informe horários diferentes para início e fim do expediente.");
                }
                String acesso = email.getText().strip();
                if (!acesso.isEmpty() && !acesso.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
                    throw new IllegalArgumentException("Confira o e-mail de acesso.");
                }
                Funcionario alvo = funcionario == null ? new Funcionario("", "") : funcionario;
                alvo.atualizar(nome.getText().strip(), cargo.getValue(), valorSalario, percentual,
                    escala, acesso, situacao.getValue().equals("Ativo"));
                if (funcionario == null) {
                    equipe.add(alvo);
                }
                voltar.run();
            } catch (IllegalArgumentException exception) {
                erro.setText(exception.getMessage());
            }
        });
        VBox painel = painel("Dados e condições de trabalho", campos, erro, linha(),
            new HBox(8, AtlasVisuals.botao("Cancelar", "secondary-button", voltar), salvar));
        VBox acesso = painel("Acesso e permissões", campoRotulado("E-mail de acesso", email), linha(),
            suave("O perfil acompanha o cargo selecionado na demonstração."));
        HBox.setHgrow(painel, Priority.ALWAYS);
        acesso.setPrefWidth(340);
        acesso.setMaxHeight(Region.USE_PREF_SIZE);
        HBox secoes = new HBox(20, painel, acesso);
        VBox formulario = new VBox(20, AtlasVisuals.botao("Voltar a funcionários", "secondary-button", voltar),
            AtlasVisuals.rotulo(funcionario == null ? "Novo funcionário" : "Editar funcionário", "page-title"), secoes);
        lista.setVisible(false);
        pagina.exibirCabecalho(false);
        area.getChildren().setAll(formulario);
    }

    Node relatorios() {
        ModuleController pagina = ModuleController.abrir("Relatórios financeiros",
            "Receita demonstrativa baseada em 33 comandas efetivamente fechadas.");
        ToggleGroup grupo = new ToggleGroup();
        ToggleButton hoje = alternador("Hoje", grupo, "station-chip");
        ToggleButton semana = alternador("Últimos 7 dias", grupo, "station-chip");
        hoje.setSelected(true);
        Label periodo = forte("15 nov. 2026");
        periodo.setGraphic(AtlasVisuals.icone("calendar", 17));
        periodo.setGraphicTextGap(10);
        BigDecimal vendido = comandas.stream().map(GestaoExemplo.Comanda::total).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ticket = vendido.divide(BigDecimal.valueOf(comandas.size()), 2, RoundingMode.HALF_UP);
        GridPane indicadores = grade(4, 0);
        String[] rotulos = {"Total vendido", "Quantidade de vendas", "Ticket médio", "Comandas fechadas"};
        String[] valores = {AtlasVisuals.moeda(vendido), Integer.toString(comandas.size()),
            AtlasVisuals.moeda(ticket), Integer.toString(comandas.size())};
        for (int i = 0; i < rotulos.length; i++) {
            Label valor = AtlasVisuals.rotulo(valores[i], "metric-number");
            if (i == 0) {
                valor.getStyleClass().add("blue-number");
            }
            VBox indicador = new VBox(6, forte(rotulos[i]), valor);
            indicador.getStyleClass().add(i == 0 ? "report-metric" : "report-metric-separator");
            indicadores.add(indicador, i, 0);
        }
        VBox resumo = painel("Resumo de vendas", linha(), indicadores);
        resumo.getStyleClass().add("report-summary");
        AtlasTabela tabela = new AtlasTabela(new double[] {22, 24, 33, 21}, "Comanda", "Mesa", "Fechada às", "Total");
        tabela.getStyleClass().add("report-table");
        for (GestaoExemplo.Comanda comanda : comandas) {
            tabela.adicionar(forte(comanda.codigo()), normal(comanda.mesa()), normal(comanda.horario()),
                forte(AtlasVisuals.moeda(comanda.total())));
        }
        Label contexto = suave("15 de novembro");
        ativar(grupo, () -> {
            boolean seteDias = semana.isSelected();
            periodo.setText(seteDias ? "9–15 nov. 2026" : "15 nov. 2026");
            contexto.setText(seteDias ? "Amostra disponível · 15 nov." : "15 de novembro");
        });
        VBox lista = new VBox(16, AtlasVisuals.linha(AtlasVisuals.rotulo("Comandas fechadas", "card-title"), contexto),
            rolagem(tabela, 272));
        lista.getStyleClass().add("panel");
        pagina.conteudo().getChildren().addAll(AtlasVisuals.linha(new HBox(8, hoje, semana), periodo), resumo, lista);
        return pagina.tela();
    }

    private record Registro(String operacao, String referencia, String responsavel, LocalDateTime instante, String tipo) {
        Registro(String operacao, String referencia, String responsavel, String hora, String tipo) {
            this(operacao, referencia, responsavel, PedidosExemplo.DATA.atTime(LocalTime.parse(hora)), tipo);
        }
        String data() { return instante.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
        String hora() { return instante.format(DateTimeFormatter.ofPattern("HH:mm")); }
    }

    Node historico() {
        ModuleController pagina = ModuleController.abrir("Histórico",
            "Consulte registros que afetam pedidos, comandas e o atendimento.");
        List<Registro> registros = new ArrayList<>(List.of(
            new Registro("Conta solicitada", "Mesa 04 · Comanda CMD-004", "Joana Lima", "12:34", "Contas solicitadas"),
            new Registro("Comanda fechada", "Mesa 06 · Comanda CMD-006", "Sofia Almeida", "12:32", "Comandas fechadas"),
            new Registro("Conta solicitada", "Mesa 08 · Comanda CMD-008", "Lucas Melo", "12:32", "Contas solicitadas"),
            new Registro("Comanda fechada", "Mesa 03 · Comanda CMD-003", "Sofia Almeida", "12:30", "Comandas fechadas"),
            new Registro("Situação do pedido alterada", "PED-104 · Cozinha", "Marina Costa", "12:30", "Alterações de pedido"),
            new Registro("Comanda fechada", "Mesa 02 · Comanda CMD-002", "Sofia Almeida", "12:17", "Comandas fechadas")
        ));
        for (Pedido pedido : pedidos) {
            for (Pedido.Alteracao alteracao : pedido.getHistorico()) {
                registros.add(new Registro("Situação do pedido alterada", pedido.getCodigo() + " · " + alteracao.status(),
                    "Marina Costa", alteracao.instante(), "Alterações de pedido"));
            }
        }
        registros.sort(Comparator.comparing(Registro::instante).reversed());
        ToggleGroup grupo = new ToggleGroup();
        FlowPane filtros = new FlowPane(8, 8);
        for (String nome : List.of("Todas", "Contas solicitadas", "Comandas fechadas", "Alterações de pedido")) {
            long contagem = registros.stream().filter(registro -> nome.equals("Todas") || registro.tipo().equals(nome)).count();
            ToggleButton botao = alternador(nome, grupo, "filter-button");
            botao.setUserData(nome);
            botao.setGraphic(AtlasVisuals.rotulo(String.format("%02d", contagem), "filter-count"));
            botao.setContentDisplay(javafx.scene.control.ContentDisplay.RIGHT);
            filtros.getChildren().add(botao);
        }
        grupo.getToggles().getFirst().setSelected(true);
        AtlasTabela tabela = new AtlasTabela(new double[] {45, 18, 12, 13, 12},
            "Operação", "Responsável", "Data", "Horário", "Detalhes");
        Runnable atualizar = () -> {
            tabela.limpar();
            String filtro = (String) grupo.getSelectedToggle().getUserData();
            for (Registro registro : registros) {
                if (!filtro.equals("Todas") && !registro.tipo().equals(filtro)) {
                    continue;
                }
                String icone = switch (registro.tipo()) {
                    case "Contas solicitadas" -> "history-bill";
                    case "Comandas fechadas" -> "status-delivered";
                    default -> "orders-active";
                };
                StackPane simbolo = new StackPane(AtlasVisuals.icone(icone, 18));
                simbolo.getStyleClass().addAll("card-icon", registro.tipo().equals("Contas solicitadas") ? "tone-amber"
                    : registro.tipo().equals("Comandas fechadas") ? "tone-green" : "tone-blue");
                HBox identificacao = new HBox(14, simbolo, new VBox(8, forte(registro.operacao()), suave(registro.referencia())));
                identificacao.setAlignment(Pos.CENTER_LEFT);
                tabela.adicionar(identificacao, normal(registro.responsavel()), normal(registro.data()), normal(registro.hora()),
                    AtlasVisuals.botao("Ver detalhes", "secondary-button", () -> {
                        Alert detalhe = new Alert(Alert.AlertType.INFORMATION);
                        detalhe.initOwner(pagina.tela().getScene().getWindow());
                        detalhe.setTitle("Histórico");
                        detalhe.setHeaderText(registro.operacao());
                        detalhe.setContentText(registro.referencia() + "\n" + registro.responsavel() + " · " + registro.data() + " · " + registro.hora());
                        detalhe.showAndWait();
                    }));
            }
        };
        tabela.getStyleClass().add("history-table");
        ativar(grupo, atualizar);
        atualizar.run();
        pagina.conteudo().getChildren().addAll(filtros, tabela);
        return pagina.tela();
    }

    Node configuracoes(Runnable funcionarios) {
        ModuleController pagina = ModuleController.abrir("Configurações",
            "Consulte as preferências do restaurante e os acessos da equipe.");
        pagina.categoria("GESTÃO");
        pagina.conteudo().getStyleClass().add("settings-page");
        GridPane grade = grade(3, 14);
        String[] titulos = {"Informações do restaurante", "Preferências", "Acesso e permissões"};
        String[] descricoes = {"Identificação que aparece na operação.", "Formato das informações operacionais.", "Acesso individual por cargo."};
        String[] icones = {"settings-card", "calendar", "lock"};
        for (int i = 0; i < titulos.length; i++) {
            StackPane icone = new StackPane(AtlasVisuals.icone(icones[i], 21));
            icone.getStyleClass().addAll("card-icon", "tone-blue");
            VBox painel = new VBox(16, icone, new VBox(5, AtlasVisuals.rotulo(titulos[i], "card-title"), suave(descricoes[i])), linha());
            painel.getStyleClass().addAll("panel", "settings-panel");
            switch (i) {
                case 0 -> painel.getChildren().addAll(forte("Nome exibido"), normal("Restaurante"));
                case 1 -> painel.getChildren().addAll(forte("Idioma e formato"), normal("Português (Brasil) · 24 horas"),
                    new HBox(40, forte("Aparência"), normal("Tema claro")));
                case 2 -> painel.getChildren().addAll(forte("Perfis do Atlas Central"), normal("Gerente, Caixa e Cozinha"),
                    AtlasVisuals.botao("Ver funcionários", "secondary-button", funcionarios));
                default -> throw new IllegalStateException();
            }
            grade.add(painel, i, 0);
        }
        VBox.setMargin(grade, new javafx.geometry.Insets(24, 0, 0, 0));
        pagina.conteudo().getChildren().add(grade);
        return pagina.tela();
    }

    private GridPane grade(int quantidade, double espaco) {
        GridPane grade = new GridPane();
        grade.setHgap(espaco);
        for (int i = 0; i < quantidade; i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(100.0 / quantidade);
            grade.getColumnConstraints().add(coluna);
        }
        return grade;
    }

    private ScrollPane rolagem(Node conteudo, double altura) {
        ScrollPane rolagem = new ScrollPane(conteudo);
        rolagem.setFitToWidth(true);
        rolagem.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rolagem.setPrefHeight(altura);
        rolagem.getStyleClass().add("page-scroll");
        return rolagem;
    }

    private ToggleButton alternador(String texto, ToggleGroup grupo, String estilo) {
        ToggleButton botao = new ToggleButton(texto);
        botao.setToggleGroup(grupo);
        botao.getStyleClass().add(estilo);
        return botao;
    }

    private void ativar(ToggleGroup grupo, Runnable atualizar) {
        grupo.getToggles().forEach(item -> {
            ToggleButton botao = (ToggleButton) item;
            botao.setOnAction(event -> {
                botao.setSelected(true);
                atualizar.run();
            });
        });
    }

    private VBox campoRotulado(String titulo, Node campo) { return new VBox(7, forte(titulo), campo); }

    private TextField campo(String valor, String dica) {
        TextField campo = new TextField(valor);
        campo.setPromptText(dica);
        campo.getStyleClass().add("search-field");
        return campo;
    }

    private ComboBox<String> selecao(List<String> valores, String atual) {
        ComboBox<String> selecao = new ComboBox<>(FXCollections.observableArrayList(valores));
        selecao.setValue(atual);
        selecao.setMaxWidth(Double.MAX_VALUE);
        selecao.getStyleClass().add("editor-select");
        return selecao;
    }

    private VBox painel(String titulo, Node... conteudo) {
        VBox painel = new VBox(18, AtlasVisuals.rotulo(titulo, "card-title"));
        painel.getChildren().addAll(conteudo);
        painel.getStyleClass().add("panel");
        return painel;
    }

    private Region linha() {
        Region linha = new Region();
        linha.getStyleClass().add("divider");
        return linha;
    }

    private Label forte(String texto) { return AtlasVisuals.rotulo(texto, "small-strong"); }
    private Label normal(String texto) { return AtlasVisuals.rotulo(texto, "page-subtitle"); }
    private Label suave(String texto) { return AtlasVisuals.rotulo(texto, "small-muted"); }
    private String dinheiro(double valor) { return AtlasVisuals.moeda(BigDecimal.valueOf(valor)); }
    private String numero(double valor) { return String.format(AtlasVisuals.BRASIL, "%.2f", valor); }

    private double lerDecimal(String texto, String campo, boolean zeroPermitido) {
        String limpo = texto.strip().replace("R$", "").strip();
        try {
            if (limpo.matches("\\d{1,3}(\\.\\d{3})+(,\\d{1,2})?")) {
                limpo = limpo.replace(".", "");
            }
            if (!limpo.matches("\\d+([,.]\\d{1,2})?")) {
                throw new NumberFormatException();
            }
            BigDecimal valor = new BigDecimal(limpo.replace(",", "."));
            if ((!zeroPermitido && valor.signum() == 0)
                || valor.compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new NumberFormatException();
            }
            return valor.doubleValue();
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Informe " + campo.toLowerCase(AtlasVisuals.BRASIL)
                + (zeroPermitido ? " a partir de zero, com até duas casas decimais." : " maior que zero, com até duas casas decimais."));
        }
    }
}
