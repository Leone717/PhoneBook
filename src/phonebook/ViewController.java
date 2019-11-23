package phonebook;
                                                                                
import java.net.URL;                                                            //importálás                                      
import java.util.ResourceBundle;                                                
import javafx.beans.value.ChangeListener;                                       
import javafx.beans.value.ObservableValue;                                      
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class ViewController implements Initializable {

    @FXML                                                                       //FXML ID-k hozzáadása, dinamikus elemek
    TableView table;
    @FXML
    TextField inputLastname;
    @FXML
    TextField inputFirstName;
    @FXML
    TextField inputEmail;
    @FXML
    Button addNewContactButton;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    SplitPane mainSplit;
    @FXML
    AnchorPane anchor;
    @FXML
    TextField inputExportName;
    @FXML
    Button exportButton;

    DB db = new DB();                                                           //új db adatbázis létrehozása

    private final String MENU_CONTACTS = "Kontaktok";                           //ÁLLANDÓK létrehozása, amiket a menüben használunk és rákattintunk
    private final String MENU_LIST = "Lista";
    private final String MENU_EXPORT = "Exportálás";
    private final String MENU_EXIT = "Kilépés";

    private final ObservableList<Person> data = FXCollections.observableArrayList();//ami ebben az ObservableList-ben benne van, azt jeleníti meg a JAVAFX Table
                                                                                    //observableArrayList(): statikus fgv, nem kell példányosítani
                                                                                    //ez köti össze a data nevű listánkat a tableView-al
    @FXML
    private void addContact(ActionEvent event) {                                //eseménykezelő, az addContact button-ra                            1. példa eseménykezelőrealertButton
        String email = inputEmail.getText();                                    //email változó, a bevitt emailcím textjének bekérése alapján
        if (email.length() > 3 && email.contains("@") && email.contains(".")) { //validálás: ellenőrzi a formáját, tartalmaz e @-ot és .-ot
            Person newPerson = new Person(inputLastname.getText(), inputFirstName.getText(), email);//bekérjük a Person adatait(Lname,Fname,e-mai) és elmentjük
            data.add(newPerson);                                                //hozzáadjuk a data-hoz("TÜKRÖZÖTT ADATBÁZIS"=DATA)
            db.addContact(newPerson);                                           //hozzáadjk a db-hez(ADATBÁZIS)
            inputLastname.clear();                                              //a bevitt adotokat töröljük az input mezőkből, h. újra üres legyen bevitel után
            inputFirstName.clear();
            inputEmail.clear();
        }else{                                                                  //üzenet rossz e-mail formátumra
            alert("Adj meg egy valódi e-mail címet!");
        }
    }

    @FXML
    private void exportList(ActionEvent event) {                                //az exportButton-hoz rendelt exportList eseménykezelő metódus, exportbutt-hoz kötjük fxml generálást
        String fileName = inputExportName.getText();                            //a fogadott exportnév lementése változóba
        fileName = fileName.replaceAll("\\s+", "");                             //a \\s és "" kicserélése
        if (fileName != null && !fileName.equals("")) {                         //elenőrizzük h. a fájlnév nem-e null és nem lehet szóköz
            PdfGeneration pdfCreator = new PdfGeneration();                     //PÉLDÁNYOSÍTÁS: új pdf generálása, mentés pdfCreator-ként
            pdfCreator.pdfGeneration(fileName, data);                           //erre a generált pdfCretaor-ra meghívjuk a pdfGeneraiton konstruktorát, nevet és tartalmat adva neki
        }else{
            alert("Adj meg egy fájlnevet!");                                    //hibaüzenet
        }
    }

    public void setTableData() {                                                //tábladatok megváltoztatása metódus
        
        TableColumn lastNameCol = new TableColumn("Vezetéknév");                //VEZETÉKNÉV oszlop létrehozása
        lastNameCol.setMinWidth(130);                                           //min. oszlopszélesség, 130nál nem lehet majd kisebbre húzni
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());        //szerkeszthető Textfield cellák készítése(cellatípus megadása)
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));//2paramétert vár:1,Pojo(Person)2,milyen értéket fogunk belőle kivenni
                                                                                              //Keresd a „lastName” változót a Person-on belül ami String típusú.
        lastNameCol.setOnEditCommit(                                            //COMMIT=küldés
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() { //új eseménykezelő, arra figyelj, hogy valaki editált-e
            @Override                                                           //felülírja a következő handle-t(ha valaki editálta)
            public void handle(TableColumn.CellEditEvent<Person, String> t) {   //t LESZ MAGA AZ ESEMÉNY AMI TARTALMAZZA A MELYIK POZÍCIÓBAN TÖRTÉNT A VÁLTOZÁS
                Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());//a t ESEMÉNYNEK KÉRJÜK A TABLEVIEW EGÉSZ TÁBLÁT, MAJD AZ ÖSSZES ITEMET 
                                                                                                              //MAJD EZEN BELÜL A TABLEPOSITIONT AMIT A t EVENT TARTALMAZ
                                                                                                //mi tudjuk hogy ez egy Person,egy sor képvisel egy Person-t, ezért kasztoljuk át Person-ra
                actualPerson.setLastName(t.getNewValue());                      //a legvégén arra kérjük, hogy ennek az objektumnak állítsd át a lstName-t, mivel most ezen az oszlopon 
                                                                                //dolgozunk, a t.getNewValue() segítségével
                                                                                //FONTOS: a t event tartalmazza a régi és az új értéket is, ezért hívható meg rá ez a függvény. 
                db.updateContact(actualPerson);                                 //actualpersonnal udpateljük a db adatbázist(VEZETÉKNÉV)
            }                                                                   //Ahogy megtörtént ez a módosítás, és kilépünk ebből a handle-ből, soha többet nem tudjuk, hogy mi volt az 
                                                                                //előző értékünk.(oldValue)
        }
        );

        TableColumn firstNameCol = new TableColumn("Keresztnév");               //KERESZTNÉV oszlop létrehozása
        firstNameCol.setMinWidth(130);                                          //min. oszlopszélesség, 130nál nem lehet majd kisebbre húzni
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());       //szerkeszthető Textfield cellák készítése(cellatípus megadása)
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));//2paramétert vár:1,Pojo(Person) 2,milyen értéket fogunk belőle kivenni
                                                                                                //Keresd a „firstName” változót a Person-on belül ami String típusú.
        firstNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() { //új eseménykezelő, arra figyelj, hogy valaki editált-e
            @Override                                                           //felülírja a következő handle-t
            public void handle(TableColumn.CellEditEvent<Person, String> t) {   //t változóba mentjük Keresztnév új értékét
                Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());//a t ESEMÉNYNEK KÉRJÜK A TABLEVIEW EGÉSZ TÁBLÁT, MAJD AZ ÖSSZES ITEMET
                actualPerson.setFirstName(t.getNewValue());                                                   //MAJD EZEN BELÜL A TABLEPOSITIONT AMIT A t EVENT TARTALMAZ
                db.updateContact(actualPerson);                                 //actualpersonnal udpateljük a db adatbázist(KERESZTMÉV)    
            }
        }
        );

        TableColumn emailCol = new TableColumn("Email cím");                    //EMAIL CÍM oszlop létrehozása
        emailCol.setMinWidth(250);                                              //min. oszlopszélesség, 250nél nem lehet majd kisebbre húzni
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));//2paramétert vár:1,Pojo(Person) 2,milyen értéket fogunk belőle kivenni
                                                                                //Keresd a "email" változót a Person-on belül ami String típusú.
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());           //szerkeszthető Textfield cellák készítése

        emailCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() { //új eseménykezelő, arra figyelj, hogy valaki editált-e
            @Override                                                           //felülírja a következő handle-t
            public void handle(TableColumn.CellEditEvent<Person, String> t) {   //t változóba mentjük email új értékét
                Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());//a t ESEMÉNYNEK KÉRJÜK A TABLEVIEW EGÉSZ TÁBLÁT, MAJD AZ ÖSSZES ITEMET
                actualPerson.setEmail(t.getNewValue());                                                       //MAJD EZEN BELÜL A TABLEPOSITIONT AMIT A t EVENT TARTALMAZ
                db.updateContact(actualPerson);                                 //actualpersonnal udpateljük a db adatbázist(EMAIL) 
            }
        }
        );

        TableColumn removeCol = new TableColumn( "Törlés" );                    //Törlés oszlop létrehozása
        emailCol.setMinWidth(100);                                              //min. oszlopszélesség, 130nál nem lehet majd kisebbre húzni

        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory = 
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>()
                {
                    @Override
                    public TableCell call( final TableColumn<Person, String> param )
                    {
                        final TableCell<Person, String> cell = new TableCell<Person, String>()
                        {   
                            final Button btn = new Button( "Törlés" );          //törlés btn nevű gomb létrehozása

                            @Override
                            public void updateItem( String item, boolean empty )//string item és boolean empty átadása a metódusnak
                            {
                                super.updateItem( item, empty );                //super:osztályra hivatkozunk, meghívjuk a metódust a korábbi paraméterekkel
                                if ( empty )                                    //ha üres 
                                {
                                    setGraphic( null );                         //null-ra teszi az értékeket
                                    setText( null );
                                }
                                else                                            //különben
                                {
                                    btn.setOnAction( ( ActionEvent event ) ->   //btn-hez ActionEvent rendelése
                                            {
                                                Person person = getTableView().getItems().get( getIndex() );//bekérjük az adott sorát a táblázatnak és person-ba mentjük
                                                data.remove(person);            //töröljük a sort a tükrözött ArrayList táblából(data)
                                                db.removeContact(person);       //törölkük a sort az adatbázisból(db)
                                       } );
                                    setGraphic( btn );                          //
                                    setText( null );                            //a text értékét nullázzuk a sorban
                                }
                            }
                        };
                        return cell;                                            //viszatérünk a cellához
                    }
                };

        removeCol.setCellFactory( cellFactory );                                //
        
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol);//a legvégén a table-hoz hozzáadjuk a korábban paraméterként bekért adatokat
                                                                                  //ez hozza létre a fejléct(Vezetéknév, Keresztnév, Email)

        data.addAll(db.getAllContacts());                                       //ITT ADJUK HOZZÁ A db ADATBÁZIST A data-HOZ
                                                                                //mostmár ha a datat feltölti, akkor olyan objektumokkal tölti fel, amik rendelkeznek id-val.
        
        table.setItems(data);   //adatot kell beállítanunk a table-hoz a setItems() metódus segítségével egy ObservaleListet:
    }

    private void setMenuData() {
        
        //SZABÁLY: //A TreeView lesz a fánk fő ága, de nem hozhatjuk létre csak úgy, csak ha átadunk neki már egy TreeItem-et ezért ezzel kezdjük:
        TreeItem<String> treeItemRoot1 = new TreeItem<>("Menü");                //TreeItem: olyan mint egy faág, a fa adott eleme, TreeView olyan elem, ami faszerkezetben jelenik meg
        TreeView<String> treeView = new TreeView<>(treeItemRoot1);              //ezt követően treeView-ban átadjuk neki ezt hogy éltere keljen(elősször kell egy TreeItem mindig!    
        treeView.setShowRoot(false);                                            //beállítjuk, hogy a menüt ne mutassa

        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);             //hozzáadjuk a String állandókat TreeItem elemként
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);

        nodeItemA.setExpanded(true);                                            //beállítjuk, hogy már nyiva legyen a Kontaktok az ágban

        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png")));//KÉPEK BEKÉRÉSE és kimentése Node változókba
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));
        TreeItem<String> nodeItemA1 = new TreeItem<>(MENU_LIST, contactsNode);  //bekért képek hozzárendelése állandókhoz, kontakt alatti menü fejlesztése
        TreeItem<String> nodeItemA2 = new TreeItem<>(MENU_EXPORT, exportNode);  //a treeItemeknek átadjuk a node-okat

        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);                 //bekérjük a nodeItamA(MENU_CONTACTS) gyerekeit, majd hozzá adjuk az állandókat(képekkel)
        treeItemRoot1.getChildren().addAll(nodeItemA, nodeItemB);               //bekérjük a treeItemRoot1 gyerekeit majd hozzáadjuk a 2 nodeItem-et
        //Létrehozunk még 2 TreeItem elemet, de őket NEM a fa elemre tettük rá!  Hanem őket a Root1 elemünkre tettük, azáltal hogy meghívtuk az összes 
        //gyerekét és hozzáadtuk őket. Tehát a felem gyökéreleméhez adtuk hozzá ezt az újabb két elemet
        
        menuPane.getChildren().add(treeView);                                   //a létrehozott MenuPane-nek bekérjük a gyerekeit, majd hozzáadjuk a treeView-et

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {//változásfigyelőt adunk a treeView-hoz, cél, hogy egy kattintásra lenyiljon a menü
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {//changed metódus, fogadok egy megfigyelt objektumot, egy hozzá tartozó régi és egy új értéket
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;    //selectedItem létherhozáa, amit az új értékkel teszek egyenlővé
                String selectedMenu;                                            //String selectedMenu bevezetése, ő lesz az amire a felhasználó rákattintott
                selectedMenu = selectedItem.getValue();                         //selectedMenühöz adjuk az értéket, a menü nevér amirea felhasználó rákatintott

                if (null != selectedMenu) {                                     //negálás, nem lehet null a selectedMenu értéke
                    switch (selectedMenu) {                                     //elágazás
                        case MENU_CONTACTS:                                     //MENU_CONTACTS-ra kattintás esetén
                            selectedItem.setExpanded(true);                     //kinyitja a kontaktokat
                            break;
                        case MENU_LIST:                                         //LIST-re kattintás esetén
                            contactPane.setVisible(true);                       //láthatóvá teszi a kontaktokat           
                            exportPane.setVisible(false);                       //exportáló felületet láthatatlanná teszi
                            break;
                        case MENU_EXPORT:                                       //MENU_EXPORT-ra kattintás esetén
                            contactPane.setVisible(false);                      //láthatatlanná teszi a kontaktokat
                            exportPane.setVisible(true);                        //megjeleníti az exportáló felületet
                            break;
                        case MENU_EXIT:                                         //MENU_EXIT-re kattintás esetén
                            System.exit(0);                                     //Kilépés
                            break;                                              //fontos a brake, formailag! 
                    }
                }

            }
        });

    }

    private void alert(String text) {                                           //alert metódus text paraméterrel
        mainSplit.setDisable(true);                                             //kikapcsojuk a mainSplitet
        mainSplit.setOpacity(0.4);                                              //átlátszóságot állítunk
        
        Label label = new Label(text);                                          //új label-t hozunk létre, a paraméternek megadott text értékkel
        Button alertButton = new Button("OK");                                  //új Button, ok
        VBox vbox = new VBox(label, alertButton);                               //új VBox(vertikális elrendezésre)
        vbox.setAlignment(Pos.CENTER);                                          //középre igazítjuk a VBoxot, mely majd rendezi a labet-t és a Button-t
        
        alertButton.setOnAction(new EventHandler<ActionEvent>() {               //eseménykezelő az OK gombhoz                           2. példa eseménykezelőre 
            @Override
            public void handle(ActionEvent e) {                                 //e esemény eset-én/lenyomjuk az ok-t
                mainSplit.setDisable(false);                                    //a mainSplitet-t láthatóvá tesszük    
                mainSplit.setOpacity(1);                                        //átlátszóságot visszaálítjuk maximumra(1)
                vbox.setVisible(false);                                         //vbox láthatóságát kivesszük
            }
        });
        
        anchor.getChildren().add(vbox);                                         //anchorPane-nek megkérük az összes gyerekétés hozzáadjuk a vbox-ot    
        anchor.setTopAnchor(vbox, 300.0);                                       //vbox elrendezése az anchorPane-en
        anchor.setLeftAnchor(vbox, 300.0);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {                        //amikor a program először lefut, ő felelős azért hogy minden kód lefusson ami a törzsébe van definiálva
        setTableData();                                                         //az initilaze-ban nem csak a tábláról, hanem a menüről is gondoskodunk
        setMenuData();
    }

}
