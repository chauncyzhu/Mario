package problem.mario.utils;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import util.RNG;

import java.util.ArrayList;

/**
 * Statistical utilities.
 */
public class Stats {
	
	/** Compute a t-statistic for two samples that may differ in size and variance. */
	public static double t(double[] x1, double[] x2) {

		int n1 = x1.length;
		int n2 = x2.length;

		double u1 = average(x1);
		double u2 = average(x2);
		
		double v1 = variance(x1, u1);
		double v2 = variance(x2, u2);
		
		return (u1 - u2) / Math.sqrt(v1/n1 + v2/n2);
	}
	
	/** Compute the degrees of freedom for the above t-test. */
	public static double dof(double[] x1, double[] x2) {

		int n1 = x1.length;
		int n2 = x2.length;

		double u1 = average(x1);
		double u2 = average(x2);
		
		double v1 = variance(x1, u1);
		double v2 = variance(x2, u2);
		
		double term1 = v1/n1;
		double term2 = v2/n2;

		return Math.pow(term1+term2, 2) / (term1*term1/(n1-1) + term2*term2/(n2-1));
	}

	/** Average an array of doubles. */
	public static double average(double[] array) {
		double sum = 0;
		for (double x : array) {
			sum += x;
		}
		return sum / array.length;
	}

	/** Estimate variance in an array of doubles. */
	public static double variance(double[] array, double mean) {
		double sum = 0;
		for (double x : array) {
			double v = x - mean;
			sum += v*v;
		}
		return sum / (array.length - 1);
	}
	
	public static double absdeviation(double[] array, double mean) {
		double sum = 0;
		for (double x : array) {
			double v = x - mean;
			sum += Math.abs(v);
		}
		return sum / (array.length);
	}
	
	/** Find the minimum in an array of doubles. */
	public static double min(double[] array) {
		double min = array[0];
		for (double x : array) {
			if (x < min)
				min = x;
		}
		return min;
	}
	
	/** Find the maximum in an array of doubles. */
	public static double max(double[] array) {
		double max = array[0];
		for (double x : array) {
			if (x > max)
				max = x;
		}
		return max;
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


	public static double sum(double[][] stats){
		double sum = 0.0;
		for(int i=0; i<stats.length; i++){
			for(int j=0; j<stats[0].length; j++){
				sum += stats[i][j];
			}
		}
		return sum;
	}

	public static double allmeans(double[][] stats){
		double means = 0.0;
		for(int i=0; i<stats.length; i++){
			for(int j=0; j<stats[0].length; j++){
				means += stats[i][j];
			}
		}
		means = means / (stats.length * stats[0].length);
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
