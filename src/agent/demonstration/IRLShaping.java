/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.demonstration;

import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import util.MyMath;

/**
 *
 * @author timbrys
 */
public class IRLShaping extends PotentialBasedShaping {

    protected double[] weights;

    public IRLShaping(double scaling, double gamma, String weightsFile) {
        super(scaling, gamma);
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(weightsFile));
            line = br.readLine();

            // use comma as separator
            String[] strWeights = line.split(cvsSplitBy);
            weights = new double[strWeights.length];
            for(int i=0; i<strWeights.length; i++){
                weights[i] = new Double(strWeights[i]);
            }
            

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected double actualPotential(StateAction sa) {
        return MyMath.vectorMultiplication(weights, sa.getState());
    }

}
