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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
public class AdviceRewardMario extends Mario implements KeyListener{

    protected boolean adviceGiven = false;
    
    public AdviceRewardMario(){
        this(true, false);
    }
    
    public AdviceRewardMario(boolean enemies, boolean frozenEnemies) {
        super(enemies, frozenEnemies);
        this.reset();
    }

    public void reset(){
        this.adviceGiven = false;
        super.reset();
    }
    
    @Override
    public double[] step(int action) {
        boolean[] act = transformAction(action);
        agent.step();
        environment.performAction(act);
        environment.tick();
        agent.integrateObservation(environment);
        double reward = adviceGiven ? 1.0 : 0.0;
        if(adviceGiven){
            System.out.println("advice");
        }
        adviceGiven = false;
        double[] r = new double[]{reward, reward};
        totalReward += reward;
        return r;
    }

    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        adviceGiven = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
    }

}