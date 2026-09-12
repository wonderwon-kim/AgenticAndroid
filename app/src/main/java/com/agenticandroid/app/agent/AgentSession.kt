package com.agenticandroid.app.agent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AgentSession {
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private var currentTask: AgentTask? = null

    fun setTask(task: AgentTask) {
        currentTask = task
    }

    fun appendMessage(message: String) {
        val updated = _messages.value.toMutableList().apply { add(message) }
        _messages.value = updated
    }

    fun getCurrentTask(): AgentTask? = currentTask
}
