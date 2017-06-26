import it.stilo.g.algo.ComunityLPA;
import it.stilo.g.structures.WeightedGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 Created by aalto on 6/26/17 */

public class BinaryComunityLPA implements Runnable{

    private static final Logger logger = LogManager.getLogger(ComunityLPA.class);

    private IntOpenHashSet yPartisans;
    private IntOpenHashSet nPartisans;

    private static Random rnd;
    private WeightedGraph g;

    private int chunk;
    private int runner;
    private CountDownLatch barrier;

    private byte[] labels;
    private int[] list = null;

    private BinaryComunityLPA(WeightedGraph g, CountDownLatch cb, byte[] labels, int chunk, int runner,
                              IntOpenHashSet yes, IntOpenHashSet no) {
        this.g = g;
        this.runner = runner;
        this.barrier = cb;
        this.labels = labels;
        this.chunk = chunk;
        this.yPartisans = yes;
        this.nPartisans = no;
    }

    private boolean initList() {
        if (list == null) {
            // Partitioning over worker
            list = new int[(g.in.length / runner) + runner];

            int j = 0;

            for (int i = chunk; i < g.in.length; i += runner) {
                if (g.in[i] != null) {
                    if(yPartisans.contains(i))
                        labels[i] = 1;     //yes partisan
                    else if(nPartisans.contains(i))
                        labels[i] = 0;     //no partisan
                    else{
                        labels[i] = -1;    //not sided elector
                        list[j] = i;
                        j++;
                    }
                } else {
                    if (g.out[i] != null) {
                        if(yPartisans.contains(i))
                            labels[i] = 1;  //yes partisan
                        else if(nPartisans.contains(i))
                            labels[i] = 0;  //no partisan
                        else
                            labels[i] = -1; //not sided elector
                    } else {
                        labels[i] = -2;     //undefinable node since not belongs to any party and has no in-edge
                    }
                }
            }
            list = Arrays.copyOf(list, j);

            //Shuffle
            for (int i = 0; i < list.length; i++) {
                for (int z = 0; z < 10; z++) {
                    int randomPosition = rnd.nextInt(list.length);
                    int temp = list[i];
                    list[i] = list[randomPosition];
                    list[randomPosition] = temp;
                }
            }

            return true;
        }
        return false;
    }

    public void run() {
        if (!initList()) {
            for (int i = 0; i < list.length; i++) {
                int[] near = g.in[list[i]];
                byte[] nearLabs = new byte[near.length];
                for (int x = 0; x < near.length; x++) {
                    nearLabs[x] = labels[near[x]];
                }
                labels[list[i]] = bestLabel(nearLabs);
            }
        }
        barrier.countDown();
    }

    public static byte bestLabel(byte[] neighborhood) {
        Arrays.sort(neighborhood);
        byte best = -2;//-1;
        int maxCount = -1;
        int counter = 0;
        byte last = -2;
        for (int i = 0; i < neighborhood.length; i++) {
            if (maxCount > (neighborhood.length - i)) {
                //disadvantage not recoverable
                break;
            }

            if (neighborhood[i] == last) {
                counter++;
                if (counter > maxCount) {
                    maxCount = counter;
                    best = last;
                }
            } else {
                counter = 0;
                last = neighborhood[i];
            }
        }

        if (maxCount <= 1) {
            return neighborhood[rnd.nextInt(neighborhood.length)];
        }
        return best;
    }

    public static byte[] compute(final WeightedGraph g, double threshold, int runner, IntOpenHashSet yPartisans, IntOpenHashSet nPartisans) {

        BinaryComunityLPA.rnd = new Random(System.currentTimeMillis());

        byte[] labels = new byte[g.size];
        byte[] newLabels = labels;
        int iter = 0;

        long time = System.nanoTime();
        CountDownLatch latch = null;

        BinaryComunityLPA[] runners = new BinaryComunityLPA[runner];

        for (int i = 0; i < runner; i++) {
            runners[i] = new BinaryComunityLPA(g, latch, labels, i, runner, yPartisans, nPartisans);
        }

        ExecutorService ex = Executors.newFixedThreadPool(runner);

        do {
            iter++;
            labels = newLabels;
            newLabels = Arrays.copyOf(labels, labels.length);
            latch = new CountDownLatch(runner);

            //Label Propagation
            for (int i = 0; i < runner; i++) {
                runners[i].barrier = latch;
                runners[i].labels = newLabels;
                ex.submit(runners[i]);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.debug(e);
            }

        } while (smoothEnd(labels, newLabels, iter, threshold));

        System.out.println("\nConverged in "+iter+" iterations.");

        ex.shutdown();

        logger.info(((System.nanoTime() - time) / 1000000000d) + "\ts");
        return labels;
    }

    private static boolean smoothEnd(byte[] labels, byte[] newLabels, int iter, double threshold) {
        if (iter < 2) {
            return true;
        }

        int k = 3;

        if (iter > k) {
            int equality = 0;

            for (int i = 0; i < labels.length; i++) {
                if (labels[i] == newLabels[i]) {
                    equality++;
                }
            }
            double currentT = (equality / ((double) labels.length));

            System.out.println("iter: "+iter+"\tCurrentT: \t"+currentT);

            return !(currentT >= threshold);
        }
        return !Arrays.equals(labels, newLabels);
    }

}
