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

data class AgentPlanStep(
    val title: String,
    val actionType: ActionType,
    val description: String
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

    fun currentTask(): AgentTask? = taskHistory.lastOrNull()

    fun buildPlan(request: String): List<AgentPlanStep> {
        val normalizedRequest = request.lowercase()
        val interaction = when {
            "scroll" in normalizedRequest -> AgentPlanStep("Scroll to target", ActionType.SCROLL, "Move through the current surface")
            "back" in normalizedRequest -> AgentPlanStep("Navigate back", ActionType.BACK, "Return to the previous surface")
            "open" in normalizedRequest -> AgentPlanStep("Open target", ActionType.OPEN_APP, "Launch the requested app or surface")
            else -> AgentPlanStep("Interact with target", ActionType.CLICK, "Locate and activate the best matching control")
        }
        return listOf(
            AgentPlanStep("Observe screen", ActionType.WAIT, "Capture the current UI state"),
            AgentPlanStep("Choose action", ActionType.ASK_USER, "Check goal and select a safe next step"),
            interaction,
            AgentPlanStep("Verify result", ActionType.WAIT, "Compare the next screen with the goal")
        )
    }

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

    fun pause() {
        if (_state.value == AgentGoalState.RUNNING || _state.value == AgentGoalState.ACTING || _state.value == AgentGoalState.THINKING) {
            _state.value = AgentGoalState.PAUSED
            currentTask()?.currentState = AgentGoalState.PAUSED
        }
    }

    fun resume() {
        if (_state.value == AgentGoalState.PAUSED) {
            _state.value = AgentGoalState.RUNNING
            currentTask()?.currentState = AgentGoalState.RUNNING
        }
    }

    fun cancel() {
        currentTask()?.currentState = AgentGoalState.CANCELLED
        _state.value = AgentGoalState.CANCELLED
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
