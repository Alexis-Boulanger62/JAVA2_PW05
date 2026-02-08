package fr.isen.java2.db.daos;

import static fr.isen.java2.db.daos.DataSourceFactory.getDataSource;
import fr.isen.java2.db.entities.Genre;
import java.util.List;

import fr.isen.java2.db.entities.Movie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MovieDao {

	public List<Movie> listMovies() {
		List<Movie> listofMovies = new ArrayList<>();
            try (Connection connection = getDataSource().getConnection()) {
              try (PreparedStatement statement = 
                        connection.prepareStatement("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre")) {
                   try (ResultSet results = statement.executeQuery()) {
                      while (results.next()) {
                          Genre genre = new Genre(results.getInt("idgenre"),
                                            results.getString("name"));
                          Movie movie = new Movie(results.getInt("idmovie"),
                                            results.getString("title"),
                                            results.getDate("release_date").toLocalDate(),
                                            genre,
                                            results.getInt("duration"),
                                            results.getString("director"),
                                            results.getString("summary"));
                    listofMovies.add(movie);
                } 
                   }
              } 
               return listofMovies;
            } catch (SQLException e){
                throw new RuntimeException("oups",e);
            }
	}

	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> listofMovies = new ArrayList<>();
            try (Connection connection = getDataSource().getConnection()) {
              try (PreparedStatement statement = 
                        connection.prepareStatement("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?")) {
                  statement.setString(1, genreName);
                   try (ResultSet results = statement.executeQuery()) {
                      while (results.next()) {
                          Genre genre = new Genre(results.getInt("idgenre"),
                                            results.getString("name"));
                          Movie movie = new Movie(results.getInt("idmovie"),
                                            results.getString("title"),
                                            results.getDate("release_date").toLocalDate(),
                                            genre,
                                            results.getInt("duration"),
                                            results.getString("director"),
                                            results.getString("summary"));
                    listofMovies.add(movie);
                } 
                   }
              } 
               return listofMovies;
            } catch (SQLException e){
                throw new RuntimeException("oups",e);
            }
	}

	public Movie addMovie(Movie movie) {
		 try (Connection connection = getDataSource().getConnection()) {
                String sqlQuery = "INSERT INTO movie(title,release_date,genre_id,duration,director,summary) VALUES(?,?,?,?,?,?)";
                try (PreparedStatement statement = connection.prepareStatement
        (sqlQuery,Statement.RETURN_GENERATED_KEYS)) {
                   statement.setString(1, movie.getTitle());
                   statement.setDate(2, java.sql.Date.valueOf(movie.getReleaseDate()));
                   statement.setInt(3,movie.getGenre().getId());
                   statement.setInt(4,movie.getDuration());
                   statement.setString(5, movie.getDirector());
                   statement.setString(6, movie.getSummary());
                   statement.executeUpdate();
                   ResultSet ids = statement.getGeneratedKeys();
                   if (ids.next()){
                       return new Movie(
                        ids.getInt(1),          
                        movie.getTitle(),
                        movie.getReleaseDate(),
                        movie.getGenre(),
                        movie.getDuration(),
                        movie.getDirector(),
                        movie.getSummary()
                    );
                   }
                   return null;
              } 
            } catch (SQLException e){
                throw new RuntimeException("oups",e);
            }
	}
}
