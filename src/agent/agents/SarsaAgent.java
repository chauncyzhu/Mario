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
package agent.agents;

import agent.state.StateAction;
import agent.shapings.PotentialBasedShaping;
import agent.shapings.Shaping;

/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, firstname_at_idsia_dot_ch
 * Date: Sep 1, 2009 Time: 3:12:07 PM Package: competition.cig.sergeykarakovskiy
 */
public class SarsaAgent extends QLambdaAgent{
    
    public SarsaAgent(double alpha, double lambda, Shaping shape, double gamma) {
        super(alpha, lambda, shape, gamma);
    }
    
    public SarsaAgent(double alpha, double lambda, PotentialBasedShaping initialization, Shaping shape, double gamma) {
        super(alpha, lambda, initialization, shape, gamma);
    }
    
    @Override
    public void update(StateAction previous, double reward, StateAction next) {
        cmac.setTraces(previous);
        double delta = shape.shape(previous, next, reward) + gamma*getQ(next) - getQ(previous);
        cmac.update(alpha*delta);
        cmac.decay();
    }

}
