package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.example.data.Transaction
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun PlanningScreen(transactions: List<Transaction>) {
    // Group transactions by month-year
    val grouped = transactions.groupBy { tx ->
        val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
        val month = cal.get(Calendar.MONTH)
        val year = cal.get(Calendar.YEAR)
        Pair(year, month)
    }.toSortedMap(compareByDescending<Pair<Int, Int>> { it.first }.thenByDescending { it.second })

    if (grouped.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("No data for planning.", color = Slate500)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(grouped.entries.toList()) { entry ->
                val (year, month) = entry.key
                val monthTransactions = entry.value
                val monthName = Calendar.getInstance().apply { set(Calendar.MONTH, month) }
                    .getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) ?: ""
                
                MonthMatrixCard("$monthName $year", monthTransactions)
            }
        }
    }
}

@Composable
fun MonthMatrixCard(title: String, transactions: List<Transaction>) {
    val totalIncome = transactions.filter { it.category == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.category == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MatrixItem("Income", format.format(totalIncome), Emerald400)
                Spacer(modifier = Modifier.width(16.dp))
                MatrixItem("Expenses", format.format(totalExpense), Rose400)
                Spacer(modifier = Modifier.width(16.dp))
                MatrixItem("Net Balance", format.format(balance), if (balance >= 0) AppGreen400 else Rose400)
            }
        }
    }
}

@Composable
fun MatrixItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column {
        Text(label, fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
