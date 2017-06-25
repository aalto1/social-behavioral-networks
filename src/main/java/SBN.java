import java.io.*;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.google.common.math.StatsAccumulator;
import it.unimi.dsi.fastutil.doubles.DoubleOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.seninp.jmotif.sax.SAXException;
import net.seninp.jmotif.sax.SAXProcessor;
import net.seninp.jmotif.sax.alphabet.NormalAlphabet;
import net.seninp.jmotif.sax.datastructure.SAXRecords;

import org.apache.lucene.document.Document;


import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * Created by aalto on 1/25/17.
 */



public abstract class SBN {
    private static final String STREAM_PATH = "stream"+ File.separator;

    static ArrayList<String> stopwords = new ArrayList<>(Arrays.asList(new String []{"","a","abbastanza","abbia","abbiamo","abbiano","abbiate","accanto","acciocché","ad","adesso","affinché","agl","agli","agosto","ah","ahimè","ai","al","alcune","alcuni","alcuno","all","alla","alle","allo","allora","almeno","alquanto","altre","altresì","altrettanto","altri","altrimenti","altro","altrove","anche","ancora","anni","anno","anzi","aperto","appunto","aprile","assai","assieme","attraverso","attuale","attualmente","avanti","avemmo","avendo","avere","avesse","avessero","avessi","avessimo","aveste","avesti","avete","aveva","avevamo","avevano","avevate","avevi","avevo","avrà","avrai","avranno","avrebbe","avrebbero","avrei","avremmo","avremo","avreste","avresti","avrete","avrò","avuta","avute","avuti","avuto","b","basta","ben","benché","bene","breve","buono","c","ce","cento","certamente","certo","che","chi","chiunque","chiuso","ci","ciascuno","cinquanta","cinque","ciò","cioè","circa","codesta","codesto","coi","col","colei","colui","come","comprare","comunque","con","consecutivi","consecutivo","contro","corto","cosa","cose","così","cotesta","cotesto","cui","d","da","dagl","dagli","dai","dai","dal","dall","dalla","dalle","dallo","data","davanti","decimo","degl","degli","dei","dei","del","dell","della","della","delle","dello","dentro","deve","devo","di","dicembre","dieci","dietro","difatti","differente","difficile","dissimile","diverso","domenica","dopo","doppio","dov","dove","dovrà","dovrebbe","dovunque","dovuto","due","dunque","durante","e","è","ebbe","ebbene","ebbero","ebbi","eccetto","ecco","ed","effettivamente","egli","eh","ella","entrambe","entrambi","era","erano","eravamo","eravate","eri","ero","esempio","essa","esse","essendo","essi","esso","esterno","f","faccia","facciamo","facciano","facciate","faccio","facemmo","facendo","facesse","facessero","facessi","facessimo","faceste","facesti","faceva","facevamo","facevano","facevate","facevi","facevo","facile","fai","falso","fanno","farà","farai","faranno","fare","farebbe","farebbero","farei","faremmo","faremo","fareste","faresti","farete","farò","febbraio","fece","fecero","feci","finanche","finché","fine","fino","forse","fosse","fossero","fossi","fossimo","foste","fosti","fra","frattanto","fu","fui","fummo","fuorché","fuori","furono","g","generalmente","gennaio","gente","già","giacché","giovedì","giu","giugno","gli","grande","h","ha","ha","hai","hanno","ho","ho","ho","i","il","impossibile","improbabile","in","indietro ","infatti","inoltre","insieme","insolito","insomma","insufficiente","insufficientemente","intanto","interno","intorno","inusuale","inutile","inutilmente","invece","invero","io","j","k","l","la","là","laddove","lavoro","le","lei","li","lì","lo","lontano","loro","luglio","lui","lunedì","lungo","m","ma","maggio","maggior","maggiore","mai","male","malgrado","martedì","marzo","me","mediante","meglio","meno","mentre","mercoledì","mi","mia","mie","miei","minor","minore","mio","modo","molta","molti","molto","n","naturalmente","ne","né","neanche","negl","negli","nei","nel","nell","nella","nelle","nello","nemmeno","neppure","nessun","nessuno","new","news","niente","no","noi","nome","non","nondimeno","nono","nonostante","nostra","nostre","nostri","nostro","novanta","nove","novembre","nulla","nuovamente","nuovi","nuovo","o","ogni","ognuno","oh","oltre","oltre","oppure","ora","orbene","orsù","ossia","ottanta","ottavo","otto","ottobre","ovunque","ovviamente","ovvio","p","parecchi","parecchio","peggio","per","perch","perché","perciò","perfino","pero","però","persino","persone","pertanto","pi","piccolo","pieno","piu","più","piuttosto","po","pochi","poco","poi","poiché","possibile","potrà","potrebbe","potuto","precedente","precedentemente","presso","presto","prima","primi","primo","probabile","probabilmente","promesso","pronto","propri","proprio","prossimo","può","purché","pure","q","qua","qual","qualche","qualcosa","qualcuno","quale","quali","qualora","qualunque","quando","quanta","quante","quanti","quanto","quantunque","quaranta","quarto","quasi","quattro","quell","quella","quelle","quelli","quello","questa","queste","questi","questo","qui","qui","quindi","quinto","r","reale","realmente","rispetto","s","sabato","salvo","sara","sarà","sarai","saranno","sarebbe","sarebbero","sarei","saremmo","saremo","sareste","saresti","sarete","sarò","scelta","scelto","se","sebbene","secondo","sei","sembra ","sembrava","semplice","semplicemente","sempre","senza","sessanta","sesto","settanta","sette","settembre","settimo","si","sì","sia","siamo","siano","siate","siete","siffatto","simile","soli","solitamente","solito","solo","soltanto","sono","sopra","soprattutto","sotto","specificamente","specifico","spesso","sta","stai","stando","stanno","stante","starà","starai","staranno","starebbe","starebbero","starei","staremmo","staremo","stareste","staresti","starete","starò","stati","stato","stava","stavamo","stavano","stavate","stavi","stavo","stemmo","stesse","stessero","stessi","stessimo","stesso","steste","stesti","stette","stettero","stetti","stia","stiamo","stiano","stiate","sto","su","sua","subito","successivamente","successivo","sue","sufficiente","sufficientemente","sugl","sugli","sui","sul","sull","sulla","sulle","sullo","suo","suoi","super","suvvia","t","tale","talvolta","tanto","tardi","te","tempo","terzo","ti","totale","totali","tra","tranne","tre","trenta","triplo","troppo","tu","tua","tue","tuo","tuoi","tuttavia","tutti","tutto","u","uguale","uguali","ulteriore","ultimi","ultimo","un","una","uno","urrà","usuale","utile","v","va","vai","vari","vario","ve","venerdì","venti","veramente","vero","verso","vi","vi","via","vicino","voi","voi","volte","vostra","vostre","vostri","vostro","vuoto","w","x","y","z"}));

