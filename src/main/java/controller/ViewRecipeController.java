package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import receptek.foto.Foto;
import receptek.foto.FotoDao;
import receptek.komment.Komment;
import receptek.komment.KommentDao;
import receptek.recept.Recept;
import receptek.recept.ReceptDao;
import receptek.user.User;
import receptek.user.UserDao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
public class ViewRecipeController {

    private String userName;
    private int userId;
    private int receptId;

    ObservableList<Komment> kommentData = FXCollections.observableArrayList();

    @FXML
    private TextField TF_Cim;

    @FXML
    private TextArea TA_Leiras;

    @FXML
    private TextArea TA_Hozzavalok;

    @FXML
    private ImageView Image_Kep;

    @FXML
    private TableView TBLVIEW_Kommentek;

    @FXML
    private TableColumn COL_Mikor;

    @FXML
    private TableColumn COL_Hozzaszolas;

    @FXML
    private Button BTN_Torles;

    public void initData(String userName, int userId, int receptId){
        this.userName = userName;
        this.userId = userId;
        this.receptId = receptId;
        List<Komment> kommentek = KommentDao.getInstance().getByReceptId(receptId);

        this.kommentData.sort((k1, k2) -> {
            return k1.getDatum().getTime() < k2.getDatum().getTime()
                    ? 1
                    : (k1.getDatum().getTime() > k2.getDatum().getTime() ? -1
                    : 0);});

        this.kommentData.addAll(kommentek);

        COL_Mikor.setCellFactory(column -> {
            TableCell<Komment, Date> cell = new TableCell<Komment, Date>() {
                private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if(empty) {
                        setText(null);
                    }
                    else {
                        setText(format.format(item));
                    }
                }
            };

            return cell;
        });

        COL_Mikor.setCellValueFactory(new PropertyValueFactory("Datum"));

        TableColumn<Komment, Void> COL_Hozzaszolo = new TableColumn("Hozzászóló");
        Callback<TableColumn<Komment, Void>, TableCell<Komment, Void>> cf = new Callback<TableColumn<Komment, Void>, TableCell<Komment, Void>>() {
            @Override
            public TableCell<Komment, Void> call(final TableColumn<Komment, Void> param) {
                final TableCell<Komment, Void> cell = new TableCell<Komment, Void>() {

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Komment data = getTableView().getItems().get(getIndex());

                            setText(data.getHozzaszoloUser().getUserName());
                        }
                    }
                };
                return cell;
            }
        };

        COL_Hozzaszolo.setCellFactory(cf);

        TBLVIEW_Kommentek.getColumns().add(COL_Hozzaszolo);

        COL_Hozzaszolas.setCellValueFactory(new PropertyValueFactory("Komment"));

        TBLVIEW_Kommentek.setItems(this.kommentData);

        Recept recept = ReceptDao.getInstance().find(receptId).get();
        this.TA_Leiras.setText(recept.getLeiras());
        this.TF_Cim.setText(recept.getCim());
        this.TA_Hozzavalok.setText(recept.getHozzavalok());

        this.BTN_Torles.setVisible(this.userId == recept.getSzerzoUser().getUserId());

        Image img = null;
        Foto f = recept.getFoto();
        if(f != null){
            String base64 = f.getData();
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            img = new Image(new ByteArrayInputStream(decodedBytes));
        } else{
            img = new Image(getClass().getResource("/pictures/placeholder.png").toExternalForm());
        }

        Image_Kep.setImage(img);

        log.info("iViewRecipe - InitData finished with userName: '{}'", this.userName);
    }

    public void ujKommetAction(ActionEvent actionEvent){
        showUjKommentDialog();
    }

    public void showUjKommentDialog(){
        log.info("ViewRecipe - UjKommentDialog started with userName: '{}'", this.userName);

        TextInputDialog dialog = new TextInputDialog("Komment");

        dialog.setTitle("Hozzászólás");
        dialog.setHeaderText("Új hozzászólás írása");
        //dialog.setContentText("Komment:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(kommentSzoveg -> {
            if(kommentSzoveg != null && !kommentSzoveg.isEmpty()) {

                User u = UserDao.getInstance().find(userId).get();

                Komment komment = new Komment();
                komment.setDatum(new Date());
                komment.setHozzaszoloUser(u);
                komment.setReceptId(receptId);
                komment.setKomment(kommentSzoveg);

                KommentDao.getInstance().persist(komment);
                this.kommentData.add(komment);
                this.kommentData.sort((k1, k2) -> {
                    return k1.getDatum().getTime() > k2.getDatum().getTime()
                            ? 1
                            : (k1.getDatum().getTime() < k2.getDatum().getTime() ? -1
                            : 0);
                });
            }});

        log.info("ViewRecipe - UjKommentDialog finished with userName: '{}'", this.userName);
    }

    public void torlesAction(ActionEvent actionEvent) {
        log.info("ViewRecipe - torlesAction started with userName: '{}'", this.userName);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        try{


            Recept rec = ReceptDao.getInstance().find(receptId).get();
            int fotoId = rec.getFoto().getId();
            ReceptDao.getInstance().remove(rec);

            Foto f = FotoDao.getInstance().find(fotoId).get();
            FotoDao.getInstance().remove(f);

            List<Komment> kommentek = KommentDao.getInstance().getByReceptId(receptId);
            for(int i = 0; i< kommentek.size(); i++){
                KommentDao.getInstance().remove(kommentek.get(i));
            }

            Parent root = fxmlLoader.load();
            fxmlLoader.<HomeController>getController().initData(userName, userId);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            log.info("Loading home scene.");
        } catch (Exception ex){
            log.error("torlesAction failed", ex);
        }

        log.info("ViewRecipe - torlesAction finished with userName: '{}'", this.userName);
    }

    public void visszaAction(ActionEvent actionEvent) throws IOException {
        log.info("ViewRecipe - visszaAction started with userName: '{}'", this.userName);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<HomeController>getController().initData(userName, userId);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

        log.info("ViewRecipe - visszaAction finished with userName: '{}'", this.userName);
        log.info("Loading home scene.");
    }
}

