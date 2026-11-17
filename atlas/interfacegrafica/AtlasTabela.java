package atlas.interfacegrafica;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/** Tabelas de leitura com as proporções de coluna do protótipo. */
final class AtlasTabela extends VBox {
    private final double[] colunas;
    private final VBox linhas = new VBox();

    AtlasTabela(double[] colunas, String... titulos) {
        this.colunas = colunas.clone();
        getStyleClass().add("list-table");
        Node[] cabecalho = new Node[titulos.length];
        for (int i = 0; i < titulos.length; i++) {
            cabecalho[i] = AtlasVisuals.rotulo(titulos[i], "table-heading");
        }
        getChildren().addAll(grade("table-header", cabecalho), linhas);
    }

    void adicionar(Node... celulas) { linhas.getChildren().add(grade("table-row", celulas)); }

    void limpar() { linhas.getChildren().clear(); }

    void vazio(String texto) {
        Label rotulo = AtlasVisuals.rotulo(texto, "page-subtitle", "table-row");
        rotulo.setMaxWidth(Double.MAX_VALUE);
        linhas.getChildren().add(rotulo);
    }

    private GridPane grade(String estilo, Node[] celulas) {
        GridPane grade = new GridPane();
        grade.setAlignment(Pos.CENTER_LEFT);
        grade.setHgap(9);
        grade.getStyleClass().add(estilo);
        for (int i = 0; i < colunas.length; i++) {
            ColumnConstraints coluna = new ColumnConstraints();
            coluna.setPercentWidth(colunas[i]);
            grade.getColumnConstraints().add(coluna);
            if (celulas[i] instanceof Label rotulo) {
                rotulo.setMinWidth(0);
            }
            grade.add(celulas[i], i, 0);
        }
        return grade;
    }
}
