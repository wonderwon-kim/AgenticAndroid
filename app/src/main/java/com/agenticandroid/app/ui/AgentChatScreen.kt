package com.agenticandroid.app.ui

import com.agenticandroid.app.BuildConfig
import com.agenticandroid.app.agent.AgentGoalState
import com.agenticandroid.app.agent.AgentLoop
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private data class ChatMessage(
    val sender: String,
    val text: String
)

@Composable
fun AgentChatScreen() {
    val agentLoop = remember { AgentLoop() }
    val coroutineScope = rememberCoroutineScope()
    val loopState by agentLoop.state.collectAsState()
    val nightlyCase = BuildConfig.NIGHTLY_CASE
    val caseAccent = when (nightlyCase) {
        "PLANNER" -> Color(0xFFFFC857)
        "ACCESSIBILITY" -> Color(0xFFB7F0AD)
        "RECOVERY" -> Color(0xFFFF8A80)
        "TURBO" -> Color(0xFFD7A7FF)
        "EXPERIMENTAL" -> Color(0xFFFF6B6B)
        "ORBIT" -> Color(0xFF65D6CE)
        "ATLAS" -> Color(0xFFFFA94D)
        "SENTINEL" -> Color(0xFF9CA3FF)
        "LUCID" -> Color(0xFFE7F5FF)
        "FORGE" -> Color(0xFFFF7A45)
        "SYNTHESIS" -> Color(0xFF74C0FC)
        else -> Color(0xFF7DE7FF)
    }
    val casePrompt = when (nightlyCase) {
        "PLANNER" -> "Plan the next steps..."
        "ACCESSIBILITY" -> "Describe the screen task..."
        "RECOVERY" -> "Resume a failed task..."
        "TURBO" -> "Run a fast command..."
        "EXPERIMENTAL" -> "Try a new agent strategy..."
        "ORBIT" -> "Coordinate a multi-step mission..."
        "ATLAS" -> "Map the screen and target..."
        "SENTINEL" -> "Check risks before acting..."
        "LUCID" -> "Explain the clearest next step..."
        "FORGE" -> "Build a reliable action chain..."
        "SYNTHESIS" -> "Combine the best execution strategy..."
        else -> "Type a focused command..."
    }
    val caseCapability = when (nightlyCase) {
        "EXPERIMENTAL" -> "Adaptive strategy lab"
        "ORBIT" -> "Multi-step coordination"
        "ATLAS" -> "Screen mapping"
        "SENTINEL" -> "Risk-aware execution"
        "LUCID" -> "Explainable planning"
        "FORGE" -> "Reliable action chains"
        "SYNTHESIS" -> "Unified agent core"
        else -> "Observe // decide // verify"
    }
    val messages = remember {
        mutableStateListOf(
            ChatMessage("AI", "$nightlyCase mode online. Ready for autonomous task execution."),
            ChatMessage("AI", "Session synced with your Android surface.")
        )
    }
    var inputText by remember { mutableStateOf("") }
    var agentStatus by remember { mutableStateOf("Ready") }
    var executionJob by remember { mutableStateOf<Job?>(null) }

    fun sendMessage() {
        val trimmed = inputText.trim()
        if (trimmed.isEmpty()) return
        if (executionJob?.isActive == true) return
        val task = agentLoop.createTask(trimmed)
        val plan = agentLoop.buildPlan(trimmed)
        messages.add(ChatMessage("User", trimmed))
        messages.add(ChatMessage("AI", "Plan ready: ${plan.joinToString(" -> ") { it.title }}"))
        agentStatus = "Executing"
        inputText = ""
        executionJob = coroutineScope.launch {
            agentLoop.executePlan(task, plan) { step ->
                agentStatus = step.title
            }
            if (agentLoop.state.value == AgentGoalState.COMPLETED) {
                agentStatus = "Completed"
                messages.add(ChatMessage("AI", "Verified ${task.actionHistory.size} actions for: $trimmed"))
            }
            executionJob = null
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF060B16)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF08101F),
                            Color(0xFF0C1830),
                            Color(0xFF101A34)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF11213E).copy(alpha = 0.82f))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "A I // $nightlyCase",
                            color = caseAccent,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$agentStatus // ${loopState.name}",
                            color = caseAccent.copy(alpha = 0.78f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = caseCapability,
                            color = Color(0xFFEAFBFF).copy(alpha = 0.72f),
                            style = MaterialTheme.typography.labelMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { agentLoop.pause() }, enabled = loopState != AgentGoalState.PAUSED) {
                                Text("Pause")
                            }
                            Button(onClick = { agentLoop.resume() }, enabled = loopState == AgentGoalState.PAUSED) {
                                Text("Resume")
                            }
                            Button(onClick = {
                                agentLoop.cancel()
                                executionJob?.cancel()
                                executionJob = null
                                agentStatus = "Cancelled"
                            }) {
                                Text("Stop")
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { message ->
                        val isUser = message.sender == "User"
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                modifier = Modifier
                                    .fillMaxWidth(0.82f)
                                    .background(
                                        color = if (isUser) Color(0xFF1FB6FF) else Color(0xFF15233F),
                                        shape = RoundedCornerShape(18.dp)
                                    )
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = message.sender,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isUser) Color.Black else caseAccent
                                    )
                                    Text(
                                        text = message.text,
                                        color = if (isUser) Color.Black else Color(0xFFEAFBFF)
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(casePrompt, color = Color(0xFF9FC3D7)) },
                        shape = RoundedCornerShape(18.dp)
                    )
                    Button(
                        onClick = { sendMessage() },
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Run")
                    }
                }
            }
        }
    }
}
