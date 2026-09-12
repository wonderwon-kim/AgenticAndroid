package com.agenticandroid.app.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AgentLoopTest {
    @Test
    fun scrollRequestBuildsObservationActionAndVerificationPlan() {
        val loop = AgentLoop()

        val plan = loop.buildPlan("scroll to the latest message")

        assertEquals(4, plan.size)
        assertEquals(ActionType.WAIT, plan.first().actionType)
        assertEquals(ActionType.SCROLL, plan[2].actionType)
        assertEquals("Verify result", plan.last().title)
    }

    @Test
    fun pauseResumeAndCancelUpdateCurrentTaskAndState() {
        val loop = AgentLoop()
        val task = loop.createTask("open settings")

        loop.pause()
        assertEquals(AgentGoalState.PAUSED, loop.state.value)
        assertEquals(AgentGoalState.PAUSED, task.currentState)

        loop.resume()
        assertEquals(AgentGoalState.RUNNING, loop.state.value)
        assertEquals(AgentGoalState.RUNNING, task.currentState)

        loop.cancel()
        assertEquals(AgentGoalState.CANCELLED, loop.state.value)
        assertEquals(AgentGoalState.CANCELLED, task.currentState)
        assertTrue(loop.currentTask() === task)
    }
}
