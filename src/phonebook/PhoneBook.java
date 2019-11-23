
package phonebook;

import javafx.application.Application;                      
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class PhoneBook extends Application {                                    //PhoneBook Application-vá tétele(alosztálya lesz)
    
    @Override
    public void start(Stage stage) throws Exception {                           //start metódus, exception ha nem olvasná be)
        Parent root = FXMLLoader.load(getClass().getResource("View.fxml"));     //FXML nézet beolvasása a root-ban
        
        Scene scene = new Scene(root);                                          //"jelenet" hozzáadása, amihez hozzáadjuk a view.fxml-et
        
        stage.setTitle("Telefonkönyv");                                         //fejléc neve
        stage.setWidth(800);                                                    //szél
        stage.setHeight(680);                                                   //magasság
        stage.setScene(scene);                                                  //stagehez adjuk a scenet
        stage.show();                                                           //megjelenítjuk a stage-t 
    }


    public static void main(String[] args) {                                    //main végrehajtó függvény
        launch(args);
    }
    
}
