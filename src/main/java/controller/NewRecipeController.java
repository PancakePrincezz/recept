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
import receptek.hozzavalo.Hozzavalo;
import receptek.hozzavalo.HozzavaloDao;
import receptek.recept.Recept;
import receptek.recept.ReceptDao;
import receptek.user.User;
import receptek.user.UserDao;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;


@Slf4j
public class NewRecipeController {

    private String userName;
    private int userId;
    private String base64;

    @FXML
    private TextField TF_Cim;

    @FXML
    private ListView LV_Hozzavalok;

    @FXML
    private TextArea TA_Leiras;

    @FXML
    private TextArea TA_Hozzavalok;

    @FXML
    private Label LBL_FilePath;

    @FXML
    private Label LBL_Error;

    @FXML
    private Button BTN_FotoFeltoltes;

    public void initData(String userName, int userId){
        this.userName = userName;
        this.userId = userId;

        log.info("initData finished with userName: '{}'", this.userName);
    }

    public void mentesAction(ActionEvent actionEvent) throws java.io.IOException {

        if(base64 == null || base64.isEmpty()){
            LBL_Error.setText("Kép feltöltése kötelező");
        }
        else if(TF_Cim.getText().isEmpty()){
            LBL_Error.setText("Cím kitöltése kötelező");
        }
        else if(TF_Cim.getText().length() >255){
            LBL_Error.setText("Túl hosszú cím");
        }
        else if(TA_Hozzavalok.getText().isEmpty()){
            LBL_Error.setText("Hozzávalók kitöltése kötelező");
        }
        else if(TA_Hozzavalok.getText().length() > 16777215){
            LBL_Error.setText("Túl hosszú a hozzávalók listája");
        }
        else if(TA_Leiras.getText().isEmpty()){
            LBL_Error.setText("Leírás kitöltése kötelező");
        }
        else if(TA_Leiras.getText().length() > 16777215){
            LBL_Error.setText("Túl hosszú a leírás");
        }
        else{
            Foto foto = new Foto();
            foto.setData(base64);
            FotoDao.getInstance().persist(foto);

            User szerzo = UserDao.getInstance().getByUserId(userId).get(0);

            Recept recept = new Recept();
            recept.setCim(TF_Cim.getText());
            recept.setLeiras(TA_Leiras.getText());
            recept.setHozzavalok(TA_Hozzavalok.getText());
            recept.setSzerzoUser(szerzo);
            recept.setFoto(foto);

            ReceptDao.getInstance().persist(recept);

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent root = fxmlLoader.load();
            fxmlLoader.<HomeController>getController().initData(userName, userId);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            log.info("Loading home scene.");
        }
    }

    public void megseAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<HomeController>getController().initData(userName, userId);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        log.info("Loading home scene.");
    }

        public void chooseFotoAction(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG files", "*.png")
                ,new FileChooser.ExtensionFilter("JPG files", "*.jpg")
        );

        File file = fileChooser.showOpenDialog(BTN_FotoFeltoltes.getScene().getWindow());
        if (file != null) {
            Image image = new Image(file.toURI().toString());

            FileInputStream fileInputStream = null;
            byte[] bytesArray = null;

            try {
                bytesArray = new byte[(int) file.length()];

                //read file into bytes[]
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bytesArray);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(bytesArray != null){
                base64 = Base64.getEncoder().encodeToString(bytesArray);
                if(file.getName().length() > 30){
                    LBL_FilePath.setText(file.getName().substring(file.getName().length() - 30, file.getName().length()));
                } else {
                    LBL_FilePath.setText(file.getName());
                }
            } else{
                //LBL ERROR
            }
        }
    }

}
