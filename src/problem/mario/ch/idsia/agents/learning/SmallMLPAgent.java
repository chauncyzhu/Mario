/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
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

package problem.mario.ch.idsia.agents.learning;

import problem.mario.ch.idsia.agents.Agent;
import problem.mario.ch.idsia.agents.controllers.BasicMarioAIAgent;
import problem.mario.ch.idsia.benchmark.mario.environments.Environment;
import problem.mario.ch.idsia.evolution.Evolvable;
import problem.mario.ch.idsia.evolution.MLP;

/**
 * Created by IntelliJ IDEA.
 * User: julian
 * Date: Apr 28, 2009
 * Time: 2:09:42 PM
 */
public class SmallMLPAgent extends BasicMarioAIAgent implements Agent, Evolvable
{

private MLP mlp;
final int numberOfOutputs = Environment.numberOfKeys;
final int numberOfInputs = 21;
static private final String name = "SmallMLPAgent";

public SmallMLPAgent()
{
    super(name);
    mlp = new MLP(numberOfInputs, 10, numberOfOutputs);
}

private SmallMLPAgent(MLP mlp)
{
    super(name);
    this.mlp = mlp;
}

public Evolvable getNewInstance()
{
    return new SmallMLPAgent(mlp.getNewInstance());
}

public Evolvable copy()
{
    return new SmallMLPAgent(mlp.copy());
}

public void reset()
{
    mlp.reset();
}

public void mutate()
{
    mlp.mutate();
}

public boolean[] getAction()
{
//        byte[][] scene = observation.getLevelSceneObservation(/*1*/);
//        byte[][] enemies = observation.getEnemiesObservation(/*0*/);
    byte[][] scene = levelScene;
    double[] inputs = new double[]{probe(-1, -1, scene), probe(0, -1, scene), probe(1, -1, scene),
            probe(-1, 0, scene), probe(0, 0, scene), probe(1, 0, scene),
            probe(-1, 1, scene), probe(0, 1, scene), probe(1, 1, scene),
            probe(-1, -1, enemies), probe(0, -1, enemies), probe(1, -1, enemies),
            probe(-1, 0, enemies), probe(0, 0, enemies), probe(1, 0, enemies),
            probe(-1, 1, enemies), probe(0, 1, enemies), probe(1, 1, enemies),
            isMarioOnGround ? 1 : 0, isMarioAbleToJump ? 1 : 0,
            1};
    double[] outputs = mlp.propagate(inputs);
    boolean[] action = new boolean[numberOfOutputs];
    for (int i = 0; i < action.length; i++)
    {
        action[i] = outputs[i] > 0;
    }
    return action;
}

public String getName()
{
    return name;
}

public void setName(String name)
{
}

private double probe(int x, int y, byte[][] scene)
{
    int realX = x + 11;
    int realY = y + 11;
    return (scene[realX][realY] != 0) ? 1 : 0;
}
}
