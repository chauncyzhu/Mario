/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author timbrys
 */
public class MyMath {
    
    public static double vectorMultiplication(double[] a, double[] b){
        double product = 0.0;
        for(int i=0; i<a.length; i++){
            product += a[i]*b[i];
        }
        return product;
    }
}