    public static void main(String [] agrs) throws IOException, InterruptedException, SAXException {
        //LuceneIndexer.generateIndex(STREAM_PATH);
        ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", "270839361");
        //e_0_1();
        //saxBuilder();


    }


    public static void e_0_1() throws IOException, InterruptedException {
        BufferedReader br_names = getBufferedReader("data/0-1/screennames.txt");
        BufferedReader br_IDs = getBufferedReader("data/0-1/screennames_ID.txt");
        String name, id;

        while((name= br_names.readLine()) != null && (id= br_IDs.readLine()) != null){
            ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", id);
            classifyPolitician(documents, name, id);
        }

        /***Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("sed -i 's/stored,indexed,tokenized<tweetText://g' /home/aalto/IdeaProjects/social-behavioral-networks/out/yesTweets.txt");
        pr = rt.exec("sed -i 's/stored,indexed,tokenized<tweetText://g' /home/aalto/IdeaProjects/social-behavioral-networks/out/noTweets.txt");***/


    }

    public static void classifyPolitician(ArrayList<Document> documents, String name, String id) throws IOException {
        ArrayList<String> yHash = new ArrayList<>(); yHash.add("iovotosi"); yHash.add("bastaunsi"); yHash.add("sì"); yHash.add("si"); yHash.add("iohovotatosi");
        ArrayList<String> nHash = new ArrayList<>(); nHash.add("iovotono");  nHash.add("iodicono"); nHash.add("no"); nHash.add("votano");
        ArrayList<String> finalHash;
        int no  = 0; int yes = 0;
        for(Document d : documents){
            for(String s: d.getValues("hashtags")){
                if(yHash.contains(s.toLowerCase())) yes++;
                if(nHash.contains(s.toLowerCase())) no++;
            }
        }

        String supporter; String tweet; String distribution;
        if(no>yes){
            //the user has more no hashtags
            supporter = "out/noSupporters.txt"; tweet = "out/noTweets.txt"; distribution = "out/noDistribution.txt";
            finalHash = nHash;

        }else{
            //the user has more yes hashtags
            supporter   = "out/yesSupporters.txt"; tweet = "out/yesTweets.txt"; distribution = "out/yesDistribution.txt";
            finalHash = yHash;
        }

        BufferedWriter bwSupporter, bwTweet, bwDistribution;

        bwSupporter = getBufferedWriter(supporter, true);
        bwSupporter.write(String.format("%s,%s,", name, id)+"\n");

        bwDistribution = getBufferedWriter(distribution, true);
        bwTweet = getBufferedWriter(tweet, true);

        String [] tweetText;
        for (Document d: documents) {
            Boolean relevant = false;
            for(String s: d.getValues("hashtags")) {
                if (finalHash.contains(s.toLowerCase())) {
                    relevant = true; break;
                }
            }
            if(relevant){
                //bwTweet.write(String.format("%s,%s,%s", name, id, d.getField("tweetText")) + "\n");
                //tweetText = d.getField("tweetText").toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                tweetText = U.stem(d.getField("tweetText").toString());
                bwTweet.write(Arrays.toString(tweetText).replace("[", "").replace("]", "") + "\n");
                Date date = new Date(Long.parseLong(d.get("creationDateMills")));
                bwDistribution.write(new SimpleDateFormat("dd_MM_yyyy").format(date) + "\n");
            }
        }
        bwSupporter.close(); bwTweet.close(); bwDistribution.close();
    }


