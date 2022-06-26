package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Director> listaDirectorsPerYear(int year){
		String sql = "SELECT d.id, d.first_name, d.last_name "
				+ "FROM directors d, movies m, movies_directors md "
				+ "WHERE d.id = md.director_id AND m.id = md.movie_id "
				+ "AND m.year = ? GROUP BY d.id, d.first_name, d.last_name ;";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, year);
			
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				result.add(new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name")));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<Adiacenza> listaAdiacenzeConPeso(int year){
		String sql = "SELECT a.did AS did1, b.did AS did2, COUNT(*) AS peso "
				+ "FROM (SELECT md.director_id AS did, r.actor_id AS aid, r.movie_id AS mid "
				+ "FROM roles r, movies m, movies_directors md  "
				+ "WHERE r.movie_id = m.id AND r.movie_id = md.movie_id AND m.id = md.movie_id "
				+ "AND m.year = ?) a, (SELECT md.director_id AS did, r.actor_id AS aid, r.movie_id AS mid "
				+ "FROM roles r, movies m, movies_directors md  "
				+ "WHERE r.movie_id = m.id AND r.movie_id = md.movie_id AND m.id = md.movie_id "
				+ "AND m.year = ?) b "
				+ "WHERE a.aid = b.aid AND a.did > b.did "
				+ "GROUP BY a.did, b.did";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();
		
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, year);
			st.setInt(2, year);
			
			ResultSet res = st.executeQuery();
			
			while(res.next()) {
				result.add(new Adiacenza(res.getInt("did1"), res.getInt("did2"), res.getInt("peso")));
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	
}
