package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import receptek.foto.Foto;
import receptek.foto.FotoDao;
import receptek.recept.Recept;
import receptek.recept.ReceptDao;
import receptek.user.User;
import receptek.user.UserDao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class HomeController {
    private int userId;
    private String userName;

    ObservableList<Recept> masterData = FXCollections.observableArrayList();

    @FXML
    private Label LBL_UserName;

    @FXML
    private TableView<Recept> TBLVIEW_Receptek;

    @FXML
    private TextField TF_CimKereso;

    @FXML
    private Button BTN_Kilepes;

    public void initData(String userName, int userId){
        this.userName = userName;
        this.userId = userId;
        LBL_UserName.setText(userName);

        log.info("initData finished with userName: '{}'", this.userName);
    }

    @FXML
    public void initialize() {
        log.info("HomeController - initialize started with userName: '{}'", this.userName);

        List<Recept> receptek = ReceptDao
                .getInstance()
                .findAll();

        TableColumn<Recept, Void> colKep = new TableColumn("Kép");
        TableColumn<Recept, Void> colCim = new TableColumn("Cím");
        TableColumn<Recept, Void> colSzerzo = new TableColumn("Szerzője");
        TableColumn<Recept, Recept> colBtn = new TableColumn("");

        Callback<TableColumn<Recept, Void>, TableCell<Recept, Void>> cellFactory0 = new Callback<TableColumn<Recept, Void>, TableCell<Recept, Void>>() {
            @Override
            public TableCell<Recept, Void> call(final TableColumn<Recept, Void> param) {
                final ImageView imageView = new ImageView();
                imageView.setFitHeight(120);
                imageView.setFitWidth(180);

                final TableCell<Recept, Void> cell = new TableCell<Recept, Void>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if(!empty){
                            Recept data = getTableView().getItems().get(getIndex());
                            Image img = null;
                            Foto f = data.getFoto();
                            if(f != null){
                                String base64 = f.getData();
                                byte[] decodedBytes = Base64.getDecoder().decode(base64);
                                img = new Image(new ByteArrayInputStream(decodedBytes));
                            } else{
                                img = new Image(getClass().getResource("/pictures/placeholder.png").toExternalForm());
                            }

                            imageView.setImage(img);
                            setGraphic(imageView);
                        }
                    }
                };
                return cell;
            }
        };

        Callback<TableColumn<Recept, Recept>, TableCell<Recept, Recept>> cellFactory = new Callback<TableColumn<Recept, Recept>, TableCell<Recept, Recept>>() {
            @Override
            public TableCell<Recept, Recept> call(final TableColumn<Recept, Recept> param) {
                final TableCell<Recept, Recept> cell = new TableCell<Recept, Recept>() {

                    private final Button btn = new Button("Elolvasom");
                    {
                        btn.setOnAction((ActionEvent event)  -> {

                            Recept recept = getTableView().getItems().get(getIndex());
                            // Create a new FXML loader
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/viewRecipe.fxml"));
                            // Load the another FXML file
                            try {
                                Parent root = loader.load();
                                loader.<ViewRecipeController>getController().initData(userName, userId, recept.getReceptId());
                                Stage stage = (Stage) (TBLVIEW_Receptek).getScene().getWindow();
                                Scene r = new Scene(root);
                                stage.setScene(r);
                                r.getStylesheets().add
                                        (getClass().getResource("/css/home.css").toExternalForm());
                                stage.show();
                            } catch (java.io.IOException ex) {
                                log.error("LoadData with '{}' receptId failed.", recept.getReceptId());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Recept item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        Callback<TableColumn<Recept, Void>, TableCell<Recept, Void>> cellFactory1 = new Callback<TableColumn<Recept, Void>, TableCell<Recept, Void>>() {
            @Override
            public TableCell<Recept, Void> call(final TableColumn<Recept, Void> param) {
                final TableCell<Recept, Void> cell = new TableCell<Recept, Void>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            Recept data = getTableView().getItems().get(getIndex());

                            setText(data.getCim());
                        }
                    }
                };
                return cell;
            }
        };

        Callback<TableColumn<Recept, Void>, TableCell<Recept, Void>> cellFactory2 = new Callback<TableColumn<Recept, Void>, TableCell<Recept, Void>>() {
            @Override
            public TableCell<Recept, Void> call(final TableColumn<Recept, Void> param) {
                final TableCell<Recept, Void> cell = new TableCell<Recept, Void>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Recept data = getTableView().getItems().get(getIndex());

                            setText(data.getSzerzoUser().getUserName());
                        }
                    }
                };
                return cell;
            }
        };

        colKep.setCellFactory(cellFactory0);
        colCim.setCellFactory(cellFactory1);
        colSzerzo.setCellFactory(cellFactory2);
        colBtn.setCellFactory(cellFactory);
        TBLVIEW_Receptek.getColumns().add(colKep);
        TBLVIEW_Receptek.getColumns().add(colCim);
        TBLVIEW_Receptek.getColumns().add(colSzerzo);
        TBLVIEW_Receptek.getColumns().add(colBtn);

        masterData.addAll(receptek);
        FilteredList<Recept> filteredData = new FilteredList<>(masterData, p -> true);

        TF_CimKereso.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(recept -> {
                //mindent megjelenitunk
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (recept.getCim().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                else if (recept.getHozzavalok().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }
                return false; // Does not match.
            });
        });

        SortedList<Recept> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(TBLVIEW_Receptek.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        TBLVIEW_Receptek.setItems(sortedData);

        log.info("HomeController - initialize finished with userName: '{}'", this.userName);
    }



    public void ujReceptAction(ActionEvent actionEvent) throws IOException {
        log.info("HomeController - ujReceptAction started with userName: '{}'", this.userName);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/newRecipe.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<NewRecipeController>getController().initData(userName, userId);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene r = new Scene(root);
        stage.setScene(r);
        r.getStylesheets().add
                (getClass().getResource("/css/home.css").toExternalForm());
        stage.show();

        log.info("HomeController - ujReceptAction finished with userName: '{}'", this.userName);
        log.info("userName is '{}', loading newRecipe scene.", userName);
    }

    public void kilepesAction(ActionEvent actionEvent) throws IOException {
        log.info("HomeController - kilepesAction started with userName:'{}'", this.userName);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene r = new Scene(root);
        stage.setScene(r);
        r.getStylesheets().add
                (getClass().getResource("/css/home.css").toExternalForm());
        stage.show();

        log.info("HomeController - kilepesAction finished with userName:'{}'", this.userName);
        log.info("userName:'{}', loading login scene", this.userName);
    }
}