    /*** EXERCISE 1.2 ***/


    public static void saxBuilder() throws IOException, SAXException {
        String [] nTop1000 = getTop1000("out/noTweets.txt", "out/nTop1000.txt"); String [] yTop1000 = getTop1000("out/yesTweets.txt", "out/yTop1000.txt");

        Object2ObjectOpenHashMap<String,String> sax;
        Object2ObjectOpenHashMap [] b = timeSeriesBuilder(array2list(nTop1000));
        System.out.println(b[0]);
        sax = ComputeSAXstrings(b[0], 2);
        storeHashMap(matchingSax(sax, 3), getBufferedWriter("out/no_cluster", false));
        sax = ComputeSAXstrings(b[1], 2);
        storeHashMap(matchingSax(sax, 3), getBufferedWriter("out/yes_cluster", false));

    }

    public static Object2ObjectOpenHashMap [] timeSeriesBuilder(ArrayList<String> top, int grain) throws IOException {
        BufferedReader br_IDs = getBufferedReader("data/0-1/screennames_ID.txt");
        String [] tweetText; String id;

        Object2ObjectOpenHashMap<String,ArrayList<Double>>[] timeSerieses = new Object2ObjectOpenHashMap [24/grain];
        for (int i = 0; i < timeSerieses.length; i++)
            timeSerieses[i] = new Object2ObjectOpenHashMap<>();



        while((id= br_IDs.readLine()) != null){
            ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", id);

            for (Document d: documents) {
                tweetText = U.stem(d.getField("tweetText").toString());
                Double date = Double.parseDouble(new SimpleDateFormat("ddMMyyyyhhmm").format(new Date(Long.parseLong(d.get("creationDateMills")))));
                String sdate = String.valueOf(date);

                Float hour = Float.parseFloat(sdate.substring(sdate.length()-7, sdate.length()-5));


                for (String word : tweetText) {
                    if(top.contains(word)){
                        timeSerieses[(int)Math.floor(hour/grain)].putIfAbsent(word, new ArrayList<>());
                        timeSerieses[(int)Math.floor(hour/grain)].get(word).add(date);
                    }
                }
            }
        }
        return timeSerieses;
    }

