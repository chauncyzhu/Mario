/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  Neither the name of the Mario AI nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package problem.mario;

import agent.agents.EnsembleAgent;
import agent.state.StateAction;
import problem.mario.ch.idsia.benchmark.tasks.*;
import problem.mario.ch.idsia.agents.Agent;
import problem.mario.ch.idsia.benchmark.mario.engine.GlobalOptions;
import problem.mario.ch.idsia.benchmark.mario.environments.Environment;
import problem.mario.ch.idsia.benchmark.mario.environments.MarioEnvironment;
import problem.mario.ch.idsia.tools.EvaluationInfo;
import problem.mario.ch.idsia.tools.MarioAIOptions;
import problem.mario.ch.idsia.utils.statistics.StatisticalSummary;

import java.util.Vector;
import problem.Problem;
import problem.SingleAgentProblem;
import util.RNG;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey@idsia.ch Date: Mar
 * 14, 2010 Time: 4:47:33 PM
 */
public class Mario extends SingleAgentProblem {

    protected final static Environment environment = MarioEnvironment.getInstance();
    protected MarioInbetween agent;
    protected MarioAIOptions options;
    private long COMPUTATION_TIME_BOUND = 1000000; // stands for prescribed  FPS 24.
    private String name = getClass().getSimpleName();
    private EvaluationInfo evaluationInfo;
    
    protected boolean viz = false;
    
    protected double totalReward;

    protected boolean enemies;
    protected boolean frozenEnemies;
    
    private Vector<StatisticalSummary> statistics = new Vector<StatisticalSummary>();

    public Mario(){
        this(true, false);
    }
    
    public Mario(boolean enemies, boolean frozenEnemies) {
        agent = new MarioInbetween();
        this.enemies = enemies;
        this.frozenEnemies = frozenEnemies;
        this.reset();
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setVisualize(boolean viz){
        this.viz = viz;
    }
    
    @Override
    public void reset() {
        final MarioAIOptions marioAIOptions = new MarioAIOptions(new String[]{});
        marioAIOptions.setAgent(agent);
        marioAIOptions.setLevelDifficulty(0);
        marioAIOptions.setLevelRandSeed(RNG.randomInt(1000000));
        marioAIOptions.setMarioMode(RNG.randomInt(3));
        marioAIOptions.setGapsCount(false);
        marioAIOptions.setVisualization(viz);
        marioAIOptions.setFrozenCreatures(frozenEnemies);
        //marioAIOptions.setFlatLevel(!enemies);
        if(enemies){
            marioAIOptions.setEnemies("");
        } else {
            marioAIOptions.setEnemies("off");
        }
        
        options = marioAIOptions;
        environment.reset(options);
        agent.reset();
        agent.setObservationDetails(environment.getReceptiveFieldWidth(),
                environment.getReceptiveFieldHeight(),
                environment.getMarioEgoPos()[0],
                environment.getMarioEgoPos()[1]);
        agent.integrateObservation(environment);
        totalReward = -1;
    }

    public String getName() {
        return name;
    }

    public void printStatistics() {
        System.out.println(evaluationInfo.toString());
    }

    public EvaluationInfo getEvaluationInfo() {
//    System.out.println("evaluationInfo = " + evaluationInfo);
        return evaluationInfo;
    }

    @Override
    public int getNumActions() {
        return 12;
    }

    @Override
    public double[] step(int action) {
        boolean[] act = transformAction(action);
        agent.step();
        environment.performAction(act);
        environment.tick();
        agent.integrateObservation(environment);
        double reward = environment.getIntermediateReward();
        double[] r = new double[]{reward-totalReward, reward-totalReward};
        totalReward = reward;
        return r;
    }
    
    public boolean[] transformAction(int a){
        boolean[] action = new boolean[Environment.numberOfKeys];
        for (int i = 0; i < action.length; ++i) {
            action[i] = false;
        }
        switch(a){
            case 0:
                break;
            case 1:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_LEFT] = true;
                break;
            case 2:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_RIGHT] = true;
                break;
            case 3:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP] = true;
                break;
            case 4:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_LEFT] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP] = true;
                break;
            case 5:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_RIGHT] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP] = true;
                break;
            case 6:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED] = true;
                break;
            case 7:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_LEFT] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED] = true;
                break;
            case 8:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_RIGHT] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED] = true;
                break;
            case 9:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED] = true;
                break;
            case 10:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_LEFT] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED] = true;
                break;
            case 11:
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_RIGHT] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_JUMP] = true;
                action[problem.mario.ch.idsia.benchmark.mario.engine.sprites.Mario.KEY_SPEED] = true;
                break;
        }
        
        return action;
    }

    @Override
    public boolean isGoalReached(int iteration) {
        return environment.isLevelFinished();
    }

    @Override
    public StateAction getState() {
        return agent.getState();
    }
    
    public MarioInbetween getMario(){
        return agent;
    }

}