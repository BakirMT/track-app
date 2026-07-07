package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.BAInvestment
import com.example.data.Kuri
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentsScreen(
    baInvestments: List<BAInvestment>,
    onAddBAInvestment: (BAInvestment) -> Unit,
    onDeleteBAInvestment: (BAInvestment) -> Unit,
    onUpdateBAInvestment: (BAInvestment) -> Unit,
    kuriList: List<Kuri>,
    onAddKuri: (Kuri) -> Unit,
    onDeleteKuri: (Kuri) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Investment", "Kuri")

    var showAddBADialog by remember { mutableStateOf(false) }
    var showAddKuriDialog by remember { mutableStateOf(false) }
    var baToEdit by remember { mutableStateOf<BAInvestment?>(null) }
    var kuriToEdit by remember { mutableStateOf<Kuri?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Slate950,
            contentColor = Slate100,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .height(3.dp)
                        .background(AppGreen500)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) },
                    selectedContentColor = AppGreen500,
                    unselectedContentColor = Slate400
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> BAInvestmentList(
                    investments = baInvestments,
                    onDelete = onDeleteBAInvestment,
                    onUpdate = onUpdateBAInvestment,
                    onEdit = {
                        baToEdit = it
                        showAddBADialog = true
                    }
                )
                1 -> KuriList(
                    kuriList = kuriList,
                    onDelete = onDeleteKuri,
                    onEdit = {
                        kuriToEdit = it
                        showAddKuriDialog = true
                    }
                )
            }

            FloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 0) {
                        baToEdit = null
                        showAddBADialog = true 
                    } else {
                        kuriToEdit = null
                        showAddKuriDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = AppGreen500,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }

    if (showAddBADialog) {
        var name by remember(baToEdit) { mutableStateOf(baToEdit?.name ?: "") }
        var amount by remember(baToEdit) { mutableStateOf(if (baToEdit != null && baToEdit!!.totalAmount > 0) baToEdit!!.totalAmount.toString().removeSuffix(".0") else "") }

        AlertDialog(
            onDismissRequest = { 
                showAddBADialog = false 
                baToEdit = null
            },
            title = { Text(if (baToEdit != null) "Edit Investment" else "Add Investment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") }
                    )
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Total Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val valAmount = amount.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && valAmount > 0) {
                        onAddBAInvestment(BAInvestment(
                            id = baToEdit?.id ?: 0,
                            date = baToEdit?.date ?: System.currentTimeMillis(),
                            name = name, 
                            totalAmount = valAmount,
                            paymentsJson = baToEdit?.paymentsJson ?: "[]"
                        ))
                        showAddBADialog = false
                        baToEdit = null
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddBADialog = false 
                    baToEdit = null
                }) { Text("Cancel") }
            }
        )
    }

    if (showAddKuriDialog) {
        var description by remember(kuriToEdit) { mutableStateOf(kuriToEdit?.description ?: "") }
        var debit by remember(kuriToEdit) { mutableStateOf(if (kuriToEdit != null && kuriToEdit!!.debit > 0) kuriToEdit!!.debit.toString().removeSuffix(".0") else "") }

        AlertDialog(
            onDismissRequest = { 
                showAddKuriDialog = false 
                kuriToEdit = null
            },
            title = { Text(if (kuriToEdit != null) "Edit Kuri" else "Add Kuri") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") }
                    )
                    OutlinedTextField(
                        value = debit,
                        onValueChange = { debit = it },
                        label = { Text("Debit Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val valDebit = debit.toDoubleOrNull() ?: 0.0
                    if (description.isNotBlank() && valDebit > 0) {
                        onAddKuri(Kuri(
                            id = kuriToEdit?.id ?: 0,
                            date = kuriToEdit?.date ?: System.currentTimeMillis(),
                            description = description, 
                            debit = valDebit
                        ))
                        showAddKuriDialog = false
                        kuriToEdit = null
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddKuriDialog = false 
                    kuriToEdit = null
                }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun BAInvestmentList(
    investments: List<BAInvestment>,
    onDelete: (BAInvestment) -> Unit,
    onUpdate: (BAInvestment) -> Unit,
    onEdit: (BAInvestment) -> Unit
) {
    if (investments.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Investments.", color = Slate500)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(investments) { inv ->
                BAInvestmentItem(inv, onDelete, onUpdate, onEdit = { onEdit(inv) })
            }
        }
    }
}

@Composable
fun BAInvestmentItem(
    investment: BAInvestment,
    onDelete: (BAInvestment) -> Unit,
    onUpdate: (BAInvestment) -> Unit,
    onEdit: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateString = dateFormat.format(Date(investment.date))
    
    val payments = remember(investment.paymentsJson) { parsePayments(investment.paymentsJson) }
    val paidAmount = payments.sumOf { it.amount }
    val remaining = (investment.totalAmount - paidAmount).coerceAtLeast(0.0)
    val isCompleted = remaining <= 0.0
    
    val baseColor = if (isCompleted) Emerald500 else Rose500
    val icon = if (isCompleted) Icons.Default.Check else Icons.AutoMirrored.Filled.TrendingUp

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
                    text = investment.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$dateString • ${if (isCompleted) "Completed" else "Paying"}",
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
                    text = if (isCompleted) "Done" else "Remaining",
                    fontSize = 10.sp,
                    color = Slate500,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppGreen500, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { onDelete(investment) }, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
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
                    Text("Total: ${format.format(investment.totalAmount)}", color = Slate400, fontSize = 12.sp)
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
                                    onUpdate(investment.copy(paymentsJson = serializePayments(newPayments)))
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
fun KuriList(
    kuriList: List<Kuri>,
    onDelete: (Kuri) -> Unit,
    onEdit: (Kuri) -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val totalDebit = kuriList.sumOf { it.debit }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Total Debit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                Text(format.format(totalDebit), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Rose400)
            }
        }

        if (kuriList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No Kuri records.", color = Slate500)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(kuriList) { kuri ->
                    KuriItem(kuri, onDelete, onEdit = { onEdit(kuri) })
                }
            }
        }
    }
}

@Composable
fun KuriItem(
    kuri: Kuri,
    onDelete: (Kuri) -> Unit,
    onEdit: () -> Unit
) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val dateString = dateFormat.format(Date(kuri.date))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.outlineVariant),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)).background(Rose500.copy(alpha = 0.2f)).border(1.dp, Rose500.copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Rose500, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = kuri.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Slate100,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$dateString • Kuri",
                fontSize = 10.sp,
                color = Slate500
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = format.format(kuri.debit),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Rose400
            )
        }
        Row {
            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppGreen500, modifier = Modifier.size(16.dp))
            }
            IconButton(onClick = { onDelete(kuri) }, modifier = Modifier.size(24.dp).padding(start = 8.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Rose500, modifier = Modifier.size(16.dp))
            }
        }
    }
}
