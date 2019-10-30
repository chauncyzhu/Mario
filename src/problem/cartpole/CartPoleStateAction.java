/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.cartpole;

import agent.state.StateAction;
import agent.state.TileCoding;
import problem.Problem;

/**
 *
 * @author timbrys
 */
public class CartPoleStateAction extends StateAction {

    public static int nrTiles = 16;
//    protected static TileCodersNoHashing projector = null;
//    protected static TabularAction toStateAction = null;
    
    public static CartPoleStateAction initState(){
        return new CartPoleStateAction(new double[4], 0);
    }
    
    public CartPoleStateAction(double[] state, int action){
        super(state, action);
//        if(projector == null){
//            init();
//        }
    }
    
    public CartPoleStateAction(double[] state, int action, long[] key){
        super(state, action, key);
//        if(projector == null){
//            init();
//        }
    }
    
    protected static void init(){
//        projector = new TileCodersNoHashing(CartPole.getObservationRanges());
//        projector.addFullTilings(20, nrTiles);
//        projector.includeActiveFeature();
//        toStateAction = new TabularAction(CartPole.Actions, projector.vectorNorm(), projector.vectorSize());
//        toStateAction.includeActiveFeature();
    }
    
    @Override
    public double[] normalize() {
        return new double[]{((state[0]) - CartPole.DEFAULTLEFTCARTBOUND)/(CartPole.DEFAULTRIGHTCARTBOUND - CartPole.DEFAULTLEFTCARTBOUND), 
            ((state[1]) - CartPole.DEFAULTLEFTCARTBOUND)/(CartPole.DEFAULTRIGHTCARTBOUND - CartPole.DEFAULTLEFTCARTBOUND), 
            ((state[2]) - CartPole.DEFAULTLEFTANGLEBOUND)/(CartPole.DEFAULTRIGHTANGLEBOUND - CartPole.DEFAULTLEFTANGLEBOUND),
            ((state[3]) - CartPole.DEFAULTLEFTANGLEBOUND)/(CartPole.DEFAULTRIGHTANGLEBOUND - CartPole.DEFAULTLEFTANGLEBOUND)};
    }

    @Override
    public StateAction unnormalize(double[] state, int action) {
        return new CartPoleStateAction(new double[]{((state[0])*(CartPole.DEFAULTRIGHTCARTBOUND - CartPole.DEFAULTLEFTCARTBOUND)+CartPole.DEFAULTLEFTCARTBOUND), 
            ((state[1])*(CartPole.DEFAULTRIGHTCARTBOUND - CartPole.DEFAULTLEFTCARTBOUND)+CartPole.DEFAULTLEFTCARTBOUND),
            ((state[2])*(CartPole.DEFAULTRIGHTANGLEBOUND - CartPole.DEFAULTLEFTANGLEBOUND)+CartPole.DEFAULTLEFTANGLEBOUND),
            ((state[3])*(CartPole.DEFAULTRIGHTANGLEBOUND - CartPole.DEFAULTLEFTANGLEBOUND)+CartPole.DEFAULTLEFTANGLEBOUND)}, action);
    }

    @Override
    public long[] calculateKey() {
        if(action < 0){
            return null;
        }
        
//        RealVector x_tp1 = projector.project(state);
//        BinaryVector phi_sa_t = (BinaryVector)toStateAction.stateAction(x_tp1, CartPole.Actions[action]);
//        
//        return phi_sa_t.getActiveIndexes().clone();
        
        return TileCoding.GetTiles(nrTiles, new double[]{state[0]/0.6, state[1]/1.5, state[2]/0.0525, state[3]/1.5}, 50000, -1, -1, -1);
    }

    @Override
    public StateAction clone() {
        if(key == null){
            return new CartPoleStateAction(state.clone(), action);
        } else {
            return new CartPoleStateAction(state.clone(), action, key.clone());
        }
    }
    
    @Override
    public String dataformat() {
        return "@RELATION maze\n"
                + "\n"
                + "   @ATTRIBUTE x  NUMERIC\n"
                + "   @ATTRIBUTE xdot  NUMERIC\n"
                + "   @ATTRIBUTE angle   NUMERIC\n"
                + "   @ATTRIBUTE angledot   NUMERIC\n"
                + "   @ATTRIBUTE reward        NUMERIC\n"
                + "   @ATTRIBUTE class        {0,1}\n"
                + "\n";
    }

    @Override
    public StateAction newStateAction(double[] state, int action) {
        return new CartPoleStateAction(state, action);
    }
}
