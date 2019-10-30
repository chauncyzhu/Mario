/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.mario;

import agent.agents.EGreedyPolicy;
import agent.agents.EnsembleAgent;
import agent.agents.Policy;
import agent.agents.QLambdaAgent;
import agent.state.StateAction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import problem.Problem;
import problem.mario.ch.idsia.benchmark.mario.environments.Environment;

/**
 *
 * @author timbrys
 */
public class HumanAgent extends EnsembleAgent {
    protected MarioInbetween mario;
    
    public HumanAgent(Problem prob, MarioInbetween mario) {
        super(prob, new QLambdaAgent[]{}, new EGreedyPolicy(0.0));
        this.mario = mario;
    }

    @Override
    protected double[] getPreferences(StateAction sa) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected int chooseAction(StateAction sa){
        return actionToInt();
    }
    
    protected int actionToInt(){
        boolean[] action = mario.getAction();
        if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_RIGHT]){
            if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED]){
                if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP]){
                    return 11;
                } else {
                    return 8;
                }
            } else {
                if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP]){
                    return 5;
                } else {
                    return 2;
                }
            }
        } else if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_LEFT]){
            if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED]){
                if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP]){
                    return 10;
                } else {
                    return 7;
                }
            } else {
                if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP]){
                    return 4;
                } else {
                    return 1;
                }
            }
        } else {
            if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED]){
                if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP]){
                    return 9;
                } else {
                    return 6;
                }
            } else {
                if(action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP]){
                    return 3;
                } else {
                    return 0;
                }
            }
        }
    }

}
