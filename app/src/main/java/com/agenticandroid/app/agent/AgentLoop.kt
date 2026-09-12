package com.agenticandroid.app.agent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AgentGoalState {
    RUNNING,
    WAITING,
    OBSERVING,
    THINKING,
    ACTING,
    VERIFYING,
    PAUSED,
    COMPLETED,
    FAILED,
    CANCELLED
}

enum class ActionType {
    CLICK,
    LONG_CLICK,
    SWIPE,
    SCROLL,
    TYPE_TEXT,
    BACK,
    HOME,
    RECENTS,
    OPEN_APP,
    WAIT,
    STOP,
    ASK_USER,
    FINISH
}

data class Action(
    val type: ActionType,
    val x: Float? = null,
    val y: Float? = null,
    val direction: String? = null,
    val text: String? = null,
    val packageName: String? = null,
    val durationMs: Long? = null,
    val description: String = ""
)

data class AgentTask(
    val userRequest: String,
    val goal: String,
    var currentState: AgentGoalState = AgentGoalState.RUNNING,
    val actionHistory: MutableList<Action> = mutableListOf(),
    val errors: MutableList<String> = mutableListOf(),
    val observations: MutableList<String> = mutableListOf(),
    val startTimeMs: Long = System.currentTimeMillis()
)

class AgentLoop {
    private val _state = MutableStateFlow(AgentGoalState.RUNNING)
    val state: StateFlow<AgentGoalState> = _state

    private val taskHistory = mutableListOf<AgentTask>()

    fun createTask(userRequest: String): AgentTask {
        val task = AgentTask(
            userRequest = userRequest,
            goal = userRequest
        )
        taskHistory.add(task)
        _state.value = AgentGoalState.RUNNING
        return task
    }

    fun updateState(newState: AgentGoalState) {
        _state.value = newState
    }

    fun recordAction(task: AgentTask, action: Action) {
        task.actionHistory.add(action)
    }

    fun recordObservation(task: AgentTask, observation: String) {
        task.observations.add(observation)
    }

    fun recordError(task: AgentTask, error: String) {
        task.errors.add(error)
    }

    fun finish(task: AgentTask) {
        task.currentState = AgentGoalState.COMPLETED
        _state.value = AgentGoalState.COMPLETED
    }

    fun fail(task: AgentTask, reason: String) {
        task.currentState = AgentGoalState.FAILED
        task.errors.add(reason)
        _state.value = AgentGoalState.FAILED
    }
}
