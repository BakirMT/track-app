package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val passwordHash: String,
    val otp: String? = null
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String = "",
    val date: Long = System.currentTimeMillis(),
    val type: String,
    val category: String, // "INCOME" or "EXPENSE"
    val amount: Double,
    val cashType: String, // "HARD" or "SOFT"
    val details: String,
    val tag: String = "Other"
)

@Entity(tableName = "debts")
data class DebtCredit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String = "",
    val date: Long = System.currentTimeMillis(),
    val description: String,
    val debit: Double,
    val credit: Double,
    val paymentsJson: String = "[]"
)

@Entity(tableName = "ba_investments")
data class BAInvestment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String = "",
    val date: Long = System.currentTimeMillis(),
    val name: String,
    val totalAmount: Double,
    val paymentsJson: String = "[]"
)

@Entity(tableName = "kuri")
data class Kuri(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String = "",
    val date: Long = System.currentTimeMillis(),
    val description: String,
    val debit: Double
)

@Dao
interface FinanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUser(email: String): User?

    @Query("SELECT * FROM transactions WHERE email = :email ORDER BY date DESC")
    fun getAllTransactions(email: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM debts WHERE email = :email ORDER BY date DESC")
    fun getAllDebts(email: String): Flow<List<DebtCredit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtCredit)

    @Delete
    suspend fun deleteDebt(debt: DebtCredit)

    @Query("SELECT * FROM ba_investments WHERE email = :email ORDER BY date DESC")
    fun getAllBAInvestments(email: String): Flow<List<BAInvestment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBAInvestment(investment: BAInvestment)

    @Delete
    suspend fun deleteBAInvestment(investment: BAInvestment)

    @Query("SELECT * FROM kuri WHERE email = :email ORDER BY date DESC")
    fun getAllKuri(email: String): Flow<List<Kuri>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKuri(kuri: Kuri)

    @Delete
    suspend fun deleteKuri(kuri: Kuri)
}

@Database(entities = [User::class, Transaction::class, DebtCredit::class, BAInvestment::class, Kuri::class], version = 6, exportSchema = false)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun financeDao(): FinanceDao

    companion object {
        @Volatile
        private var Instance: FinanceDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE transactions ADD COLUMN tag TEXT NOT NULL DEFAULT 'Other'")
            }
        }

        fun getDatabase(context: Context): FinanceDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FinanceDatabase::class.java, "finance_db")
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .build().also { Instance = it }
            }
        }
    }
}

class FinanceRepository(private val dao: FinanceDao) {
    suspend fun insertUser(user: User) = dao.insertUser(user)
    suspend fun getUser(email: String) = dao.getUser(email)

    fun getAllTransactions(email: String): Flow<List<Transaction>> = dao.getAllTransactions(email)
    fun getAllDebts(email: String): Flow<List<DebtCredit>> = dao.getAllDebts(email)
    fun getAllBAInvestments(email: String): Flow<List<BAInvestment>> = dao.getAllBAInvestments(email)
    fun getAllKuri(email: String): Flow<List<Kuri>> = dao.getAllKuri(email)

    suspend fun insertTransaction(tx: Transaction) = dao.insertTransaction(tx)
    suspend fun deleteTransaction(tx: Transaction) = dao.deleteTransaction(tx)

    suspend fun insertDebt(debt: DebtCredit) = dao.insertDebt(debt)
    suspend fun deleteDebt(debt: DebtCredit) = dao.deleteDebt(debt)

    suspend fun insertBAInvestment(investment: BAInvestment) = dao.insertBAInvestment(investment)
    suspend fun deleteBAInvestment(investment: BAInvestment) = dao.deleteBAInvestment(investment)

    suspend fun insertKuri(kuri: Kuri) = dao.insertKuri(kuri)
    suspend fun deleteKuri(kuri: Kuri) = dao.deleteKuri(kuri)
}
