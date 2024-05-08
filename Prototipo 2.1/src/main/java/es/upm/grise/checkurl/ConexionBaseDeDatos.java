package es.upm.grise.checkurl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConexionBaseDeDatos {
    private static final String DATABASE_URL = "jdbc:sqlite:pdf_database.db";
    private static final String CREATE_DOWNLOADED_PDFS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS downloaded_pdfs (doi TEXT, url TEXT, accesible BOOLEAN DEFAULT FALSE)";
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

    public static void insertPDF(String doi, String url, int codigoRespuesta) {
        String insertSQL = "INSERT INTO downloaded_pdfs (doi, url, accesible) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(insertSQL)) {
            statement.setString(1, doi);
            statement.setString(2, url);
            statement.setLong(3, codigoRespuesta);
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

    public static boolean isPDFDownloaded(String doi) {
        String querySQL = "SELECT COUNT(*) FROM downloaded_pdfs WHERE doi = ?";
        try (Connection connection = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = connection.prepareStatement(querySQL)) {
            statement.setString(1, doi);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
