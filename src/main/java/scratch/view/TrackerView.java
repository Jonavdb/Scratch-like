package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import scratch.viewmodel.TrackerViewModel;


import java.util.List;

public class TrackerView extends VBox {
    private final Label textPlayerData;
    private final TableView<TrackerViewModel.VariableRow> tableView;

    public TrackerView(TrackerViewModel vm) {
        // Texte affichant les données du joueur
        textPlayerData = new Label("");

        // on bind le texte au données formatées dans le view model
        textPlayerData.textProperty().bind(vm.getPlayerDataBinding());

        // on instancie la vue du tableau de variable
        tableView = new TableView<>();

        // on crée la colonne "Nom"
        TableColumn<TrackerViewModel.VariableRow, String> nameColumn = new TableColumn<>("Nom");

        // la valeur dans la cellule est la valeur de nameProperty de la classe VariableRow du trackerViewModel
        nameColumn.setCellValueFactory(cellData ->
                cellData.getValue().getNameProperty());

        // on crée la colonne "Nom"
        TableColumn<TrackerViewModel.VariableRow, String> valueColumn = new TableColumn<>("Valeur");

        // la valeur dans la cellule est la valeur de valueProperty de la classe VariableRow du trackerViewModel
        valueColumn.setCellValueFactory(cellData ->
                cellData.getValue().getValueProperty());

        // on ajoute les deux colonnes au tableau
        tableView.getColumns().addAll(List.of(nameColumn, valueColumn));

        // les deux colonnes prennent l'espace disponible
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // on remplit les valeurs des cellules avec la liste observable du viewModel
        tableView.setItems(vm.getEntries());

        // debug (pas important)
        System.out.println("in view : " + vm.getEntries().hashCode());

        // création du tableau
        TitledPane pane = new TitledPane("Variables", tableView);


        // on empêche de remonter
        pane.setCollapsible(false);

        // taille max
        this.setMaxHeight(200);

        // on centre le titre "Variables"
        pane.setStyle("-fx-alignment: CENTER;");

        // ajout bords gris + un peu de padding aerer autour des éléments du container
        this.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-padding: 10;");

        // ajout espace entre les données du joueur et le tableau de vriables
        this.setSpacing(5);

        // ajoute les éléments au conteneur de la classe courante "MapView"
        this.getChildren().addAll(textPlayerData,pane);


    }

}
