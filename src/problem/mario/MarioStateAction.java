/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.mario;

import agent.state.StateAction;

/**
 *
 * @author timbrys
 */
public class MarioStateAction extends StateAction{
    
    public static MarioStateAction initState(){
        return new MarioStateAction(new double[27], 0);
    }
    
    public MarioStateAction(double[] state, int action){
        super(state, action);
    }
    
    protected MarioStateAction(double[] state, int action, long[] key){
        super(state, action, key);
    }
    
    @Override
    public MarioStateAction unnormalize(double[] state, int action){
        double[] newState = state.clone();
        newState[3] = newState[3]*2.0;
        newState[4] = newState[4]*2.0;
        newState[25] = newState[25]*21.0;
        newState[26] = newState[26]*21.0;
        return new MarioStateAction(newState, action);
    }

    @Override
    public double[] normalize() {
        double[] newState = state.clone();
        newState[3] = newState[3]/2.0;
        newState[4] = newState[4]/2.0;
        newState[25] = newState[25]/21.0;
        newState[26] = newState[26]/21.0;
        return newState;
    }
    
    @Override
    public long[] calculateKey() {
//        long hash = action;
//        hash += 12 * state[0];
//        hash += 12*2 * state[1];
//        hash += 12*2*2 * state[2];
//        hash += 12*2*2*2 * state[3];
//        hash += 12*2*2*2*9 * state[4];
//        hash += 12*2*2*2*9*256 * state[5];
//        hash += 12*2*2*2*9*256*256 * state[7];
//        hash += 12*2*2*2*9*256*256*16 * state[8];
//        hash += 12*2*2*2*9*256*256*16*22 * state[9];
////        hash += 12*2*2*2*9*256*256*16*22*22 * state[10];
//        return new long[]{hash};

        long hash = 0;
        for(int i=0; i<3; i++){
            hash += Math.pow(2, i)*state[i];
        }
        hash += 8*state[3];
        hash += 8*3*state[4];
        for(int i=5; i<25; i++){
            hash += 8*9*Math.pow(2, i-5)*state[i];
        }
        hash += 9*8388608*state[25];
        hash += 9*8388608*22*state[26];
        return new long[]{hash};
    }
    
    @Override
    public MarioStateAction clone(){
        if(key == null){
            return new MarioStateAction(state.clone(), action);
        } else {
            return new MarioStateAction(state.clone(), action, key.clone());
        }
    }

    @Override
    public String dataformat() {
        return "@RELATION mario\n"
                + "\n"
                + "   @ATTRIBUTE jump  {0.0, 1.0}\n"
                + "   @ATTRIBUTE ground   {0.0, 1.0}\n"
                + "   @ATTRIBUTE shoot   {0.0, 1.0}\n"
                + "   @ATTRIBUTE x_dir   NUMERIC\n"
                + "   @ATTRIBUTE y_dir   NUMERIC\n"
                + "   @ATTRIBUTE close_enemies_1   {0.0, 1.0}\n"
                + "   @ATTRIBUTE close_enemies_2   {0.0, 1.0}\n"
                + "   @ATTRIBUTE close_enemies_3   {0.0, 1.0}\n"
                + "   @ATTRIBUTE close_enemies_4   {0.0, 1.0}\n"
                + "   @ATTRIBUTE close_enemies_5   {0.0, 1.0}\n"
                + "   @ATTRIBUTE close_enemies_6   {0.0, 1.0}\n"
                + "   @ATTRIBUTE close_enemies_7   {0.0, 1.0}\n"
                + "   @ATTRIBUTE close_enemies_8   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_1   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_2   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_3   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_4   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_5   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_6   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_7   {0.0, 1.0}\n"
                + "   @ATTRIBUTE mid_enemies_8   {0.0, 1.0}\n"
                + "   @ATTRIBUTE obstacles_1   {0.0, 1.0}\n"
                + "   @ATTRIBUTE obstacles_2   {0.0, 1.0}\n"
                + "   @ATTRIBUTE obstacles_3   {0.0, 1.0}\n"
                + "   @ATTRIBUTE obstacles_4   {0.0, 1.0}\n"
                + "   @ATTRIBUTE closest_enemy_x   NUMERIC\n"
                + "   @ATTRIBUTE closest_enemy_y   NUMERIC\n"
                + "   @ATTRIBUTE reward   NUMERIC\n"
                + "   @ATTRIBUTE class        {0,1,2,3,4,5,6,7,8,9,10,11}\n";
    }
    
    @Override
    public StateAction newStateAction(double[] state, int action) {
        return new MarioStateAction(state.clone(), action);
    }
}
