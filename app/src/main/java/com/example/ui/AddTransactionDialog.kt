package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.Transaction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(txToEdit: Transaction? = null, onDismiss: () -> Unit, onSave: (Transaction) -> Unit) {
    var details by remember(txToEdit) { mutableStateOf(txToEdit?.details ?: "") }
    var amount by remember(txToEdit) { mutableStateOf(txToEdit?.amount?.toString()?.removeSuffix(".0") ?: "") }
    var category by remember(txToEdit) { mutableStateOf(txToEdit?.category ?: "EXPENSE") } // INCOME or EXPENSE
    var type by remember(txToEdit) { mutableStateOf(txToEdit?.type ?: "MY_ACCOUNT") }
    var cashType by remember(txToEdit) { mutableStateOf(txToEdit?.cashType ?: "SOFT") } // HARD or SOFT
    var tag by remember(txToEdit) { mutableStateOf(txToEdit?.tag ?: "Other") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(if (txToEdit != null) "Edit Transaction" else "Add Transaction", style = MaterialTheme.typography.titleLarge)
            
            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Details (e.g. Groceries)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Category")
                SegmentedControl(
                    items = listOf("EXPENSE", "INCOME"),
                    selectedItem = category,
                    onItemSelection = { category = it }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Type")
                SegmentedControl(
                    items = listOf("MY_ACCOUNT", "MONTHLY"),
                    selectedItem = type,
                    onItemSelection = { type = it }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Cash Type")
                SegmentedControl(
                    items = listOf("SOFT", "HARD"),
                    selectedItem = cashType,
                    onItemSelection = { cashType = it }
                )
            }
            
            Text("Tag", style = MaterialTheme.typography.bodyMedium)
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val tags = listOf("Food", "Transport", "Rent", "Salary", "Shopping", "Entertainment", "Other")
                items(tags.size) { index ->
                    val item = tags[index]
                    val isSelected = tag == item
                    val color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    
                    Surface(
                        color = color,
                        shape = MaterialTheme.shapes.small,
                        onClick = { tag = item }
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0
                    if (details.isNotBlank() && value > 0) {
                        onSave(Transaction(
                            id = txToEdit?.id ?: 0,
                            date = txToEdit?.date ?: System.currentTimeMillis(),
                            type = type,
                            category = category,
                            amount = value,
                            cashType = cashType,
                            details = details,
                            tag = tag
                        ))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Transaction")
            }
        }
    }
}

@Composable
fun SegmentedControl(items: List<String>, selectedItem: String, onItemSelection: (String) -> Unit) {
    Row {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == item
            val color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            
            Surface(
                color = color,
                shape = MaterialTheme.shapes.small,
                onClick = { onItemSelection(item) },
                modifier = Modifier.padding(end = if (index < items.size - 1) 4.dp else 0.dp)
            ) {
                Text(
                    text = item,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
            }
        }
    }
}