    public static Object2ObjectOpenHashMap<String, String> ComputeSAXstrings(Object2ObjectOpenHashMap<String,ArrayList<Double>> timeSeries, int alphabetSize) throws SAXException{

        Object2ObjectOpenHashMap<String, String> SAX = new Object2ObjectOpenHashMap<>();
        for(String key : timeSeries.keySet()){
            ArrayList<Double> normalizedTS = normalize(timeSeries.get(key));
            //System.out.println(normalizedTS);
            timeSeries.replace(key, normalizedTS);
            String s = performSAX(normalizedTS, alphabetSize, 0.01); // compute the SAX string
            //System.out.println(s);
            SAX.put(key, s);
        }

        return SAX;
    }

    private static ArrayList<Double> normalize(ArrayList<Double> timeSeries){
        // given an ArrayList, normalize it using the formula (x - mu)/sigma
        if(timeSeries.size()>1) {
            StatsAccumulator acc = new StatsAccumulator();
            acc.addAll(timeSeries);
            double avg = acc.mean();
            double sd = acc.sampleStandardDeviation();
            for (int i = 0; i < timeSeries.size(); i++) {
                timeSeries.set(i, (timeSeries.get(i) - avg) / sd);
            }
        }
        return timeSeries;

    }


    /**
     *
     * @param ts The time series as ArrayList of doubles
     * @param size The alphabet size that must be used in the SAX approximation
     * @param threshold
     * @return The SAX String related to the given time series
     * @throws SAXException
     */
    private static String performSAX(ArrayList<Double> ts, int size, double threshold) throws SAXException{

        String sax= "";
        NormalAlphabet na = new NormalAlphabet();
        SAXProcessor sp = new SAXProcessor();
        double[] timeS  = new double[ts.size()];
        // give a time series, perform the related SAX string
        for (int i=0; i<ts.size(); i++) {
            timeS[i] = (double)ts.get(i);
        }
        SAXRecords res = sp.ts2saxByChunking(timeS, timeS.length,na.getCuts(size), threshold);
        sax = res.getSAXString("");
        return sax;

    }

    public static Object2ObjectOpenHashMap<String, ArrayList<String>> matchingSax(Object2ObjectOpenHashMap<String, String> sax_ts, int thresold){

        Object2ObjectOpenHashMap<String, ArrayList<String>> out = new Object2ObjectOpenHashMap();
        for(String key : sax_ts.keySet()){
            out.putIfAbsent(sax_ts.get(key), new ArrayList<>());
            out.get(sax_ts.get(key)).add(key);
        }
        out.keySet().stream().filter(key -> out.get(key).size() < thresold).forEach(out::remove);
        System.out.println(out);
        return out;

    }



    public static String [] getTop1000(String FILENAME, String OUTFILE) throws IOException {
        if(!Files.exists(Paths.get(OUTFILE))){
            BufferedWriter bw = getBufferedWriter(OUTFILE, false);
            Object2IntOpenHashMap<String> map = new Object2IntOpenHashMap<>();
            BufferedReader br = getBufferedReader(FILENAME);
            String s;
            String[][] aux = new String[2][];

            while ((s = br.readLine()) != null) {
                for (String word : s.split(" ")) {
                    word = word.toLowerCase();
                    if (map.putIfAbsent(word, 1) != null && !stopwords.contains(word)) map.merge(word, 1, Integer::sum);
                }
            }
            String[][] words = new String[map.size()][2];
            aux[0] = map.keySet().toArray(new String[map.size()]);
            aux[1] = Arrays.toString(map.values().toArray()).split("[\\[\\]]")[1].split(", ");

            for (int i = 0; i < words.length; i++) {
                words[i][0] = aux[0][i];
                words[i][1] = aux[1][i];
            }
            System.out.println(Arrays.deepToString(words));

            Arrays.sort(words, new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    return Integer.compare(Integer.parseInt(o2[1]), Integer.parseInt(o1[1]));
                }
            });
            //System.out.println(Arrays.deepToString(words));

