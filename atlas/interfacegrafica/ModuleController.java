package atlas.interfacegrafica;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/** Estrutura das páginas de operação e gestão. */
public class ModuleController {
    @FXML private VBox content;
    @FXML private VBox header;
    @FXML private Label kicker;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;

    static ModuleController abrir(String titulo, String descricao) {
        FXMLLoader loader = new FXMLLoader(ModuleController.class.getResource("/recursos/telas/Modulo.fxml"));
        try {
            ScrollPane tela = loader.load();
            ModuleController controlador = loader.getController();
            controlador.tela = tela;
            controlador.titleLabel.setText(titulo);
            controlador.descriptionLabel.setText(descricao);
            return controlador;
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível abrir " + titulo, exception);
        }
    }

    private ScrollPane tela;
    ScrollPane tela() { return tela; }
    VBox conteudo() { return content; }
    void acaoNoTitulo(Node acao) {
        int posicao = header.getChildren().indexOf(titleLabel);
        header.getChildren().remove(posicao);
        header.getChildren().add(posicao, AtlasVisuals.linha(titleLabel, acao));
    }

    void exibirCabecalho(boolean exibir) {
        header.setVisible(exibir);
        header.setManaged(exibir);
    }

    void categoria(String texto) {
        kicker.setText(texto);
        kicker.setManaged(true);
        kicker.setVisible(true);
    }
}
