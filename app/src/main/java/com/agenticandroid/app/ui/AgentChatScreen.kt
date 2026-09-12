package com.agenticandroid.app.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private data class ChatMessage(
    val sender: String,
    val text: String
)

@Composable
fun AgentChatScreen() {
    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                sender = "AI",
                text = "Ready. Tell me what to do on your screen."
            )
        )
    }
    var inputText by remember { mutableStateOf("") }
    var agentStatus by remember { mutableStateOf("Standby") }

    fun sendMessage() {
        val trimmed = inputText.trim()
        if (trimmed.isEmpty()) return
        messages.add(ChatMessage("User", trimmed))
        agentStatus = "Planning"
        messages.add(ChatMessage("AI", "Task accepted: $trimmed\nObserving screen and selecting next action."))
        inputText = ""
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF7F2EA)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1D1A27))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "AgenticAndroid",
                        color = Color(0xFFFFE7B7),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = agentStatus,
                        color = Color(0xFFF3E6C8),
                        style = MaterialTheme.typography.bodyMedium
                    )
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
                                    color = if (isUser) Color(0xFF2E7D32) else Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(18.dp)
                                )
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = message.sender,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isUser) Color.White else Color(0xFF8A5E00)
                                )
                                Text(
                                    text = message.text,
                                    color = if (isUser) Color.White else Color(0xFF1F1F1F)
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
                    placeholder = { Text("Ask the agent...") },
                    shape = RoundedCornerShape(16.dp)
                )
                Button(
                    onClick = { sendMessage() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Send")
                }
            }
        }
    }
}
