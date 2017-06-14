import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import javafx.beans.binding.ObjectExpression;
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

    public static void main(String [] agrs) throws IOException, InterruptedException {
        //LuceneIndexer.generateIndex(STREAM_PATH);
        ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", "270839361");
        //e_0_1();
        saxBuilder();

    }


    public static void e_0_1() throws IOException, InterruptedException {
        BufferedReader br_names = getBufferedReader("data/0-1/screennames.txt");
        BufferedReader br_IDs = getBufferedReader("data/0-1/screennames_ID.txt");
        String name, id;

        while((name= br_names.readLine()) != null && (id= br_IDs.readLine()) != null){
            ArrayList<Document> documents = LuceneIndexer.queryOnField("userID", id);
            classifyUser(documents, name, id);
        }

        /***Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("sed -i 's/stored,indexed,tokenized<tweetText://g' /home/aalto/IdeaProjects/social-behavioral-networks/out/yesTweets.txt");
        pr = rt.exec("sed -i 's/stored,indexed,tokenized<tweetText://g' /home/aalto/IdeaProjects/social-behavioral-networks/out/noTweets.txt");***/


    }

    public static void classifyUser(ArrayList<Document> documents, String name, String id) throws IOException {
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
                tweetText = d.getField("tweetText").toString().replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                bwTweet.write(Arrays.toString(tweetText) + "\n");
                Date date = new Date(Long.parseLong(d.get("creationDateMills")));
                bwDistribution.write(new SimpleDateFormat("dd_MM_yyyy").format(date) + "\n");
            }
        }
        bwSupporter.close(); bwTweet.close(); bwDistribution.close();
    }


    /*** EXERCISE 1.2 ***/


    public static void saxBuilder() throws IOException {
        String [] nTop1000 = getTop1000("out/noTweets.txt", "out/nTop1000.txt"); String [] yTop1000 = getTop1000("out/yesTweets.txt", "out/yTop1000.txt");
        timeSeriesBuilder(nTop1000, yTop1000);
        System.out.println(Arrays.toString(nTop1000));

    }

    public static void timeSeriesBuilder(String [] nTop1000, String [] yTop1000) throws IOException {
        BufferedReader br_IDs = getBufferedReader("data/0-1/screennames_ID.txt");
        ArrayList<String> yHash = new ArrayList<>(); yHash.add("iovotosi"); yHash.add("bastaunsi"); yHash.add("sì"); yHash.add("si"); yHash.add("iohovotatosi");
        ArrayList<String> nHash = new ArrayList<>(); nHash.add("iovotono");  nHash.add("iodicono"); nHash.add("no"); nHash.add("votano");
        String [] tweetText; String id;
        Object2ObjectOpenHashMap<String,ArrayList<Integer>> timeSeries = new Object2ObjectOpenHashMap<>();
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
                    Integer date = Integer.parseInt(new SimpleDateFormat("ddMMyyyy").format(new Date(Long.parseLong(d.get("creationDateMills")))));
                    //Double date = Double.parseDouble(d.get("creationDateMills"));
                    for (String word: tweetText) {
                        timeSeries.putIfAbsent(word, new ArrayList<Integer>());
                        timeSeries.get(word).add(date);

                    }
                }
            }

        }
        System.out.println(timeSeries.get("no"));
    }



    /**
     *
     * @param ts The time series as ArrayList of doubles
     * @param size The alphabet size that must be used in the SAX approximation
     * @param threshold
     * @return The SAX String related to the given time series
     * @throws SAXException
     */
    private String performSAX(ArrayList<Double> ts, int size, double threshold) throws SAXException{

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

            for (int i = 0; i < 500; i++) {
                bw.write(words[i][0] + "\n");
                //System.out.println(Arrays.toString(words[i]));
            }
            bw.close();
        }

        BufferedReader br = getBufferedReader(OUTFILE);
        String s; String[] top1000 = new String[500];
        for(int i = 0; (s=br.readLine())!=null; i++ )  top1000[i]=s;


        return top1000;
    }

    /*** EXERCISE 1.3 ***/

    public static void co_Occurence_Graph(){

    }



    /*** EXERCISE 1.4 ***/



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


}
