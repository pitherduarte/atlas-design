import atlas.interfacegrafica.MainController;

import java.io.IOException;
import java.io.InputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

/** Ponto de entrada do Atlas Desktop no BlueJ. */
public class AtlasApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        carregarFontes();
        Parent root = FXMLLoader.load(MainController.class.getResource("/recursos/telas/MainLayout.fxml"));
        Rectangle2D tela = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, Math.min(1440, tela.getWidth() - 20),
            Math.min(900, tela.getHeight() - 45));

        stage.setTitle("Atlas Desktop");
        stage.setMinWidth(Math.min(1050, tela.getWidth()));
        stage.setMinHeight(Math.min(650, tela.getHeight()));
        stage.setScene(scene);
        stage.show();
        root.requestFocus();
    }

    private static void carregarFontes() {
        for (String peso : new String[] {"Regular", "SemiBold", "Bold"}) {
            String caminho = "/recursos/fontes/Inter-" + peso + ".ttf";
            try (InputStream fonte = AtlasApp.class.getResourceAsStream(caminho)) {
                if (fonte != null) {
                    Font.loadFont(fonte, 14);
                }
            } catch (IOException exception) {
                throw new IllegalStateException("Não foi possível ler " + caminho, exception);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
