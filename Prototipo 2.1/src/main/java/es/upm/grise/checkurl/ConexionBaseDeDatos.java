package es.upm.grise.checkurl;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ConexionBaseDeDatos {
    private static final String DATABASE_URL = "jdbc:sqlite:pdf_database.db";

    private static final String CREATE_DOWNLOADED_PDFS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS downloaded_pdfs (doi TEXT, URL TEXT, finalURL TEXT, accesible NUMBER, contexto TEXT, type TEXT)";
    private static final String CREATE_NOMBRE_PDFS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS nombre_pdfs (nombre TEXT PRIMARY KEY)";

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement1 = connection.prepareStatement(CREATE_DOWNLOADED_PDFS_TABLE_SQL);
             PreparedStatement statement2 = connection.prepareStatement(CREATE_NOMBRE_PDFS_TABLE_SQL)) {
            statement1.executeUpdate();
            statement2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertNewLinkForPaper(String doi, String URL, String finalURL, int codigoRespuesta, String contexto) {
        String insertSQL = "INSERT INTO downloaded_pdfs (doi, URL, finalURL, accesible, contexto) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, doi);
            statement.setString(2, URL);
            statement.setString(3, finalURL);
            statement.setLong(4, codigoRespuesta);
            statement.setString(5, contexto);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertNombrePDF(String nombre) {
        String insertSQL = "INSERT INTO nombre_pdfs (nombre) VALUES (?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, nombre);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNombrePDFExistente(String nombre) {
        String querySQL = "SELECT COUNT(*) FROM nombre_pdfs WHERE nombre = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(querySQL)) {
            statement.setString(1, nombre);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public static void EliminarEnlacesDeUnArticulo(String doi) {
		
        String querySQL = "DELETE FROM downloaded_pdfs WHERE doi = ?";
        
        try {
        	
        	Connection connection = DriverManager.getConnection(DATABASE_URL);
        	
        	PreparedStatement statement = connection.prepareStatement(querySQL);
        	statement.setString(1, doi);
        	statement.executeUpdate();
        	
        } catch (SQLException e) {
            e.printStackTrace();
        }		
	}
	
	public static List<String> getAllURLs() throws SQLException {
		String querySQL = "SELECT doi, finalURL FROM downloaded_pdfs";
		List<String> linkList = new ArrayList<String>(); 

			Connection connection = DriverManager.getConnection(DATABASE_URL);
			PreparedStatement statement = connection.prepareStatement(querySQL);
			ResultSet resultSet = statement.executeQuery();
						
			while (resultSet.next()) {                      

				linkList.add(resultSet.getString("finalURL"));
				
			}
		
		return linkList;
	}
	
    public static void saveURLType(String link, String type) {
        String updateSQL = "UPDATE downloaded_pdfs SET type = ? WHERE finalURL = ?";
        
        try {
        	Connection connection = DriverManager.getConnection(DATABASE_URL);
        	PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setString(1, type);
            statement.setString(2, link);
            statement.executeUpdate();
            
        } catch (SQLException e) {
        	
            e.printStackTrace();
            
        }
    }
	
	public static boolean BDExist() {
		try {
			
			DriverManager.getConnection(DATABASE_URL);
		
		} catch (SQLException e) {
		
			return false;
		
		}
		
		return true;
		
	}

	public static void updateResponseCode(String URL, String finalURL, int codigoRespuesta) {
        String updateSQL = "UPDATE downloaded_pdfs SET accesible = ?, finalURL = ? WHERE URL = ?";
        
        try {
        	Connection connection = DriverManager.getConnection(DATABASE_URL);
        	PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setInt(1, codigoRespuesta);
            statement.setString(2, finalURL);
            statement.setString(3, URL);
            statement.executeUpdate();
            
        } catch (SQLException e) {
        	
            e.printStackTrace();
            
        }
	}
	
}
