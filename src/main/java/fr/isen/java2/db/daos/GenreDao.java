package fr.isen.java2.db.daos;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GenreDao {

	public List<Genre> listGenres() {
            List<Genre> listofGenres = new ArrayList<>();
            try (Connection connection = getDataSource().getConnection()) {
              try (PreparedStatement statement = 
                        connection.prepareStatement("SELECT * FROM genre")) {
                   try (ResultSet results = statement.executeQuery()) {
                      while (results.next()) {
                    Genre genre = new Genre(results.getInt("idgenre"),
                                            results.getString("name"));
                    listofGenres.add(genre);
                } 
                   }
              } 
               return listofGenres;
            } catch (SQLException e){
                throw new RuntimeException("oups",e);
            }
           
	}

	public Genre getGenre(String name) {
            try (Connection connection = getDataSource().getConnection()) {
              try (PreparedStatement statement = 
                        connection.prepareStatement("SELECT * FROM genre WHERE name = ?")) {
                  statement.setString(1, name);
                   try (ResultSet results = statement.executeQuery()) {
                      if (results.next()) {
                    Genre genre = new Genre(results.getInt("idgenre"),
                                            results.getString("name"));
                    return genre;
                } 
                   }
              } 
            } catch (SQLException e){
                throw new RuntimeException("oups",e);
            }
            return null;
	}

	public void addGenre(String name) {
            try (Connection connection = getDataSource().getConnection()) {
                String sqlQuery = "INSERT INTO genre(name) VALUES(?)";
              try (PreparedStatement statement = connection.prepareStatement
        (sqlQuery,Statement.RETURN_GENERATED_KEYS)) {
                  statement.setString(1, name);
                   statement.executeUpdate();
              } 
            } catch (SQLException e){
                throw new RuntimeException("oups",e);
            }
	}
}
