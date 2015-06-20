package cra.APSP;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import sun.security.provider.certpath.AdjacencyList;
import cra.model.Element;
import cra.model.PathSet;

public class Algorithmus
{
    public void computePaths(Element source)
    {
        source.setMinDistance(0);
        PriorityQueue<Element> vertexQueue = new PriorityQueue<Element>();
      	vertexQueue.add(source);
      	while (!vertexQueue.isEmpty()) {
		    Element u = vertexQueue.poll();
	
	        // Visit each edge exiting u
	        for (Element e : u.getNeighbour())
	        {
	        	Element v = e;
	        	double weight = 1;
	        	double distanceThroughU = u.getMinDistance() + weight;
	        	if (distanceThroughU < v.getMinDistance()) {
				    vertexQueue.remove(v);
				    v.setMinDistance(distanceThroughU);
				    v.setPrevious(u);
				    vertexQueue.add(v);
	        	}
	        	
	        	if(distanceThroughU == v.getMinDistance() && v.getMinDistance()>1){
	        
	        		LinkedList<Element>tempPath = new LinkedList<Element>();
	        		Element temp = v;
	        		
	        		while(temp!=null && elementInList(temp, tempPath)){
	        			tempPath.add(temp);
	        			temp = temp.getPrevious();
	        		}
	        		
	        		Collections.reverse(tempPath);
	        		
	        		source.shortestPaths.add(new PathSet(source, v, tempPath));
	        		
	        		vertexQueue.remove(v);
				    v.setMinDistance(distanceThroughU);
				    v.setPrevious(u);
				    vertexQueue.add(v);
	        	}
	          }
        }
    }

    
    private boolean elementInList(Element e, LinkedList<Element> list){
    	for(int i = 0; i<list.size();i++){
    		if(list.get(i)== e)
    			return true;
    	}
    	return false;
    }
    public List<Element> getShortestPathTo(Element target, Element source)
    {
        List<Element> path = new ArrayList<Element>();
        for (Element vertex = target; vertex != null; vertex = vertex.getPrevious()){
        	path.add(vertex);
        	if(vertex == source){
        		Collections.reverse(path);
        		return path;
        	}
        }
        
        Collections.reverse(path);
        source.shortestPaths.add(new PathSet(source, target, (LinkedList)path));
        return path;
    }

   public void resetPrevious(Element[] vertex){
	   for(int i = 0; i<vertex.length;i++){
		   vertex[i].setPrevious(null);
		   vertex[i].setMinDistance(Double.POSITIVE_INFINITY);
	   }
   }
   /*
    * Main Method to start Algorithmus (TEST)
    
    public static void main(String[] args)
    {
    	Algorithmus algo = new Algorithmus();
    	
	    Element v0 = new Element("Redville");
		Element v1 = new Element("Blueville");
		Element v2 = new Element("Greenville");
		Element v3 = new Element("Orangeville");
		Element v4 = new Element("Purpleville");
	
		
		Edge[] adja ={	
				new Edge(v1),
				new Edge(v2),
				new Edge(v3)
		};
		
		v0.setAdjacencies(adja);
		
		Edge[] adja1 ={
				new Edge(v0),
				new Edge(v2),
				new Edge(v4)
		};
		v1.setAdjacencies(adja1);
		
		Edge[] adja2 ={
				new Edge(v0),
				new Edge(v1)
		};
		v2.setAdjacencies(adja2);
		
		Edge[] adja3 ={
				new Edge(v0),
				new Edge(v4)
		};
		v3.setAdjacencies(adja3);
		

		Edge[] adja4 = {
				new Edge(v1),
				new Edge(v3)
		};
		v4.setAdjacencies(adja4);
		
		
		
		Element[] vertices = { v0, v1, v2, v3, v4 };

		for(int i = 0; i<vertices.length;i++){
			vertices[i].copyAdja();
		}
		
	    for (int i = 0; i<vertices.length;i++)
		{
	    	algo.resetPrevious(vertices);
	    	algo.computePaths(vertices[i]);
		    for(int j = i+1; j<vertices.length;j++){
			    System.out.println("Distance from "+vertices[i].getNounPhrase()+" to " + vertices[j].getNounPhrase() + ": " + vertices[j].getMinDistance());
			    List<Element> path = algo.getShortestPathTo(vertices[j], vertices[i]);
			    System.out.print("Path: ");
			    for(int k=0; k<path.size();k++){
			    	if(path.size()>1)
			    		System.out.print(path.get(k).getNounPhrase()+" ,");
			    }
			    System.out.println();
	    	}
		    System.out.println(vertices[i].shortestPaths.size());
		    System.out.println();
		}
    }*/
}