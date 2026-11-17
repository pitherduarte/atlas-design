# Atlas Desktop — Gerência

Protótipo JavaFX do restaurante Atlas, editável no Scene Builder e executável pelo BlueJ. A referência visual é a página [03 — Atlas Central / Gerência](https://www.figma.com/design/qn1c28a2EVnptWWzrP09xT/Atlas-Desktop?node-id=1-4).

## Abrir no BlueJ

1. Abra **Project > Open Project...** e selecione esta pasta, que contém `package.bluej`.
2. Compile o projeto com o JDK 21 e o JavaFX disponibilizado pelo BlueJ.
3. Clique com o botão direito em `AtlasApp` e selecione **Run as JavaFX application**.

O projeto usa o JDK 21 e o JavaFX disponibilizado pelo BlueJ. Se você já estava com a versão anterior aberta no BlueJ, reabra o projeto para carregar a nova estrutura de pacotes.

## Estrutura do projeto

| Caminho | Conteúdo |
| --- | --- |
| `AtlasApp.java` | Ponto de entrada na raiz do projeto BlueJ |
| `atlas/interfacegrafica/` | Controladores JavaFX e componentes compartilhados |
| `atlas/modelo/` | Modelos de pedidos, mesas, produtos e funcionários |
| `atlas/dados/` | Dados usados na demonstração |
| `recursos/telas/` | Arquivos FXML editáveis no Scene Builder |
| `recursos/estilos/` | Folha de estilos JavaFX |
| `recursos/imagens/` | Ícones PNG carregados pelo aplicativo |
| `recursos/fontes/` | Fonte Inter e sua licença |
| `design/icones/` | Ícones SVG originais exportados do Figma |

## Comparação com o Figma

| Área | Versão anterior | Versão adaptada |
| --- | --- | --- |
| Estrutura e Início | Navegação recolhível, marca tipográfica e seis cartões estáticos | Logotipo e ícones exportados do Figma, paleta e tipografia Inter. Os cartões abrem os módulos e as filas do panorama |
| Pedidos | Seis pedidos, busca por cliente ou código e avanço de status | Grade de cartões, busca também por mesa, filtros combináveis, contagens, itens, totais, detalhe e histórico de alterações |
| Salão | Tela genérica | Mapa de 12 mesas, lista, seleção de mesa ou cadeira, filtros e consulta aos pedidos vinculados |
| Menu | Tela genérica | Os 41 produtos do Figma, busca, categorias, cadastro, edição, disponibilidade e exclusão |
| Funcionários | Tela genérica | Tabela com 65 fichas do Figma, filtros de cargo e situação, consulta, cadastro e edição |
| Relatórios e Histórico | Telas genéricas | Resumo calculado sobre 33 comandas fechadas e histórico filtrável que recebe as mudanças de situação dos pedidos |
| Configurações | Tela genérica | Cartões de restaurante, preferências e perfis, com atalho para Funcionários |

## Dados e limites do protótipo

- As alterações ficam em memória durante a execução. Os cadastros e os estados retornam aos dados de exemplo ao reiniciar. O acesso de Marina Costa é simulado.
- Pedidos contém um recorte de 12 pedidos em dois dias de demonstração. A saudação e o relógio do Início usam o horário real. Os indicadores de pedidos e produção usam a operação simulada de 15 de novembro de 2026.
- Os relatórios usam as 33 comandas fechadas de 15 de novembro. Ao escolher **Últimos 7 dias**, a tela informa que essa data é a única amostra disponível no período.
- Salão e suas filas permitem consulta e navegação. O fechamento de comandas, o pagamento, a integração com o Atlas Garçom e os fluxos de Caixa fazem parte de outros estados ou áreas do protótipo do Figma.

## Editar no Scene Builder

Abra as telas em `recursos/telas/`.

- `MainLayout.fxml` define a janela, a navegação lateral e o cabeçalho.
- `Dashboard.fxml` define o panorama do restaurante.
- `Pedidos.fxml` define a estrutura dos filtros, da grade e da área de detalhes.
- `Modulo.fxml` é a estrutura compartilhada das outras páginas. Os controladores montam as tabelas, os formulários, o mapa e as filas de acordo com os dados da sessão.
- `recursos/estilos/Atlas.css` define os estilos compartilhados pelas telas.
