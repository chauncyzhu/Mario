/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package experiment;

import java.util.ArrayList;
import util.RNG;

/**
 *
 * @author timbrys
 */
public class Results {
    protected double[] result;
    protected int[] steps;
    protected double[][] results;
    
    public Results(int experiments, int episodes){
        this.steps = new int[experiments];
        this.results = new double[experiments][episodes];
    }
    
    public void record(int ex, int ep, double[] res){
        steps[ex] = (int)res[0];
        results[ex][ep] = res[1];
    }
    
    public double result(int ex, int ep){
        return results[ex][ep];
    }
    
    public int[] steps(){
        return steps;
    }
    
    public double[] results(){
        return means(results);
    }
    
    public static double sum(double[] res){
        double sum = 0.0;
        for(int i=0; i<res.length; i++){
            sum += res[i];
        }
        return sum;
    }
    
    public static void printArray(int[][] matrix, int which){
        System.out.print("figure; plot([");
        for(int i=0; i<matrix[0].length; i++){
            System.out.print(1.0*matrix[which][i]/matrix[3][i] + ",");
        }
        System.out.println("]);");
    }
    
    
    public static void printMatrix(int[][][] matrix){
        System.out.print("figure; surf([");
        for(int i=0; i<matrix[0].length; i++){
            System.out.print("[");
            for(int j=0; j<matrix[0][i].length; j++){
                int ob = -1;
                
                int best = -1;
                ArrayList<Integer> ibestp = new ArrayList<Integer>();
                for(int o=0; o<3; o++){
                    if(matrix[o][i][j] >= best){
                        if(matrix[o][i][j] > best){
                            ibestp.clear();
                        }
                        best = matrix[o][i][j];
                        ibestp.add(o);
                    }
                }

                ob = ibestp.get(RNG.randomInt(ibestp.size()));
                System.out.print(ob + ",");
            }
            System.out.print("];");
        }
        System.out.println("]);");
    }
    
    public static void printMatrix(int[][] matrix, int[][] total){
        System.out.print("figure; surf([");
        for(int i=0; i<matrix.length; i++){
            System.out.print("[");
            for(int j=0; j<matrix[i].length; j++){
                if(total[i][j] == 0){
                    System.out.print(0.0 + ",");
                } else {
                    System.out.print(1.0*matrix[i][j]/total[i][j] + ",");
                }
            }
            System.out.print("];");
        }
        System.out.println("]);");
    }
    
    public static double[] means(double[][] stats){
        double[] means = new double[stats[0].length];
        for(int j=0; j<means.length; j++){
            for(int i=0; i<stats.length; i++){
                means[j] += stats[i][j];
            }
            means[j] = 1.0*means[j]/(stats.length);
        }
        return means;
    }
    
    public static double[] xs(double[][] stats, int every){
        double[] x = new double[stats[0].length/every];
        for(int i=0; i<stats[0].length; i+=every){
            x[i/every] = i;
        }
        return x;
    }
    
    public static double[] ys(double[] stats, int every){
        double[] x = new double[stats.length/every];
        for(int i=0; i<stats.length; i+=every){
            x[i/every] = stats[i];
        }
        return x;
    }
    
    public static double[][] stds(double[][] stats, int every){
        double[] mean = means(stats);
        
        double[] stdL = new double[stats[0].length/every];
        double[] stdU = new double[stats[0].length/every];
        int counterL = 0;
        int counterU = 0;
        
        for(int j=0; j<stats[0].length; j+=every){
            for(int i=0; i<stats.length; i++){
                if(stats[i][j] < mean[j]){
                    stdL[j/every] += Math.pow(stats[i][j] - mean[j], 2.0);
                    counterL++;
                } else {
                    stdU[j/every] += Math.pow(stats[i][j] - mean[j], 2.0);
                    counterU++;
                }
            }
            if(counterL > 0){
                stdL[j/every] = Math.sqrt(stdL[j/every]/counterL);
            }
            if(counterU > 0){
                stdU[j/every] = Math.sqrt(stdU[j/every]/counterU);
            }
            
//            stdL[j/every] = stdL[j/every];
//            stdU[j/every] = stdU[j/every];
        }
        
        return new double[][]{stdL, stdU};
    }
    
    public static double[] runningAVG(double[] data, int n){
        double[] data2 = new double[data.length-n];
        double avg = 0.0;
        for(int i=0; i<n; i++){
            avg += data[i];
        }
        data2[0] = avg/n;
        for(int i=1; i<data.length-n; i++){
            avg -= data[i-1] + data[i+n-1];
            data2[i] = avg/n;
        }
        return data2;
    }
    
}
