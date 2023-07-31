package com.example.views;

import com.example.Comparator;
import com.example.Money;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ComparatorView extends Application {
    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage primaryStage) throws Exception {
        // Iniciar comparador
        final Comparator comparator = new Comparator();

        final String previousTitle = comparator.getPreviousPayroll().getCompetence().toString();
        final String currentTitle = comparator.getCurrentPayroll().getCompetence().toString();

        // Criação da tabela
        final TableView<IndividualView> tableView = new TableView<>();

        // Criação das colunas
        final TableColumn<IndividualView, String> registrationColumn = new TableColumn<IndividualView, String>(
                "Matrícula");
        registrationColumn
                .setCellValueFactory(new PropertyValueFactory<IndividualView, String>("registration"));

        final TableColumn<IndividualView, String> nameColumn = new TableColumn<>("Nome");
        nameColumn.setCellValueFactory(new PropertyValueFactory<IndividualView, String>("name"));
        nameColumn.setSortType(TableColumn.SortType.ASCENDING);

        final TableColumn<IndividualView, Money> previousValueColumn = new TableColumn<>(previousTitle);
        previousValueColumn
                .setCellValueFactory(new PropertyValueFactory<IndividualView, Money>("previousValue"));

        final TableColumn<IndividualView, String> previousSummaryColumn = new TableColumn<>(
                "Diferenciais de " + previousTitle);
        previousSummaryColumn.setCellValueFactory(
                new PropertyValueFactory<IndividualView, String>("previousSummary"));

        final TableColumn<IndividualView, Money> currentValueColumn = new TableColumn<>(currentTitle);
        currentValueColumn.setCellValueFactory(new PropertyValueFactory<IndividualView, Money>("currentValue"));

        final TableColumn<IndividualView, String> currentSummaryColumn = new TableColumn<>(
                "Diferenciais de " + currentTitle);
        currentSummaryColumn.setCellValueFactory(
                new PropertyValueFactory<IndividualView, String>("currentSummary"));

        final TableColumn<IndividualView, Money> differenceColumn = new TableColumn<>("Diferença");
        differenceColumn.setCellValueFactory(new PropertyValueFactory<IndividualView, Money>("difference"));
        // differenceColumn.setCellFactory(
        //         new Callback<TableColumn<IndividualView, Money>, TableCell<IndividualView, Money>>() {

        //             @Override
        //             public TableCell<IndividualView, Money> call(
        //                     final TableColumn<IndividualView, Money> col) {
        //                 return new TableCell<IndividualView, Money>() {
        //                     @Override
        //                     public void updateItem(final Money money, final boolean empty) {
        //                         super.updateItem(money, empty);
        //                         if (empty) {
        //                             setGraphic(null);
        //                         } else {
        //                             setText(money.toString());
        //                         }
        //                     }
        //                 };
        //             }

        //         });

        final TableColumn<IndividualView, String> previousDetailsColumn = new TableColumn<>(
                "Detalhes de " + previousTitle);
        previousDetailsColumn.setCellValueFactory(
                new PropertyValueFactory<IndividualView, String>("previousDetails"));

        final TableColumn<IndividualView, String> currentDetailsColumn = new TableColumn<>(
                "Detalhes de " + currentTitle);
        currentDetailsColumn.setCellValueFactory(
                new PropertyValueFactory<IndividualView, String>("currentDetails"));

        final TableColumn<IndividualView, Hyperlink[]> hyperlinksColumn = new TableColumn<>(
                "Contracheques");
        hyperlinksColumn.setCellValueFactory(
                new PropertyValueFactory<IndividualView, Hyperlink[]>(
                        "hyperlinks"));
        hyperlinksColumn.setCellFactory(
                new Callback<TableColumn<IndividualView, Hyperlink[]>, TableCell<IndividualView, Hyperlink[]>>() {

                    @Override
                    public TableCell<IndividualView, Hyperlink[]> call(
                            final TableColumn<IndividualView, Hyperlink[]> col) {
                        return new TableCell<IndividualView, Hyperlink[]>() {
                            @Override
                            public void updateItem(final Hyperlink[] hyperlinks,
                                    final boolean empty) {
                                super.updateItem(hyperlinks, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    final VBox hyperlinksCombo = new VBox(
                                            hyperlinks);
                                    setGraphic(hyperlinksCombo);
                                }
                            }
                        };
                    }

                });

        // Adiciona as colunas na tabela
        tableView.getColumns().addAll(registrationColumn, nameColumn, previousValueColumn,
                previousSummaryColumn,
                currentValueColumn, currentSummaryColumn, differenceColumn, previousDetailsColumn,
                currentDetailsColumn, hyperlinksColumn);

        // Adiciona os objetos na tabela
        tableView.setItems(FXCollections.observableArrayList(comparator.compare()));

        // Adiciona a coluna de nome como a ordenação padrão
        tableView.getSortOrder().add(nameColumn);

        // Configuração da personalização da linha
        tableView.setRowFactory(new CustomTableRowFactory());

        // Criação do layout
        AnchorPane root = new AnchorPane(tableView);

        // Configuração das âncoras da tabela
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);

        // Criação da cena
        Scene scene = new Scene(root, 1500, 700);

        // Configuração da janela
        primaryStage.setTitle("Comparação das folhas " + comparator.getPreviousPayroll().getCompetence() + " e "
                + comparator.getCurrentPayroll().getCompetence());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private class CustomTableRowFactory implements Callback<TableView<IndividualView>, TableRow<IndividualView>> {
        @Override
        public TableRow<IndividualView> call(TableView<IndividualView> param) {
            return new TableRow<IndividualView>() {
                @Override
                protected void updateItem(IndividualView item, boolean empty) {
                    super.updateItem(item, empty);

                    // if (item == null || empty) {
                    // setStyle(""); // Restaura o estilo padrão da linha
                    // } else {
                    // if (item.getNumericDifference() >= -30 && item.getNumericDifference() <= 30)
                    // {
                    // setStyle("-fx-background-color: red; -fx-text-fill: white;");
                    // } else {
                    // setStyle(""); // Restaura o estilo padrão da linha
                    // }
                    // }
                }
            };
        }
    }
}
