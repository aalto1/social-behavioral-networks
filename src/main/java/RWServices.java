/**
 * Created by aalto on 6/3/17.
 */


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract interface RWServices {

    public static ArrayList<String> readList(String filename)
            throws FileNotFoundException, IOException {

        ArrayList<String> list = new ArrayList();
        BufferedReader in = new BufferedReader(new FileReader(filename));

        String line = "";
        while ((line = in.readLine()) != null) {
            list.add(line);
        }

        System.out.println("Reading \"" + filename + "\"......done!");

        return list;
    }

    public static ArrayList<String> readListUnique(String filename)
            throws FileNotFoundException, IOException {

        HashSet<String> list = new HashSet();
        BufferedReader in = new BufferedReader(new FileReader(filename));

        String line = "";
        while ((line = in.readLine()) != null) {
            if(!list.contains(line))
                list.add(line);
        }

        System.out.println("Reading \"" + filename + "\"......done!");

        return new ArrayList(list);
    }

    public static HashMap<Integer,ArrayList<String>> readClusters(String filename)
            throws FileNotFoundException, IOException {

        System.out.println("Reading clusters file in: "+filename);

        HashMap<Integer,ArrayList<String>> outMap = new HashMap();
        TextCleaner tc = new TextCleaner();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line = "";
        int counterID = 0;
        while ((line = in.readLine()) != null) {
            counterID++;
            line = line.substring(line.indexOf("[")+1,line.indexOf("]"));
            String[] tokens = tc.cleanAndTokenize(line);
            ArrayList<String> tokensArray = new ArrayList(Arrays.asList(tokens));
            outMap.put(counterID,tokensArray);
        }

        return outMap;
    }

    public static void writeString(String s, String filename){

        PrintWriter writer = null;
        try{
            writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
            writer.println(s);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }finally{
            if(writer != null)
                writer.close();
        }
    }

    public static void writeList(ArrayList<String> list, String filename) throws IOException{

        File file = new File(filename);
        if(file.exists())
            file.delete();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(), true)));
        list.forEach(x -> {writer.println(x);});
        writer.close();
        System.out.println("\""+filename+"\" file created.");
    }

    public static void writeSubgroupFromMap(HashMap<Integer,ArrayList<String>> map, String filename) throws IOException{
        File file = new File(filename);
        if(file.exists())
            file.delete();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
        ArrayList<String> terms = new ArrayList();
        for (Map.Entry<Integer, ArrayList<String>> entry : map.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            terms.addAll(value);
        }

        terms.forEach(x -> {writer.println(x);});
        writer.close();

        System.out.println("\""+filename+"\" file created.");
    }

    public static void writeStringMap(Map<String, ArrayList<String>> map, String filename) throws IOException {

        File file = new File(filename);
        if(file.exists())
            file.delete();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));

        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            writer.println(key + ":" + value);
        }
        writer.close();

        System.out.println("\""+filename+"\" file created.");

    }

    public static void writeIntegerMap(Map<Integer, ArrayList<String>> map, String filename) throws IOException {

        File file = new File(filename);
        if(file.exists())
            file.delete();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));

        for (Map.Entry<Integer, ArrayList<String>> entry : map.entrySet()) {
            int key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            writer.println(key + ":" + value);
//            System.out.println(key + ":" + value);
        }
        writer.close();

        System.out.println("\""+filename+"\" file created.");


    }

    public static void aggregateTweets(String outPath, String filenameOUT, String yesTweetsPath, String noTweetsPath)
            throws FileNotFoundException, IOException{

        File f = new File(outPath+filenameOUT);
        if(f.exists())
            f.delete();

        BufferedReader in = new BufferedReader(new FileReader(yesTweetsPath));

        String line = "";
        while((line = in.readLine()) != null){
            RWServices.writeString(line, outPath+filenameOUT);
        }
        in = new BufferedReader(new FileReader(noTweetsPath));
        line = "";
        while((line = in.readLine()) != null){
            RWServices.writeString(line, outPath+filenameOUT);
        }
    }

    public static HashMap<String, Double> readMap(String filename) throws FileNotFoundException, IOException{

        HashMap<String, Double> out = new HashMap();
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line = "";
        while((line = br.readLine()) != null){
            String[] lineArray = line.split("\t");
            out.put(lineArray[0], Double.parseDouble(lineArray[1]));
        }
        return out;

    }

    public static LinkedHashMap<String, Double> readMapSorted(String filename) throws FileNotFoundException, IOException{

        LinkedHashMap<String, Double> out = new LinkedHashMap();
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line = "";
        while((line = br.readLine()) != null){
            String[] lineArray = line.split("\t");
            out.put(lineArray[0], Double.parseDouble(lineArray[1]));
        }
        return out;

    }

    public static int countLines(String fileName) throws FileNotFoundException, IOException{

        int out=0;
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        while((br.readLine()) != null){
            out +=1;
        }
        return out;

    }
    public static void writeMap(HashMap<String, Double> h, String fileName) throws IOException{

        File file = new File(fileName);
        if(file.exists())
            file.delete();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(), true)));
        for (Map.Entry<String, Double> entry : h.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();
            writer.println(key + "\t" + value);

        }
        writer.close();
        System.out.println("\""+fileName+"\" file created.");



    }


}