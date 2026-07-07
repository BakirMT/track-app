package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DebtCredit
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility

data class Payment(val amount: Double, val date: Long)

fun parsePayments(jsonString: String): List<Payment> {
    val list = mutableListOf<Payment>()
    try {
        val array = JSONArray(jsonString)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(Payment(obj.getDouble("amount"), obj.getLong("date")))
        }
    } catch (e: Exception) { }
    return list
}

fun serializePayments(list: List<Payment>): String {
    val array = JSONArray()
    for (p in list) {
        val obj = JSONObject()
        obj.put("amount", p.amount)
        obj.put("date", p.date)
        array.put(obj)
    }
    return array.toString()
}

@Composable
fun DebtsScreen(debts: List<DebtCredit>, onAdd: (DebtCredit) -> Unit, onDelete: (DebtCredit) -> Unit, onUpdate: (DebtCredit) -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var debtToEdit by remember { mutableStateOf<DebtCredit?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (debts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No debts or credits.", color = Slate500)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(debts) { debt ->
                    DebtItem(
                        debt, 
                        onDelete = { onDelete(debt) }, 
                        onUpdate = { onUpdate(it) },
                        onEdit = { 
                            debtToEdit = debt
                            showAddDialog = true 
                        }
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { 
                debtToEdit = null
                showAddDialog = true 
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Add Debt") },
            containerColor = AppGreen500,
            contentColor = Color.White
        )
    }

    if (showAddDialog) {
        AddDebtDialog(
            debtToEdit = debtToEdit,
            onDismiss = { 
                showAddDialog = false
                debtToEdit = null
            },
            onSave = { 
                onAdd(it)
                showAddDialog = false
                debtToEdit = null
            }
        )
    }
}

@Composable
fun DebtItem(debt: DebtCredit, onDelete: () -> Unit, onUpdate: (DebtCredit) -> Unit, onEdit: () -> Unit) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateString = dateFormat.format(Date(debt.date))
    
    val isOwedToMe = debt.credit > debt.debit
    val totalAmount = if (isOwedToMe) debt.credit else debt.debit
    
    val payments = remember(debt.paymentsJson) { parsePayments(debt.paymentsJson) }
    val paidAmount = payments.sumOf { it.amount }
    val remaining = (totalAmount - paidAmount).coerceAtLeast(0.0)
    val isCompleted = remaining <= 0.0
    
    val baseColor = if (isCompleted) Emerald500 else Rose500
    val icon = if (isCompleted) Icons.Default.Check else Icons.Default.SwapHoriz

    var expanded by remember { mutableStateOf(false) }
    var paymentInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCompleted) Emerald900.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, if (isCompleted) Emerald500.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .clickable { expanded = !expanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.outlineVariant),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)).background(baseColor.copy(alpha = 0.2f)).border(1.dp, baseColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = baseColor, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = debt.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$dateString • ${if (isCompleted) "Settled" else "Pending"}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = format.format(remaining),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) Emerald400 else Rose400
                )
                Text(
                    text = if (isCompleted) "Done" else if (isOwedToMe) "Credit" else "Debit",
                    fontSize = 10.sp,
                    color = Slate500,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppGreen500, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Rose500, modifier = Modifier.size(16.dp))
                }
            }
        }
        
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Divider(color = Slate800, modifier = Modifier.padding(bottom = 16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total: ${format.format(totalAmount)}", color = Slate400, fontSize = 12.sp)
                    Text("Paid: ${format.format(paidAmount)}", color = Slate400, fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (payments.isNotEmpty()) {
                    Text("History", color = Slate300, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    payments.forEach { p ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(dateFormat.format(Date(p.date)), color = Slate500, fontSize = 12.sp)
                            Text(format.format(p.amount), color = Slate300, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                if (!isCompleted) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = paymentInput,
                            onValueChange = { paymentInput = it },
                            placeholder = { Text("Amount...", color = Slate500) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Slate100, fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AppGreen500,
                                unfocusedBorderColor = Slate700,
                            ),
                            modifier = Modifier.weight(1f).height(50.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val added = paymentInput.toDoubleOrNull() ?: 0.0
                                if (added > 0) {
                                    val newPayments = payments.toMutableList()
                                    newPayments.add(Payment(added, System.currentTimeMillis()))
                                    onUpdate(debt.copy(paymentsJson = serializePayments(newPayments)))
                                    paymentInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppGreen500),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddDebtDialog(debtToEdit: DebtCredit? = null, onDismiss: () -> Unit, onSave: (DebtCredit) -> Unit) {
    var description by remember(debtToEdit) { mutableStateOf(debtToEdit?.description ?: "") }
    
    val initialAmount = if (debtToEdit != null) {
        if (debtToEdit.credit > debtToEdit.debit) debtToEdit.credit else debtToEdit.debit
    } else 0.0
    var amount by remember(debtToEdit) { mutableStateOf(if (initialAmount > 0) initialAmount.toString().removeSuffix(".0") else "") }
    
    var isOwedToMe by remember(debtToEdit) { mutableStateOf(if (debtToEdit != null) debtToEdit.credit > debtToEdit.debit else true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (debtToEdit != null) "Edit Debt/Credit" else "Add Debt/Credit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Person or Event") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = isOwedToMe, onClick = { isOwedToMe = true })
                    Text("Owes me (Credit)")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = !isOwedToMe, onClick = { isOwedToMe = false })
                    Text("I owe (Debit)")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0
                    if (description.isNotBlank() && value > 0) {
                        onSave(DebtCredit(
                            id = debtToEdit?.id ?: 0,
                            date = debtToEdit?.date ?: System.currentTimeMillis(),
                            description = description,
                            debit = if (isOwedToMe) 0.0 else value,
                            credit = if (isOwedToMe) value else 0.0,
                            paymentsJson = debtToEdit?.paymentsJson ?: "[]"
                        ))
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
