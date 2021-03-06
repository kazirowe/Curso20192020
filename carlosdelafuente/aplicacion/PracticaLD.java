package practicald;

import java.io.InputStream;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

/**
 * 
 * @author carlosdlfuente
 * @description Práctica de la asignatura Web Semántica y Datos Enlazados
 */
public class PracrticaLD
{

	public static void main(String args[])
	{
		// Archivo de datos
		String filename = "Arbolado_Viario_Gijon.ttl";
				
		// Creación de un modelo vacio
		Model model = ModelFactory.createDefaultModel();
		
		// Uso de Filemanager para localizar el archivo de entrada
		InputStream in = FileManager.get().open(filename);

		if (in == null)
			throw new IllegalArgumentException("File: "+filename+" not found");

		// Lectura del archivo en la serialización 'TTL'
		model.read(in, null, "TTL");

		// Casos de aplicación
		
		System.out.println("Caso 1: Obtener los nombres científicos de todos los árboles "
				+ "de la ciudad\n");

		String queryString = 
				"PREFIX biol: <http://ontologi.es/biol/botany#>" +
				"SELECT ?arbol ?nombre_cientifico " +
				"WHERE {?arbol biol:name ?nombre_cientifico.}" +
				"LIMIT 10";
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model) ;
		ResultSet results = qexec.execSelect() ;
	    
		while (results.hasNext())
		{
			QuerySolution binding = results.nextSolution();
			Resource arbol = (Resource) binding.get("arbol");
			Resource nombre = (Resource) binding.get("nombre_cientifico");		
		    System.out.println("Arbol: "+ arbol +" --> Nombre cientifico: "+ nombre);
		}
	
		System.out.println("\nCaso 2: Obtener la situación de todos los árboles "
				+ "de la ciudad\n");

		String queryString2 = 
				"PREFIX schema: <http://schema.org/> " +
				"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
				"SELECT ?arbol ?calle ?latitud ?longitud " +
				"WHERE {?arbol schema:streetAddress ?calle;" +
				 " geo:lat ?latitud;" +
				 " geo:long ?longitud.}" +
				"LIMIT 10";
		
		Query query2 = QueryFactory.create(queryString2);
		QueryExecution qexec2 = QueryExecutionFactory.create(query2, model) ;
		ResultSet results2 = qexec2.execSelect() ;
	    
		while (results2.hasNext())
		{
			QuerySolution binding = results2.nextSolution();
			Resource arbol = (Resource) binding.get("arbol");
			Literal calle = binding.getLiteral("calle");
			Literal latitud = binding.getLiteral("latitud");
			Literal longitud = binding.getLiteral("longitud");
		    System.out.println("Arbol: "+ arbol +" --> Situado en: "+ calle + ", con las coordenadas: " +
			latitud + ", " + longitud);
		}
		
		System.out.println("\nCaso 3: Obtener los árboles con mayor diámetro de copa y su situación\n");

		String queryString3 = 
				"PREFIX arb: <http://vocab.linkeddata.es/datosabiertos/def/medio-ambiente/arbolado#>" +
				"PREFIX schema: <http://schema.org/> " +
				"SELECT ?arbol ?calle ?diametro " +
				"WHERE {?arbol arb:hasDiametroCopa ?diametro;"
				+ "schema:streetAddress ?calle.}" +
				"ORDER BY DESC(?diametro)" +
				"LIMIT 10";

		Query query3 = QueryFactory.create(queryString3);
		QueryExecution qexec3 = QueryExecutionFactory.create(query3, model) ;
		ResultSet results3 = qexec3.execSelect() ;
	    
		while (results3.hasNext())
		{		    
			QuerySolution binding = results3.nextSolution();
			Resource arbol = (Resource) binding.get("arbol");
			Literal calle = binding.getLiteral("calle");
			Literal diametro = binding.getLiteral("diametro");		
		    System.out.println("Arbol: "+ arbol +" --> Situado en: "+ calle + ", con diametro de copa: "+ diametro);
		}		
		
		System.out.println("\nCaso 4: Obtener todos los nombres y número de árboles distintos y ordenados existentes en el dataset\n");

		String queryString4 = 
				"PREFIX biol: <http://ontologi.es/biol/botany#>" +
				"SELECT ?nombre_cientifico (COUNT(?nombre_cientifico) AS ?count)" +
				"WHERE {?arbol biol:name ?nombre_cientifico} " +
				"GROUP BY ?nombre_cientifico " +
				"ORDER BY DESC(?count) " +
				"LIMIT 20";

		Query query4 = QueryFactory.create(queryString4);
		QueryExecution qexec4 = QueryExecutionFactory.create(query4, model) ;
		ResultSet results4 = qexec4.execSelect() ;

		while (results4.hasNext())
		{		    		    
			QuerySolution binding = results4.nextSolution();
			Resource nombre = (Resource) binding.get("nombre_cientifico");
			Literal count = binding.getLiteral("count");
		    System.out.println("Arbol: " + nombre + " --> Unidades: " + count);		    
		}		

		System.out.println("\nCaso 5: Obtener todos los árboles con sus nombres científicos de una calle determinada (Por ejemplo: CALLE POLA DE SIERO)\n");

		String queryString5 = 
				"PREFIX schema: <http://schema.org/> " +
				"PREFIX biol: <http://ontologi.es/biol/botany#>" +
				"PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
				"SELECT ?arbol ?calle ?nombre_cientifico " +
				"WHERE {?arbol schema:streetAddress ?calle; " +
					"biol:name ?nombre_cientifico ." +
					"FILTER regex(?calle, '^CALLE POLA DE SIERO')}" +
				"LIMIT 10";
		
		Query query5 = QueryFactory.create(queryString5);
		QueryExecution qexec5 = QueryExecutionFactory.create(query5, model) ;
		ResultSet results5 = qexec5.execSelect() ;
	    
		while (results5.hasNext())
		{		    		    
			QuerySolution binding = results5.nextSolution();
			Resource arbol = (Resource) binding.get("arbol");
			Resource nombre = (Resource) binding.get("nombre_cientifico");					
			Literal calle = binding.getLiteral("calle");
			System.out.println("Arbol: "+ arbol +" de nombre científico: " + nombre + ", situado en:" + calle);   
		}
		
	}
}
