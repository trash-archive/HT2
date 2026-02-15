package com.example.ht2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// FIXED: Added currentTheme parameter for dynamic colors
@Composable
fun AddQuestionDialog(
    currentTheme: CategoryTheme,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val maxCharacters = 140
    var questionText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create Question",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF666666)
                        )
                    }
                }

                // Description
                Text(
                    text = "Write your own question to add to your collection. Keep it short and meaningful so it looks great on a card.",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )

                // FIXED: Text field with theme color
                OutlinedTextField(
                    value = questionText,
                    onValueChange = {
                        if (it.length <= maxCharacters) {
                            questionText = it
                            showError = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = {
                        Text(
                            text = "What question would you like to ask?",
                            color = Color(0xFFAAAAAA)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = currentTheme.accentColor,  // DYNAMIC
                        unfocusedBorderColor = Color(0xFFDDDDDD),
                        focusedTextColor = Color(0xFF2D2D2D),
                        unfocusedTextColor = Color(0xFF2D2D2D),
                        cursorColor = currentTheme.accentColor  // DYNAMIC
                    ),
                    shape = RoundedCornerShape(16.dp),
                    isError = showError,
                    supportingText = if (showError) {
                        {
                            Text(
                                text = if (questionText.isBlank())
                                    "Please enter a question"
                                else
                                    "Question must be $maxCharacters characters or less",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else null
                )

                // Character Counter
                Text(
                    text = "${questionText.length} / $maxCharacters",
                    fontSize = 12.sp,
                    color = if (questionText.length >= maxCharacters)
                        MaterialTheme.colorScheme.error
                    else
                        Color(0xFF999999),
                    modifier = Modifier.align(Alignment.End)
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF666666)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.5.dp
                        )
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // FIXED: Save button with theme color
                    Button(
                        onClick = {
                            when {
                                questionText.trim().isEmpty() -> {
                                    showError = true
                                }
                                questionText.length > maxCharacters -> {
                                    showError = true
                                }
                                else -> {
                                    onSave(questionText.trim())
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentTheme.accentColor,  // DYNAMIC
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}