            for (int i = 0; i < 800; i++) {
                bw.write(words[i][0] + "\n");
                //System.out.println(Arrays.toString(words[i]));
            }
            bw.close();
        }

        BufferedReader br = getBufferedReader(OUTFILE);
        String s; String[] top1000 = new String[800];
        for(int i = 0; (s=br.readLine())!=null; i++ )  top1000[i]=s;


        return top1000;
    }



    /*** EXERCISE 1.3 ***/

    public static void CoOccurenceGraph() throws IOException, InterruptedException {
        coOccurence(fetchHashMap("out/no_cluster"), "out/noLCC", "noKCore");
        coOccurence(fetchHashMap("out/no_cluster"), "out/yesLCC", "out/yesKCore");
    }

    public static void coOccurence(Object2ObjectOpenHashMap<Integer, ArrayList<String>> clusters, String LCCFILENAME, String KCOREFILENAME) throws IOException, InterruptedException {
        ArrayList<ArrayList<Integer>> LCCs = new ArrayList<>(); ArrayList<ArrayList<String>> KCores = new ArrayList<>();
        BufferedWriter LCCbw = getBufferedWriter(LCCFILENAME, false),  KCOREbw = getBufferedWriter(KCOREFILENAME, false);
        fastUGraph g;

        for(ArrayList<String> terms: clusters.values()){
            g = new fastUGraph(terms.size(), terms, "out/yesTweets.txt");
            g = g.edgePruner(3);
            LCCs.add(g.getLCC());
            KCores.add(g.KCore());
        }

        for (ArrayList<Integer> LCC: LCCs) U.storeArrayList(LCC, LCCbw);
        for (ArrayList<String> KCore : KCores) U.storeArrayList(KCore, KCOREbw);
    }



    /*** EXERCISE 0.4  NON HO CAPITO BENE COSA BISOGNA FARE***/

    public static void InnerMostDistribution() throws IOException {
        ArrayList<String> yesCore = U.fetchArrayList("out/noCore");
        ArrayList<String> noCore = U.fetchArrayList("out/yesCore");

        Object2ObjectOpenHashMap [] yesTS = timeSeriesBuilder(yesCore, 3);
        Object2ObjectOpenHashMap [] noTS = timeSeriesBuilder(noCore, 3);

        double significance = 0.5;

        for (int i = 0; i < yesTS.length ; i++) {
            for (int j = 0; j < noTS.length ; j++) {
                if(crossCorrelation(yesTS[i], noTS[i])>significance);
            }
        }


    }


    /*** EXERCISE 1.1 ***/

    public static DoubleOpenHashSet getPoliticians(String FILENAME) throws IOException {
        String line; String [] words;
        DoubleOpenHashSet politicians = new DoubleOpenHashSet();

        BufferedReader br = getBufferedReader(FILENAME);
        while((line=br.readLine())!=null){
            words = line.split(",");
            politicians.add(Double.parseDouble(words[1]));
        }

        return politicians;
    }
    public static void getPartisans(ArrayList<String> politicians, ArrayList<String> keywords) throws IOException {

        ObjectOpenHashSet<String> ids = LuceneIndexer.userIDbyQuery("mentions", politicians);
        ids.addAll(LuceneIndexer.userIDbyQuery("mentions", politicians));

        System.out.println(ids.size());
        U.storeArrayList(new ArrayList<>(ids), getBufferedWriter("out/musers", false));
    }

    public static void getPartisansTweets(ArrayList<String> partisans) throws IOException {
        String [] tweetText; BufferedWriter bwTweet = getBufferedWriter("out/partisanTweets", false);
        ArrayList<String[]> partisanTweets = new ArrayList<>();
        for (String partisan : partisans) {
            ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", partisan);
            for (Document d: documents) {
                tweetText = U.stem(d.getField("tweetText").toString());
                U.storeArrayList(Arrays.asList(tweetText), bwTweet);

            }
        }
        bwTweet.close();
    }

    public static void classifyPartisan(){

    }













    /*** AUXILIARY FUNCTIONS ***/
    static BufferedReader getBufferedReader(String FILENAME) throws FileNotFoundException {
        return new BufferedReader(new FileReader(FILENAME));
    }

    static BufferedWriter getBufferedWriter(String FILENAME, Boolean append) throws IOException {
        if(append == true){
            return new BufferedWriter(new FileWriter(FILENAME, true));
        }else {
            return new BufferedWriter(new FileWriter(FILENAME));
        }
    }

    static void storeHashMap(Object2ObjectOpenHashMap<String,ArrayList<String>> map, BufferedWriter bw) throws IOException {
        for(String key : map.keySet()){
            System.out.println(map.get(key));
            bw.write(key+":"+map.get(key).toString().replace("[", "").replace("]","").replace(" ","")+"\n");
        }
        bw.close();
    }

    static Object2ObjectOpenHashMap<Integer, ArrayList<String>> fetchHashMap(String FILENAME) throws IOException {
        BufferedReader br = getBufferedReader(FILENAME);
        String line; String [] s; int k = 0;
        Object2ObjectOpenHashMap<Integer, ArrayList<String>> hm = new Object2ObjectOpenHashMap<>();

        while((line=br.readLine())!=null){
            s = line.split(":");
            hm.put(k++, array2list(s[1].split(",")));
        }
        br.close();
        return hm;
    }

    static ArrayList<String> array2list(String [] a){
        ArrayList<String> l = new ArrayList<>();
        for (String s: a) {
            l.add(s);
        }
        return l;
    }
}


