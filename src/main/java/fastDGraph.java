import it.stilo.g.algo.ConnectedComponents;
import it.stilo.g.algo.HubnessAuthority;
import it.stilo.g.algo.KppNeg;
import it.stilo.g.structures.DoubleValues;
import it.stilo.g.structures.WeightedDirectedGraph;
import it.stilo.g.util.NodesMapper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by aalto on 6/25/17.
 */
public class fastDGraph extends WeightedDirectedGraph {

    private NodesMapper<String> mapper;
    private NodesMapper<Long> mapperLong;
    private ArrayList<Integer> LCC;
    private int edgesNumber;

    public fastDGraph(int size, LongOpenHashSet nodes, BufferedReader edgelist) throws IOException {
        super(size);
        this.mapper = new NodesMapper<>();
        this.mapperLong = new NodesMapper<>();
        this.fillmapper(nodes);
        this.addEdgesFromFile2(edgelist, nodes);
        this.LCC = null;
        this.edgesNumber = 0;
    }


    private void fillmapper(LongOpenHashSet nodes){
        if (!nodes.isEmpty()) {
            nodes.forEach(elem -> {this.mapperLong.getId(elem);});
        } else {
            System.out.println("No term");
        }
    }

    public void addEdgesFromFile1(BufferedReader br, LongOpenHashSet nodes) throws IOException {
        String line;
        while((line=br.readLine())!=null){
            insertEdges(U.stem(line), nodes);
        }
        br.close();
    }

    public void addEdgesFromFile2(BufferedReader br, LongOpenHashSet nodes) throws IOException {
        String line;
        while((line = br.readLine())!= null){
            String[] arr = line.split("\t");
            long id1 = Long.parseLong(arr[0]);
            long id2 = Long.parseLong(arr[1]);
            if(nodes.contains(id1) && nodes.contains(id2))
                this.add(this.mapperLong.getId(id1), this.mapperLong.getId(id2), 1.0);
        }
    }

    public void insertEdges(String [] relations, LongOpenHashSet nodes){

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


    public boolean existEdge(int a, int b) {
        int[] neighbors = this.out[a];
        return ArrayUtils.contains(neighbors, b);
    }


    public ArrayList<Integer> getLCC(){
        if(this.LCC == null) computeLargestCC();
        return this.LCC;
    }

    private void setLCC(ArrayList<Integer> lcc){
        this.LCC = new ArrayList(lcc);
    }


    private void computeLargestCC(){
        ArrayList<Integer> lccArray;
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


    /**
     * Computes a list of top-k authorities among the nodes of the graph and return it.
     * @param k
     * @param outputPath
     * @return
     * @throws IOException
     */
    public ArrayList<String> getAuthorities(int k, String outputPath) throws IOException {
        String AUTH = "out/auth", HUB = "out/hub";
        if(!(Files.exists(Paths.get(AUTH)) & Files.exists(Paths.get(HUB)))) {
            System.out.println("Computing first " + k + " authorities...");

            BufferedWriter authBW = U.getBufferedWriter(AUTH, false);
            BufferedWriter hubBW = U.getBufferedWriter(HUB, false);

            int cores = Runtime.getRuntime().availableProcessors();
            ArrayList<ArrayList<DoubleValues>> hubAuth = HubnessAuthority.compute(this, 0.00001, cores);

            ArrayList<DoubleValues> auth = hubAuth.get(0);
            ArrayList<DoubleValues> hub = hubAuth.get(1);

            for (DoubleValues x : auth) authBW.write(this.getNode(x.index) + "\t" + x.value + "\n");
            for (DoubleValues x : hub) hubBW.write(this.getNode(x.index) + "\t" + x.value + "\n");

            hubBW.close();
            authBW.close();
        }

        // get top-1000 auth
        BufferedReader authBR = U.getBufferedReader(AUTH);
        ArrayList<String> authTOP = new ArrayList<>();

        for (int i = 0; i < 1000 ; i++) {
           authTOP.add(authBR.readLine().split("\t")[0]);
        }
        return authTOP;
    }

    public String getNode(int nodeID){
        return this.mapperLong.getNode(nodeID).toString();
    }


    public ArrayList<String> getKPPNEG(ObjectOpenHashSet<Partisan> partisans) throws InterruptedException, IOException {
        String BROKERS = "out/brokers";
        if(!Files.exists(Paths.get(BROKERS))) {
            int cores = Runtime.getRuntime().availableProcessors();
            BufferedWriter bw = U.getBufferedWriter(BROKERS, false);
            ArrayList<DoubleValues> brokers = KppNeg.searchBroker(this, this.getVertex(), cores);
            for (DoubleValues x : brokers) bw.write(this.getNode(x.index) + "\t" + x.value + "\n");
            bw.close();
        }

        // get top-1000 auth
        BufferedReader authBR = U.getBufferedReader(BROKERS);
        ArrayList<String> brokerTOP = new ArrayList<>();

        for (int i = 0; i < 1000 ; i++) {
            brokerTOP.add(authBR.readLine().split("\t")[0]);
        }
        return brokerTOP;


    }


}
