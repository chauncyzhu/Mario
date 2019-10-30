package util;

import java.util.ArrayList;
import java.util.List;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author timbrys
 */
public class Util {
    
    public static double max(double[] data){
        double max = data[0];
        for(int i=1; i<data.length; i++){
            max = Math.max(max, data[i]);
        }
        return max;
    }

    public static ArrayList<Integer> argMaxes(List<Double> v){
        ArrayList<Integer> ibest = new ArrayList<Integer>();
        ibest.add(0);
        double best = v.get(0);
        for(int i=1; i<v.size(); i++){
            if(v.get(i) > best){
                best = v.get(i);
                ibest.clear();
                ibest.add(i);
            } else if (v.get(i) == best){
                ibest.add(i);
            }
        }
        return ibest;
    }
    
    public static ArrayList<Integer> argMaxes(double[] v){
        ArrayList<Integer> ibest = new ArrayList<Integer>();
        ibest.add(0);
        double best = v[0];
        for(int i=1; i<v.length; i++){
            if(v[i] > best){
                best = v[i];
                ibest.clear();
                ibest.add(i);
            } else if (v[i] == best){
                ibest.add(i);
            }
        }
        return ibest;
    }

    public static ArrayList<Integer> argMins(List<Double> v){
        ArrayList<Integer> ilowest = new ArrayList<Integer>();
        ilowest.add(0);
        double low = v.get(0);
        for(int i=1; i<v.size(); i++){
            if(v.get(i) < low){
                low = v.get(i);
                ilowest.clear();
                ilowest.add(i);
            } else if (v.get(i) == low){
                ilowest.add(i);
            }
        }
        return ilowest;
    }


    public static ArrayList<Integer> argMins(double[] v){
        ArrayList<Integer> ilowest = new ArrayList<Integer>();
        ilowest.add(0);
        double low = v[0];
        for(int i=1; i<v.length; i++){
            if(v[i] < low){
                low = v[i];
                ilowest.clear();
                ilowest.add(i);
            } else if (v[i] == low){
                ilowest.add(i);
            }
        }
        return ilowest;
    }
    
    public static int argMax(double[] v){
        ArrayList<Integer> best = argMaxes(v);
        return best.get(RNG.randomInt(best.size()));
    }

    public static int argMax(List<Double> v){
        ArrayList<Integer> best = argMaxes(v);
        return best.get(RNG.randomInt(best.size()));
    }

    public static int argMin(double[] v){
        ArrayList<Integer> best = argMins(v);
        return best.get(RNG.randomInt(best.size()));
    }

    public static int argMin(List<Double> v){
        ArrayList<Integer> best = argMins(v);
        return best.get(RNG.randomInt(best.size()));
    }
    
    public static int argMax(int[] v){
        ArrayList<Integer> ibest = new ArrayList<Integer>();
        ibest.add(0);
        int best = v[0];
        for(int i=0; i<v.length; i++){
            if(v[i] > best){
                best = v[i];
                ibest.clear();
                ibest.add(i);
            } else if (v[i] == best){
                ibest.add(i);
            }
        }
        return ibest.get(RNG.randomInt(ibest.size()));
    }
    
    public static boolean amongstArgMax(int arg, double[] v){
        ArrayList<Integer> ibest = new ArrayList<Integer>();
        ibest.add(0);
        double best = v[0];
        for(int i=0; i<v.length; i++){
            if(v[i] > best){
                best = v[i];
                ibest.clear();
                ibest.add(i);
            } else if (v[i] == best){
                ibest.add(i);
            }
        }
        return ibest.contains(arg);
    }
    
    public static double closestToZero(double arg0, double arg1){
        if(Math.abs(arg0) < Math.abs(arg1)){
            return arg0;
        } else {
            return arg1;
        }
    }
    
    public static boolean contains(int[] array, int element){
        for(int i=0; i<array.length; i++){
            if(array[i] == element) {
                return true;
            }
        }
        return false;
    }
    
    public static double[] means(double[][] stats) {
        double[] means = new double[stats[0].length];
        for (int j = 0; j < stats[0].length; j++) {
            for (double[] stat : stats) {
                means[j] += stat[j];
            }
            means[j] = 1.0 * means[j] / (stats.length);
        }
        return means;
    }
}
