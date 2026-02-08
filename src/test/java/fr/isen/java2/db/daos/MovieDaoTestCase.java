package fr.isen.java2.db.daos;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovieDaoTestCase {
        private final MovieDao movieDao = new MovieDao();
        private final GenreDao genreDao = new GenreDao();
	@BeforeEach
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='movie'");
		stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='genre'");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
    @Test
    public void shouldListMovies() {
        // WHEN
        List<Movie> movies = movieDao.listMovies();

        // THEN
        assertThat(movies).hasSize(3);

        assertThat(movies)
            .extracting(
                "id",
                "title",
                "genre.name",
                "duration"
            )
            .containsOnly(
                tuple(1, "Title 1", "Drama", 120),
                tuple(2, "My Title 2", "Comedy", 114),
                tuple(3, "Third title", "Comedy", 176)
            );
    }
	
	 @Test
	 public void shouldListMoviesByGenre() {
		// WHEN
		List<Movie> movies = movieDao.listMoviesByGenre("Comedy");
		// THEN
		assertThat(movies).hasSize(2);
		assertThat(movies)
            .extracting(
                "id",
                "title",
                "genre.name",
                "duration"
            )
            .containsOnly(
                tuple(2, "My Title 2", "Comedy", 114),
                tuple(3, "Third title", "Comedy", 176)
            );
	 }
	
	 @Test
public void shouldAddMovie() throws Exception {
        // GIVEN
        Genre genre = new Genre(4,"Western"); 
        Movie movie = new Movie(
            "Funny Movie",
            LocalDate.of(2023, 2, 8),
            genre,
            120,
            "John Doe",
            "A hilarious movie for testing"
        );

        // WHEN
        Movie savedMovie = movieDao.addMovie(movie);

        // THEN
        Connection connection = DataSourceFactory.getDataSource().getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM movie WHERE title = 'Funny Movie'");
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getInt("idmovie")).isNotNull();
        assertThat(resultSet.getString("title")).isEqualTo(movie.getTitle());
        assertThat(resultSet.getDate("release_date").toLocalDate()).isEqualTo(movie.getReleaseDate());
        assertThat(resultSet.getInt("genre_id")).isEqualTo(movie.getGenre().getId());
        assertThat(resultSet.getInt("duration")).isEqualTo(movie.getDuration());
        assertThat(resultSet.getString("director")).isEqualTo(movie.getDirector());
        assertThat(resultSet.getString("summary")).isEqualTo(movie.getSummary());
        assertThat(resultSet.next()).isFalse();

        resultSet.close();
        statement.close();
        connection.close();
}
        
        
}

