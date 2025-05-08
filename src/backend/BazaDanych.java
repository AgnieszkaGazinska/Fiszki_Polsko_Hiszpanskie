
package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class BazaDanych {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=FiszkiDB;private static final String DB_URL = \"jdbc:sqlserver://localhost:1433;databaseName=FiszkiDB;encrypt=true;trustServerCertificate=true;loginTimeout=30";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "student";
    
    public static Map<String, String> pobierzSlowka(int kategoriaId) {
        Map<String, String> slowkaMapa = new LinkedHashMap<>();
        Connection polaczenie = null;
        PreparedStatement skladnia = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareStatement("SELECT polski, hiszpanski FROM slowka WHERE kategoria_id = ?");
            skladnia.setInt(1, kategoriaId);
            rs = skladnia.executeQuery();
            
            while (rs.next()) {
                String polskie = rs.getString("polski").trim();
                String hiszpanskie = rs.getString("hiszpanski").trim();
                slowkaMapa.put(polskie, hiszpanskie);
            }
            
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd bazy danych: " + e.getMessage(), "Błąd - aplikacja FiszkiDB", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Brak sterownika JDBC: " + e.getMessage(), "Błąd - aplikacja FiszkiDB", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (skladnia != null) skladnia.close();
                if (polaczenie != null) polaczenie.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Błąd przy zamykaniu zasobów: " + e.getMessage(), "Błąd - aplikacja FiszkiDB", JOptionPane.ERROR_MESSAGE);
            }
        }
        System.out.println("🔎 Ilość słówek pobranych z bazy: " + slowkaMapa.size());
        return slowkaMapa;
    }
    
    public static Map<String, Integer> pobierzKategorie() {
        Map<String, Integer> kategorie = new LinkedHashMap<>();
        Connection polaczenie = null;
        PreparedStatement skladnia = null;
        ResultSet rs = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareStatement("SELECT id, nazwa FROM kategorie");
            rs = skladnia.executeQuery();

            while (rs.next()) {
                kategorie.put(rs.getString("nazwa"), rs.getInt("id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd bazy danych: " + e.getMessage(), "Błąd - aplikacja FiszkiDB", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Brak sterownika JDBC: " + e.getMessage(), "Błąd - aplikacja FiszkiDB", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (skladnia != null) skladnia.close();
                if (polaczenie != null) polaczenie.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Błąd przy zamykaniu zasobów: " + e.getMessage(), "Błąd - aplikacja FiszkiDB", JOptionPane.ERROR_MESSAGE);
            }
        }
        return kategorie;
    }
    
    public static void edytujSlowko(int id, String nowyPolski, String nowyHiszpanski, int nowaKategoria) throws ClassNotFoundException, SQLException {
        Connection polaczenie = null;
        PreparedStatement skladnia = null;        
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");            
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareCall("{call dbo.EdytujSlowko(?, ?, ?, ?)}");
            skladnia.setInt(1, id);
            skladnia.setString(2, nowyPolski);
            skladnia.setString(3, nowyHiszpanski);
            skladnia.setInt(4, nowaKategoria);
            skladnia.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd " + e.getMessage(), "Błąd podczas edycji.", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (skladnia != null) skladnia.close();
            if (polaczenie != null) polaczenie.close();
        }
    }
    
    
    
    public static void dodajSlowko(String polski, String hiszpanski, int kategoriaId) throws ClassNotFoundException, SQLException {
        Connection polaczenie = null;
        PreparedStatement skladnia = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");            
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareCall("{call dbo.DodajSlowko(?, ?, ?)}");
            skladnia.setString(1, polski);
            skladnia.setString(2, hiszpanski);
            skladnia.setInt(3, kategoriaId);
            skladnia.execute();
            pobierzSlowka(kategoriaId);  
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd " + e.getMessage(), "Błąd podczas dodawania słowa.", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (skladnia != null) skladnia.close();
            if (polaczenie != null) polaczenie.close();
        }
    }
    
    
    public static void usunSlowko(String polski, int kategoriaId) throws ClassNotFoundException, SQLException {
        Connection polaczenie = null;
        PreparedStatement skladnia = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");            
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareCall("{call dbo.UsunSlowko(?, ?)}");
            skladnia.setString(1, polski);
            skladnia.setInt(2, kategoriaId);
            skladnia.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd " + e.getMessage(), "Błąd podczas usuwania słowa.", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (skladnia != null) skladnia.close();
            if (polaczenie != null) polaczenie.close();
        }
    }
    
    public static void dodajKategorie(String nazwa) throws ClassNotFoundException, SQLException {
        Connection polaczenie = null;
        PreparedStatement skladnia = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareCall("{call dbo.DodajKategorie(?)}");
            skladnia.setString(1, nazwa);
            skladnia.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd " + e.getMessage(), "Błąd podczas dodawania kategorii.", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (skladnia != null) skladnia.close();
            if (polaczenie != null) polaczenie.close();
        }
    }
    
    public static void usunKategorie(String nazwa) throws ClassNotFoundException, SQLException {
        Connection polaczenie = null;
        PreparedStatement skladnia = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareCall("{call dbo.UsunKategorie(?)}");
            skladnia.setString(1, nazwa);
            skladnia.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd: " + e.getMessage(), "Błąd podczas usuwania kategorii", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (skladnia != null) skladnia.close();
            if (polaczenie != null) polaczenie.close();
        }
    }
    
    public static void edytujKategorie(int id, String nowaNazwa) throws ClassNotFoundException, SQLException {
        Connection polaczenie = null;
        PreparedStatement skladnia = null;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            polaczenie = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            skladnia = polaczenie.prepareCall("{call dbo.EdytujKategorie(?, ?)}");
            skladnia.setInt(1, id);  // Ustawienie ID kategorii do edytowania
            skladnia.setString(2, nowaNazwa);  // Ustawienie nowej nazwy kategorii
            skladnia.execute();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd: " + e.getMessage(), "Błąd podczas edytowania kategorii", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (skladnia != null) skladnia.close();
            if (polaczenie != null) polaczenie.close();
        }
    }
    
    
    
}
