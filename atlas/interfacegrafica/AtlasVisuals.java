package atlas.interfacegrafica;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/** Recursos visuais compartilhados pelas telas do Atlas. */
final class AtlasVisuals {
    static final Locale BRASIL = Locale.forLanguageTag("pt-BR");
    private static final Map<String, Image> IMAGENS = new HashMap<>();

    private AtlasVisuals() { }

    static ImageView icone(String nome, double largura) {
        Image imagem = IMAGENS.computeIfAbsent(nome, arquivo -> new Image(
            AtlasVisuals.class.getResource("/recursos/imagens/" + arquivo + ".png").toExternalForm()
        ));
        ImageView icone = new ImageView(imagem);
        icone.setFitWidth(largura);
        icone.setPreserveRatio(true);
        icone.setMouseTransparent(true);
        return icone;
    }

    static Label rotulo(String texto, String... estilos) {
        Label rotulo = new Label(texto);
        rotulo.getStyleClass().addAll(estilos);
        return rotulo;
    }

    static Button botao(String texto, String estilo, Runnable acao) {
        Button botao = new Button(texto);
        botao.getStyleClass().add(estilo);
        botao.setOnAction(evento -> acao.run());
        return botao;
    }

    static HBox linha(Node esquerda, Node direita) {
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox linha = new HBox(6, esquerda, espaco, direita);
        linha.setAlignment(Pos.CENTER_LEFT);
        return linha;
    }

    static String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(BRASIL).format(valor).replace('\u00a0', ' ');
    }

    static String normalizarBusca(String texto) {
        return Normalizer.normalize(texto.strip(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "").toLowerCase(BRASIL);
    }
}

