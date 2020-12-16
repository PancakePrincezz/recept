package controller;

import com.google.common.hash.Hashing;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import lombok.extern.slf4j.Slf4j;
import receptek.user.User;
import receptek.user.UserDao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class LoginController {
    @FXML
    private TextField TF_Felhasznalonev;

    @FXML
    private PasswordField PF_Jelszo;

    @FXML
    private Label LBL_ErrorLabel;


    public void loginAction(ActionEvent actionEvent) throws IOException{
        if(TF_Felhasznalonev.getText().isEmpty()){
            LBL_ErrorLabel.setText("A felhasználónév mező kitöltése kötelező");
        }
        else if(PF_Jelszo.getText().isEmpty()){
            LBL_ErrorLabel.setText("A jelszó mező kitöltése kötelező");
        }
        else{
            Integer loginUserId = login(TF_Felhasznalonev.getText(), PF_Jelszo.getText());
            if(loginUserId != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
                Parent root = fxmlLoader.load();
                fxmlLoader.<HomeController>getController().initData(TF_Felhasznalonev.getText(), loginUserId);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene r = new Scene(root);
                stage.setScene(r);
                r.getStylesheets().add
                        (getClass().getResource("/css/home.css").toExternalForm());
                stage.show();
                log.info("userName is '{}', loading home scene.", TF_Felhasznalonev.getText());
            } else{
                LBL_ErrorLabel.setText("Hibás felhasználónév és jelszó páros");
            }
        }
    }

    public void loginAsVendegAction(ActionEvent actionEvent) throws IOException{
        TF_Felhasznalonev.setText("Vendég");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Parent root = fxmlLoader.load();
        fxmlLoader.<HomeController>getController().initData(TF_Felhasznalonev.getText(), -1);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene r = new Scene(root);
        stage.setScene(r);
        r.getStylesheets().add
                (getClass().getResource("/css/home.css").toExternalForm());
        stage.show();
        log.info("userName is '{}', loading home scene.", TF_Felhasznalonev.getText());
    }

    public void registerAction(ActionEvent actionEvent) throws IOException{
        log.info("LoginController - registerAction started with userName: '{}'", TF_Felhasznalonev.getText());

        if(TF_Felhasznalonev.getText().isEmpty()){
            LBL_ErrorLabel.setText("A felhasználónév mező kitöltése kötelező");
        }
        else if(PF_Jelszo.getText().isEmpty()){
            LBL_ErrorLabel.setText("A jelszó mező kitöltése kötelező");
        }
        else{
            Integer regUserId = register(TF_Felhasznalonev.getText(), PF_Jelszo.getText());
            if(regUserId != null){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
                Parent root = fxmlLoader.load();
                fxmlLoader.<HomeController>getController().initData(TF_Felhasznalonev.getText(), regUserId);
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                Scene r = new Scene(root);
                stage.setScene(r);
                r.getStylesheets().add
                        (getClass().getResource("/css/home.css").toExternalForm());
                stage.show();
                log.info("userName is '{}', loading home scene.", TF_Felhasznalonev.getText());
            } else{
                LBL_ErrorLabel.setText("Foglalt felhasználónév");
            }
        }

        log.info("LoginController - registerAction finished with userName: '{}'", TF_Felhasznalonev.getText());
    }

    private Integer register(String userName, String password){
        log.info("LoginController - register started with userName: '{}'", TF_Felhasznalonev.getText());

        Integer result = null;

        //letezik mar a user vagy sem
        if(UserDao.getInstance().getUserByName(userName).isEmpty()){
            String pwHash = Hashing
                    .sha256()
                    .hashString(password, StandardCharsets.UTF_8)
                    .toString();
            User u = new User();
            u.setUserName(userName);
            u.setPwHash(pwHash);

            UserDao.getInstance().persist(u);
            List<User> user = UserDao.getInstance().getUserByNameAndPasswordHash(userName, pwHash);
            if(!user.isEmpty()){
                result = user.get(0).getUserId();
            }
        }

        log.info("register finished with userName: '{}'. result: '{}'", userName, result != null ? result : "NULL");

        return result;
    }

    private Integer login(String userName, String password){
        log.info("LoginController - login started with userName: '{}'", TF_Felhasznalonev.getText());

        Integer result = null;

        String pwHash = Hashing
                .sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();

        List<User> u = UserDao.getInstance().getUserByNameAndPasswordHash(userName, pwHash);
        if(!u.isEmpty()){
            result = u.get(0).getUserId();
        }

        log.info("login finished with userName: '{}'. result: '{}'", userName, result != null ? result : "NULL");

        return result;
    }
}
