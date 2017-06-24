/**
 * Created by aalto on 6/23/17.
 */
import it.stilo.g.algo.ConnectedComponents;
import it.stilo.g.algo.CoreDecomposition;
import it.stilo.g.structures.Core;
import it.stilo.g.structures.WeightedUndirectedGraph;
import it.stilo.g.util.NodesMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.ArrayUtils;

public class fastUGraph extends WeightedUndirectedGraph {

    private NodesMapper<String> mapper;
    private ArrayList<Integer> LCC;
    private int edgesNumber;
//    private ArrayList<String> terms;

    public fastUGraph(int size) {
        super(size);
        this.mapper = new NodesMapper();
        this.LCC = new ArrayList();
        this.edgesNumber = 0;
    }

    public fastUGraph(int size, ArrayList<String> nodes, String edgeslistFILENAME) throws IOException {
        super(size);
        this.mapper = new NodesMapper();
        this.fillmapper(nodes);
        this.addEdgesFromFile(edgeslistFILENAME, nodes);
        this.LCC = null;
        this.edgesNumber = 0;
    }

    public static fastUGraph cloneGraph(fastUGraph g){

        fastUGraph newGraph = new fastUGraph(g.size);
        newGraph.mapper = g.mapper;
        newGraph.edgesNumber = 0;
        newGraph.LCC = null;

        return newGraph;
    }

    public void addEdgesFromFile(String FILENAME, ArrayList<String> nodes) throws IOException {
        String line;
        BufferedReader br = U.getBufferedReader(FILENAME);
        while((line=br.readLine())!=null){
            insertEdges(U.stem(line), nodes);

        }
        br.close();
    }

    public void insertEdges(String [] relations, ArrayList<String> nodes){

        ObjectOpenHashSet<String> relationSet = new ObjectOpenHashSet<>(relations);
        relationSet.retainAll(nodes);
        relations = (String []) relationSet.toArray();
        int w1, w2;

        for (int x = 0; x < relations.length - 1; x++) {
            for (int y = x; y < relations.length; y++) {

                w1 = this.mapper.getId(relations[x]);
                w2 = this.mapper.getId(relations[y]);

                if(w1!=w2) insertEdge(w1,w2);

            }
        }
    }

    public void insertEdge(int w1, int w2){
        if (!this.existEdge(w1, w2)) {
            this.add(w1, w2, (double) 1);
            this.edgesNumber += 1;
        } else {
            double w = this.get(w1, w2) +1 ;
            this.update(w1, w2, w);
        }
    }

    public fastUGraph edgePruner(int threshold){

        fastUGraph newGraph =  fastUGraph.cloneGraph(this);

        for (int v1 : this.getVertex()) {
            for (int v2 : this.getVertex()) {
                double w = this.get(v1, v2);
                if(this.existEdge(v1, v2) && (w > threshold)) newGraph.add(v1, v2, w);
            }
        }
        return newGraph;
    }


    private void computeLargestCC(){
        ArrayList<Integer> lccArray = new ArrayList();
        try {
            int max = -1;
            IntOpenHashSet tmpLCC = null;
            int cores = Runtime.getRuntime().availableProcessors();
            for(Set<Integer> component : ConnectedComponents.rootedConnectedComponents(this, this.getVertex(), cores)){
                if(component.size()>max){
                    tmpLCC = new IntOpenHashSet(component);
                    max = component.size();
                }
            }
                lccArray = new ArrayList(tmpLCC);
                this.setLCC(lccArray);
            } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getLCC(){
        if(this.LCC == null) computeLargestCC();
        return this.LCC;
    }

    private void setLCC(ArrayList<Integer> lcc){
        this.LCC = new ArrayList(lcc);
    }

    public void graphToTXT(String filename) throws IOException {

        File file = new File(filename);
        if(file.exists())
            file.delete();


        int[] vertices = this.getVertex();
//                System.out.println(Arrays.toString(vertices));
        for (int i = 0; i < vertices.length - 1; i++) {
            int vertex1 = vertices[i];
            for (int j = 0; j < vertices.length; j++) {
                int vertex2 = vertices[j];
//                        System.out.println(vertex1+"-"+vertex2+":"+graph.get(vertex1, vertex2));
                if ((vertex1 != vertex2) && this.existEdge(vertex1, vertex2)) {
                    double weight = this.get(vertex1, vertex2);
//                            System.out.println(vertex1+"\t"+vertex2+"\t"+weight);
                    RWServices.writeString(this.mapper.getNode(vertex1) + "\t" + this.mapper.getNode(vertex2) + "\t" + weight, filename);
                }
            }
        }

    }

    public ArrayList<String> KCore() throws InterruptedException {

        int cores = Runtime.getRuntime().availableProcessors();
        Core c = CoreDecomposition.getInnerMostCore(this, cores);
        int[] nodesID = c.seq;

        ArrayList<String> terms = new ArrayList<>();

        for (int id : nodesID) terms.add(this.getMapper().getNode(id));

        return terms;
    }






    public boolean existEdge(int a, int b) {
        int[] neighbors = this.out[a];
        return ArrayUtils.contains(neighbors, b);
    }


    public NodesMapper<String> getMapper() {
        return this.mapper;
    }


    public void printGraphSummary(){

        System.out.println("\n+-------- GRAPH SUMMARY --------+");
        System.out.println("Graph size:\t\t" + this.size);
        System.out.println("Edges number:\t\t" + this.edgesNumber);
        System.out.println("LCC dimension:\t\t" + this.LCC.size());
        System.out.println("+-------------------------------+\n");

    }

    public void fillmapper(ArrayList<String> list){
        if (!list.isEmpty()) {
            list.forEach(elem -> {mapper.getId(elem);});
        } else {
            System.out.println("No term");
        }
    }

}