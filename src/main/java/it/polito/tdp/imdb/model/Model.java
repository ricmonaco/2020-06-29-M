package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private Graph<Director, DefaultWeightedEdge> grafo;
	private ImdbDAO dao;
	private Map<Integer, Director> idMapDirector;
	
	public Model () {
		dao = new ImdbDAO();
	}
	
	public void creaGrafo(int year) {
		grafo = new SimpleWeightedGraph<Director, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//Inserisco i vertici = direttori dell'anno passato
		
		Graphs.addAllVertices(this.grafo, dao.listaDirectorsPerYear(year));
		
		//Creo idMap dei direttori per quell'anno
		idMapDirector = new HashMap<Integer, Director>();
		for(Director d : dao.listaDirectorsPerYear(year)) {
			idMapDirector.put(d.id, d);
		}
		
		//Inserisco gli archi = se hanno diretto al meno una volta lo stesso attore nello stesso anno
		
		for(Adiacenza a : dao.listaAdiacenzeConPeso(year)) {
			Graphs.addEdgeWithVertices(this.grafo, idMapDirector.get(a.getDirectorId1()), idMapDirector.get(a.getDirectorId2()) , a.getPeso());
		}	
	}

	public int numeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int numeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Director> listaDirettoriGrafo(){
		List<Director> result = new ArrayList<>(this.grafo.vertexSet());
		return result;
	}
	
	public List<Adiacenza> listaVerticiAdiacenti(Director d){
		List<Adiacenza> adiacenze = new ArrayList<Adiacenza>();
		
		for(Director di : Graphs.neighborListOf(this.grafo, d)) {
			
			adiacenze.add(new Adiacenza(d.id, di.id, (int) this.grafo.getEdgeWeight(this.grafo.getEdge(d, di))));
		}
		
		Collections.sort(adiacenze, new Comparator<Adiacenza>() {
			public int compare(Adiacenza arg0, Adiacenza arg1) {
				int res = 0;
				
				if(arg0.getPeso()<arg1.getPeso()) {
					res = 1;
				}
				else if (arg0.getPeso()>arg1.getPeso()){
					res = -1;
				}
				return res;
			}});
				
		return adiacenze;
	}
	
	public Map<Integer, Director> idMapDirector(){
		return this.idMapDirector;
	}
	
	
	
}
