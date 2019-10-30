/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.demonstration;

import agent.state.StateAction;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author timbrys
 */
public class Demonstration {
    
    //normalized reward features 
    protected List<DemonstratedSample> trajectory;
    protected List<StateAction> originalTrajectory;
    
    //normalized original state-space
    protected List<List<DemonstratedSample>> demonstratedSamples;
    
    protected int size;
    protected int cap;
    
    protected StateAction prototype;

    public Demonstration(StateAction prototype) {
        this.prototype = prototype;
    }
    
    public Demonstration(String file, StateAction prototype){
        clear();
        this.prototype = prototype;
        addFile(file);
    }
    
    public Demonstration(String[] files, StateAction prototype){
        clear();
        this.prototype = prototype;
        for(int i=0; i<files.length; i++){
            addFile(files[i]);
        }
    }
    
    protected void addFile(String file){
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(file);
            Instances data = source.getDataSet();
            for(int i=0; i<data.numInstances(); i++){
                Instance datum = data.instance(i);
                readline(datum.toDoubleArray());
            }
        } catch (Exception ex) {
            System.out.println(file);
            System.out.println(ex.getMessage());
            Logger.getLogger(Demonstration.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }
    
    public Demonstration(int cap){
        this.cap = cap;
        clear();
    }
    
    public void clear(){
        size = 0;
        demonstratedSamples = new ArrayList<List<DemonstratedSample>>();
        for(int i=0; i<13; i++){
            demonstratedSamples.add(new LinkedList<DemonstratedSample>());
        }
        trajectory = new ArrayList<DemonstratedSample>();
        originalTrajectory = new ArrayList<StateAction>();
    }
    
    public void readline(double[] state_action_reward){
        double[] state = new double[state_action_reward.length-2];
        double reward = state_action_reward[state_action_reward.length-2];
        int action = (int)state_action_reward[state_action_reward.length-1];
        System.arraycopy(state_action_reward, 0, state, 0, state.length);
        
        DemonstratedSample sample = new DemonstratedSample(state, action, reward);
        trajectory.add(sample);
        originalTrajectory.add(prototype.unnormalize(state, action));
        if(!contains(demonstratedSamples.get(action), state)){
            demonstratedSamples.get(action).add(sample);
        } else {
            find(demonstratedSamples.get(action), state).increaseCounter();
        }
    }
    
    protected boolean contains(List<DemonstratedSample> states, double[] state){
        for(Iterator<DemonstratedSample> it = states.iterator(); it.hasNext();){
            if(Arrays.equals(it.next().getState(), state)){
                return true;
            }
        }
        return false;
    }
    
    protected DemonstratedSample find(List<DemonstratedSample> states, double[] state){
        DemonstratedSample sample;
        for(Iterator<DemonstratedSample> it = states.iterator(); it.hasNext();){
            sample = it.next();
            if(Arrays.equals(sample.getState(), state)){
                return sample;
            }
        }
        return null;
    }
    
    public int size(){
        return trajectory.size();
    }
    
    public void record(StateAction state, double reward){
        if(size < cap || cap < 0){
            size++;
            DemonstratedSample sample = new DemonstratedSample(state.normalize(), state.getAction(), reward);
            trajectory.add(sample);
            originalTrajectory.add(state.clone());
            demonstratedSamples.get(state.getAction()).add(sample);
        }
    }
    
    public double getDistanceClosest(StateAction sa){
        double[] normalizedsa = sa.normalize();
        double distance, lowestDistance = Double.MAX_VALUE;
        List<DemonstratedSample> states = demonstratedSamples.get(sa.getAction());
        for(Iterator<DemonstratedSample> it = states.iterator(); it.hasNext();){
            distance = distance(normalizedsa, it.next().getState());
            if(distance < lowestDistance){
                lowestDistance = distance;
            }
        }
        return Math.sqrt(lowestDistance);
    }
    
    public DemonstratedSample getClosest(StateAction sa){
        double[] normalizedsa = sa.normalize();
        double distance, lowestDistance = Double.MAX_VALUE;
        DemonstratedSample closest = null, tmp;
        List<DemonstratedSample> states = demonstratedSamples.get(sa.getAction());
        for(Iterator<DemonstratedSample> it = states.iterator(); it.hasNext();){
            tmp = it.next();
            distance = distance(normalizedsa, tmp.getState());
            if(distance < lowestDistance){
                lowestDistance = distance;
                closest = tmp;
            }
        }
        return closest;
    }
    
    protected double distance(double[] state1, double[] state2){
        double distance = 0.0;
        for(int i=0; i<state1.length; i++){
            distance += Math.pow(state1[i]-state2[i], 2.0); 
        }
        return distance;
    }
    
    public void toFile(String file, StateAction sa){
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(file), "utf-8"));
            toString(sa, writer);
        } catch (IOException ex) {
           System.out.println("Couldn't write demonstration to file: " + ex.getMessage());
        } finally {
           try {writer.close();} catch (Exception ex) {}
        }
    }
    
    public void toString(StateAction sa, Writer writer) throws IOException{        
        writer.write(sa.dataformat());
        writer.write("\n@DATA\n");
        for (DemonstratedSample state : trajectory) {
            for(int j=0; j<state.getState().length; j++){
                writer.write(state.getState()[j] + ",");
            }
            writer.write(state.getReward() + ",");
            writer.write(state.getAction() + "\r\n");
        }
    }
    
    public String toString(){
        String s = "[";
        for (DemonstratedSample state : trajectory) {
            s += Arrays.toString(state.getState()) + ",";
        }
        return s + "]";
    }
    
    public StateAction getSample(int i){
        return originalTrajectory.get(i);
    }
    
    public double getReward(int i){
        return trajectory.get(i).getReward();
    }
    
    public StateAction getPrototype(){
        return prototype.clone();
    }
}
