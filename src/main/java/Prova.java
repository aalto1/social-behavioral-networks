import java.util.*;

/**
 * Created by aalto on 3/29/17.
 */
public class Prova {

    public static void main(String [] args){
        int lunghezza = 100000;
        ArrayList<Integer> list = new ArrayList<Integer>(lunghezza);
        for (int i=0; i<lunghezza; i++){
            list.add((int)(Math.random()*100000000 + 1));
        }
        int counter2 = 0;
        for (int i = 0; true ; i++) {
            Collections.shuffle(list);
            int counter = 0;
            int max = 0;
            for (int a: list
                    ) {
                if(a>max){
                    counter++;
                    max = a;
                }
            }
            counter2 += counter;

            System.out.println(counter2 +" : " + Math.log(lunghezza)*i);
        }

    }
}
