package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DebtCredit
import com.example.data.Transaction
import com.example.data.BAInvestment
import com.example.data.Kuri
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.ui.draw.alpha
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween

@Composable
fun DashboardScreen(
    transactions: List<Transaction>, 
    debts: List<DebtCredit>,
    baInvestments: List<BAInvestment>,
    kuriList: List<Kuri>
) {
    val currentColors = MaterialTheme.colorScheme
    val alphaAnim = remember(currentColors) { Animatable(0f) }
    LaunchedEffect(currentColors) {
        alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    val totalIncome = transactions.filter { it.category == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.category == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense


    val hardCash = transactions.filter { it.cashType == "HARD" }.let {
        it.filter { it.category == "INCOME" }.sumOf { it.amount } - it.filter { it.category == "EXPENSE" }.sumOf { it.amount }
    }
    val softCash = transactions.filter { it.cashType == "SOFT" }.let {
        it.filter { it.category == "INCOME" }.sumOf { it.amount } - it.filter { it.category == "EXPENSE" }.sumOf { it.amount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .alpha(alphaAnim.value),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Total Balance Card
        item {
            TotalBalanceCard(balance, hardCash, softCash)
        }

        // Quick Stats
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Income",
                    amount = totalIncome,
                    color = Emerald400,
                    progress = if (totalIncome + totalExpense > 0) (totalIncome / (totalIncome + totalExpense)).toFloat() else 0f
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Expense",
                    amount = totalExpense,
                    color = Rose400,
                    progress = if (totalIncome + totalExpense > 0) (totalExpense / (totalIncome + totalExpense)).toFloat() else 0f
                )
            }
        }

        // Summaries
        item {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Debt & Credit Summary
                val totalDebts = debts.size
                val completedDebts = debts.count { debt ->
                    val totalAmount = if (debt.credit > debt.debit) debt.credit else debt.debit
                    val paidAmount = parsePayments(debt.paymentsJson).sumOf { it.amount }
                    (totalAmount - paidAmount) <= 0.0
                }
                SummaryCard(
                    title = "Debt & Credit",
                    valueText = "$totalDebts Transactions",
                    subtitleText = "$completedDebts Completed",
                    icon = Icons.Default.SwapHoriz,
                    color = AppGreen500
                )

                // Investment Summary
                val invTotal = baInvestments.sumOf { it.totalAmount }
                val invPaid = baInvestments.sumOf { parsePayments(it.paymentsJson).sumOf { p -> p.amount } }
                val invRemaining = (invTotal - invPaid).coerceAtLeast(0.0)
                
                SummaryCard(
                    title = "Investment",
                    valueText = "${format.format(invPaid)} / ${format.format(invTotal)}",
                    subtitleText = "Remaining: ${format.format(invRemaining)}",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    color = Emerald500
                )

                // Kuri Summary
                val kuriTotal = kuriList.sumOf { it.debit }
                SummaryCard(
                    title = "Kuri",
                    valueText = format.format(kuriTotal),
                    subtitleText = "Total Amount",
                    icon = Icons.Default.AccountBalance,
                    color = Rose500
                )
            }
        }

        // Recent Transactions Ledger
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Transactions", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("See All", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AppGreen400, modifier = Modifier.clickable { })
                    }
                    
                    if (transactions.isEmpty()) {
                        Text("No recent transactions", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(vertical = 16.dp))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            transactions.take(5).forEach { tx ->
                                RecentTxItem(tx)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TotalBalanceCard(balance: Double, hardCash: Double, softCash: Double) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(AppGreen600, AppGreen700)))
            .padding(20.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text("Total Net Balance", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color.White.copy(alpha = 0.8f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Live", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = format.format(balance),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.1f)).padding(8.dp)) {
                    Column {
                        Text("Physical (Hard)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        Text(format.format(hardCash), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
                Box(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.1f)).padding(8.dp)) {
                    Column {
                        Text("Digital (Soft)", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        Text(format.format(softCash), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, title: String, amount: Double, color: Color, progress: Float) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(title.uppercase(), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(format.format(amount), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            
            Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.outlineVariant)) {
                Box(modifier = Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(color))
            }
        }
    }
}

@Composable
fun RecentTxItem(tx: Transaction) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
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
            Text(
                text = tx.category.lowercase().replaceFirstChar { it.uppercase() },
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
fun SummaryCard(title: String, valueText: String, subtitleText: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(valueText, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Text(subtitleText, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
