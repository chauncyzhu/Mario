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

package problem.mario.ch.idsia.scenarios.oldscenarios;

import problem.mario.ch.idsia.agents.Agent;
import problem.mario.ch.idsia.agents.AgentsPool;
import problem.mario.ch.idsia.agents.learning.SimpleMLPAgent;
import problem.mario.ch.idsia.benchmark.mario.engine.GlobalOptions;
import problem.mario.ch.idsia.benchmark.tasks.MultiSeedProgressTask;
import problem.mario.ch.idsia.evolution.Evolvable;
import problem.mario.ch.idsia.evolution.ea.ES;
import problem.mario.ch.idsia.tools.MarioAIOptions;
import problem.mario.ch.idsia.utils.wox.serial.Easy;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: May 4, 2009
 * Time: 4:33:25 PM
 */
public class EvolveIncrementally
{

final static int generations = 100;
final static int populationSize = 100;


public static void main(String[] args)
{
    MarioAIOptions options = new MarioAIOptions(new String[0]);
//        options.setEvaluationQuota(1);
    Evolvable initial = new SimpleMLPAgent();
    if (args.length > 0)
    {
        initial = (Evolvable) AgentsPool.loadAgent(args[0], options.isPunj());
    }
//        AgentsPool.registerAgent ((Agent) initial);
    // maybe need
    AgentsPool.addAgent((Agent) initial);
    for (int difficulty = 0; difficulty < 11; difficulty++)
    {
        System.out.println("New EvolveIncrementally phase with difficulty = " + difficulty + " started.");
        options.setLevelDifficulty(difficulty);
        options.setFPS(GlobalOptions.MaxFPS);
        options.setVisualization(false);
        //Task task = new ProgressTask(options);
        MultiSeedProgressTask task = new MultiSeedProgressTask(options);
        task.setNumberOfSeeds(3);
        task.setStartingSeed(0);
        ES es = new ES(task, initial, populationSize);
        System.out.println("Evolving " + initial + " with task " + task);
        for (int gen = 0; gen < generations; gen++)
        {
            es.nextGeneration();
            double bestResult = es.getBestFitnesses()[0];
            System.out.println("Generation " + gen + " best " + bestResult);
            options.setVisualization(gen % 5 == 0 || bestResult > 4000);
//                options.setFPS(true);
            Agent a = (Agent) es.getBests()[0];
            a.setName(((Agent) initial).getName() + gen);
//                AgentsPool.addAgent(a);
//                AgentsPool.setCurrentAgent(a);
            int result = task.evaluate(a);
            options.setVisualization(false);
//                options.setFPS(true);
            Easy.save(es.getBests()[0], "evolved.xml");
            if (result > 4000)
            {
                initial = es.getBests()[0];
                break; // Go to next difficulty.
            }
        }
    }
    System.exit(0);
}
}
