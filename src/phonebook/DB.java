
package phonebook;                                          //ADATBÁZISMŰVELETEK/DB

import java.sql.Connection;                                 //importok a meghívott függvényekhez és értékekhez
import java.sql.DatabaseMetaData;                           //a derby-t hozzá kell adni a libraries-hez
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class DB {
    
    final String URL = "jdbc:derby:sampleDB;create=true";   //derby megadása, sampleDB elnevezés és ha még nincs kreáljon újat
    final String USERNAME = "";                             //ezeket kihagyjuk
    final String PASSWORD = "";
    
        //Létrehozzuk a kapcsolatot (hidat), hogy létezzenek
    Connection conn = null;                                 //conn="híd"
    Statement createStatement = null;                       //createStatement="TEHERAUTÓ"
    DatabaseMetaData dbmd = null;                           //dbmd=adatbázis metaadatok, pl. oszlopnév 
    
    
    public DB() {
        //Megpróbáljuk életre kelteni
        try {
            conn = DriverManager.getConnection(URL);        //URL beolvasása az adatbázishoz
            System.out.println("A híd létrejött");
        } catch (SQLException ex) {                         //EXCEPTION: ha nem jönne létre a híd az adatbázishoz
            System.out.println("Valami baj van a connection (híd) létrehozásakor.");
            System.out.println(""+ex);
        }
        
        //Ha életre kelt, csinálunk egy megpakolható teherautót
        
        if (conn != null){                                  // ha a conn(híd) nem egyenlő null-al, akkor tudjuk hogy létezik, létrejött a kapcsolat 
            try {
                createStatement = conn.createStatement();   //a createStatement lesz a teherautó, meghívjuk a conn híd createStatement fgv-t
            } catch (SQLException ex) {                     //EXCEPTION:ha baj lene a teherautó létrehozásakot
                System.out.println("Valami baj van van a createStatament (teherautó) létrehozásakor.");
                System.out.println(""+ex);
            }
        }
        
        //Megnézzük, hogy üres-e az adatbázis? Megnézzük, létezik-e az adott adattábla.
        try {           
            dbmd = conn.getMetaData();                       // a conn híd bekéri a metaadatokat, ha van (pl. oszlopértékek)
        } catch (SQLException ex) {                          //EXCEPTION: ha nem sikerülne ezeket kreálni
            System.out.println("Valami baj van a DatabaseMetaData (adatbázis leírása) létrehozásakor..");
            System.out.println(""+ex);
        }
        
        try {
            ResultSet rs = dbmd.getTables(null, "APP", "CONTACTS", null);//bekérjük a táblát
            if(!rs.next())                                               // negálás, ha nincs következő érték
            { 
             createStatement.execute("create table contacts(id INT not null primary key GENERATED ALWAYS AS IDENTITY"       //ID HOZZÁADÁSA A DERBY-HEZ
                     + " (START WITH 1, INCREMENT BY 1),lastname varchar(20), firstname varchar(20), email varchar(30))");
            }                       //megadjuk a kocsinak az adatokat. tipusa id nem lehet null, azt mondjuk generált, azaz a derbynek kell létrehoznia és ezzel azonosítjuk
                                    //1-el kezdpődik és 1-el növeljük illetve az oszlopok nevéhez megadjuk a terjedelmét(varchar)
            
        } catch (SQLException ex) {                                             //EXCEPTION: ha baj lenne a tábla létrehozásakor 
            System.out.println("Valami baj van az adattáblák létrehozásakor.");
            System.out.println(""+ex);
        }       
    }
    
    // Itt kezdődnek a függvények  
    public ArrayList<Person> getAllContacts(){                  //az összes contacts bekérése a táblából
        String sql = "select * from contacts";                  //sql parancs mentése
        ArrayList<Person> users = null;                         //users arraylist 
        try {
            ResultSet rs = createStatement.executeQuery(sql);   //ReslutSet-be mentjük a bekért sql adatokat
            users = new ArrayList<>();                          //az users-t inicializáljuk ArryList-nek
            
            while (rs.next()){                                  //amíg a rs(bekért adatoknak van következő értéke(új sor), hozzáadjuk)
                Person actualPerson = new Person(rs.getInt("id"),rs.getString("lastname"),rs.getString("firstname"),rs.getString("email"));
                users.add(actualPerson);                        //usres arrayListhoz adjuk az adatbázisból bekért sort(felöltjük adatokkal)
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van a userek kiolvasásakor");
            System.out.println(""+ex);
        }
      return users;
    }
    
    public void addContact(Person person){                                      //új ember hozzáadása
      try {
        String sql = "insert into contacts (lastname, firstname, email) values (?,?,?)";//sql beviteli parancs(3 paraméterrel)
        PreparedStatement preparedStatement = conn.prepareStatement(sql);       //prepareStatement használata új értékek hozzáadásához, sql átadása paraméterként a hídnak
        preparedStatement.setString(1, person.getLastName());                   //értékek hozzáadása a parancshoz(Pojobol kérjük be az ott megadott adatokat)
        preparedStatement.setString(2, person.getFirstName());
        preparedStatement.setString(3, person.getEmail());
        preparedStatement.execute();                                            //legvégén a megadott értékek végrehajtása, bevitele
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact hozzáadásakor");
            System.out.println(""+ex);
        }
    }
    
    public void updateContact(Person person){                                   //új ember frissítése 
      try {
            String sql = "update contacts set lastname = ?, firstname = ? , email = ? where id = ?";//update sql parancs 
            PreparedStatement preparedStatement = conn.prepareStatement(sql);   //prepareStatement használata új értékek hozzáadásához, sql átadása paraméterként a hídnak
            preparedStatement.setString(1, person.getLastName());               //értékek hozzáadása a parancshoz(Pojobol kérjük be az ott megadott adatokat)
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.setInt(4, Integer.parseInt(person.getId()));      //a kocsi szigorúan ellenőrzi, hogy az id Int típus legyen
            preparedStatement.execute();                                        //legvégén a megadott értékek végrehajtása, bevitele
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact hozzáadásakor");
            System.out.println(""+ex);
        }
    }
    
     public void removeContact(Person person){                                  //új ember törlése 
      try {
            String sql = "delete from contacts where id = ?";                   //delete sql parancs ID AZONOSÍTóVAL
            PreparedStatement preparedStatement = conn.prepareStatement(sql);   //prepareStatement használata új értékek hozzáadásához, sql átadása paraméterként a hídnak
            preparedStatement.setInt(1, Integer.parseInt(person.getId()));      //kikérjük a person id-jét amit törölni szeretnénk a pojoból
            preparedStatement.execute();                                        //végrehajtjuk
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact törlésekor");
            System.out.println(""+ex);
        }
    }
    
}