/***
 * public static Object2ObjectOpenHashMap [] timeSeriesBuilder(String [] nTop1000, String [] yTop1000) throws IOException {
 BufferedReader br_IDs = getBufferedReader("data/0-1/screennames_ID.txt");
 ArrayList<String> yHash = new ArrayList<>(); yHash.add("iovotosi"); yHash.add("bastaunsi"); yHash.add("sì"); yHash.add("si"); yHash.add("iohovotatosi");
 ArrayList<String> nHash = new ArrayList<>(); nHash.add("iovotono");  nHash.add("iodicono"); nHash.add("no"); nHash.add("votano");
 String [] tweetText; String id;
 Object2ObjectOpenHashMap<String,ArrayList<Double>> timeSeries00_11 = new Object2ObjectOpenHashMap<>();
 Object2ObjectOpenHashMap<String,ArrayList<Double>> timeSeries12_23 = new Object2ObjectOpenHashMap<>();
 ArrayList<Double> aux;

 while((id= br_IDs.readLine()) != null){
 ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", id);

 for (Document d: documents) {
 Boolean relevant = false;
 for(String s: d.getValues("hashtags")) {
 if(yHash.contains(s.toLowerCase()) | nHash.contains(s.toLowerCase())) {
 relevant = true; break;
 }
 }
 if(relevant){
 tweetText = d.getField("tweetText").toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
 Double date = Double.parseDouble(new SimpleDateFormat("ddMMyyyyhhmm").format(new Date(Long.parseLong(d.get("creationDateMills")))));
 //Double date = Double.parseDouble(d.get("creationDateMills"));
 String sdate = String.valueOf(date);
 if(Integer.parseInt(sdate.substring(sdate.length()-7, sdate.length()-5))<12) {
 for (String word : tweetText) {
 timeSeries00_11.putIfAbsent(word, new ArrayList<>());
 timeSeries00_11.get(word).add(date);
 }
 }else{
 for (String word: tweetText) {
 timeSeries12_23.putIfAbsent(word, new ArrayList<>());
 timeSeries12_23.get(word).add(date);
 }
 }
 }
 }

 }
 System.out.println(timeSeries00_11.size());
 System.out.println(timeSeries12_23.size());
 System.out.println(timeSeries00_11.keySet());
 Object2ObjectOpenHashMap [] ts = new Object2ObjectOpenHashMap [] {timeSeries00_11,timeSeries12_23};
 return ts;
 }***/