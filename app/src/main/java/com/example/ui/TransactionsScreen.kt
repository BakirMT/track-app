package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Transaction
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    transactions: List<Transaction>, 
    onDelete: (Transaction) -> Unit,
    onEdit: (Transaction) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var dateFilter by remember { mutableStateOf("All") }
    
    val dateFilters = listOf("All", "Last 7 Days", "Last 30 Days", "This Month", "This Year")
    
    val filteredTransactions = transactions.filter { tx ->
        val matchesSearch = tx.details.contains(searchQuery, ignoreCase = true) || 
                            tx.type.contains(searchQuery, ignoreCase = true) ||
                            tx.category.contains(searchQuery, ignoreCase = true) ||
                            tx.tag.contains(searchQuery, ignoreCase = true)
                            
        val matchesDate = when (dateFilter) {
            "Last 7 Days" -> tx.date >= System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
            "Last 30 Days" -> tx.date >= System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
            "This Month" -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                tx.date >= cal.timeInMillis
            }
            "This Year" -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                tx.date >= cal.timeInMillis
            }
            else -> true
        }
        matchesSearch && matchesDate
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search transactions...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Slate700,
                    focusedBorderColor = AppGreen500
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dateFilters) { filter ->
                    FilterChip(
                        selected = dateFilter == filter,
                        onClick = { dateFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppGreen500.copy(alpha = 0.2f),
                            selectedLabelColor = AppGreen500
                        )
                    )
                }
            }
        }

        if (filteredTransactions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = if (transactions.isEmpty()) "No transactions yet." else "No matches found.", 
                    color = Slate500
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredTransactions) { tx ->
                    TransactionItem(
                        tx, 
                        onDelete = { onDelete(tx) },
                        onEdit = { onEdit(tx) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction, onDelete: () -> Unit, onEdit: () -> Unit) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(tx.date))
    val isIncome = tx.category == "INCOME"
    val color = if (isIncome) Emerald500 else Rose500
    val icon = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward

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
                modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)).background(color.copy(alpha = 0.2f)).border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.details.ifEmpty { "Transaction" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$dateString • ${tx.type} • ${tx.cashType}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            val tagColor = when(tx.tag) {
                "Food" -> Color(0xFFF59E0B)
                "Transport" -> Color(0xFF3B82F6)
                "Rent" -> Color(0xFF10B981)
                "Salary" -> Color(0xFF14B8A6)
                "Shopping" -> Color(0xFFEC4899)
                "Entertainment" -> Color(0xFF8B5CF6)
                else -> Color(0xFF6B7280)
            }
            Surface(
                color = tagColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = tx.tag,
                    color = tagColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${if (isIncome) "+" else "-"}${format.format(tx.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) AppGreen400 else MaterialTheme.colorScheme.onSurface
            )
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AppGreen500, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Rose500, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
