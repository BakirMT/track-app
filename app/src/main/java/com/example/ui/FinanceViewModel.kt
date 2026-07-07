package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    
    private val _currentUser = MutableStateFlow<String?>(auth.currentUser?.email)
    val currentUser: StateFlow<String?> = _currentUser

    fun login(email: String) {
        _currentUser.value = email
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    val transactions: StateFlow<List<Transaction>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getAllTransactions(user) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debts: StateFlow<List<DebtCredit>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getAllDebts(user) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val baInvestments: StateFlow<List<BAInvestment>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getAllBAInvestments(user) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kuri: StateFlow<List<Kuri>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getAllKuri(user) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(tx: Transaction) = viewModelScope.launch { 
        _currentUser.value?.let { user -> repository.insertTransaction(tx.copy(email = user)) }
    }
    fun deleteTransaction(tx: Transaction) = viewModelScope.launch { repository.deleteTransaction(tx) }
    
    fun addDebt(debt: DebtCredit) = viewModelScope.launch { 
        _currentUser.value?.let { user -> repository.insertDebt(debt.copy(email = user)) } 
    }
    fun deleteDebt(debt: DebtCredit) = viewModelScope.launch { repository.deleteDebt(debt) }

    fun addBAInvestment(investment: BAInvestment) = viewModelScope.launch { 
        _currentUser.value?.let { user -> repository.insertBAInvestment(investment.copy(email = user)) } 
    }
    fun deleteBAInvestment(investment: BAInvestment) = viewModelScope.launch { repository.deleteBAInvestment(investment) }
    
    fun addKuri(kuri: Kuri) = viewModelScope.launch { 
        _currentUser.value?.let { user -> repository.insertKuri(kuri.copy(email = user)) } 
    }
    fun deleteKuri(kuri: Kuri) = viewModelScope.launch { repository.deleteKuri(kuri) }

    fun signup(email: String, passwordHash: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, passwordHash)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUser.value = email
                    onResult(true, "Success")
                } else {
                    onResult(false, task.exception?.message ?: "Signup failed")
                }
            }
    }

    fun signin(email: String, passwordHash: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, passwordHash)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUser.value = email
                    onResult(true, "Success")
                } else {
                    onResult(false, task.exception?.message ?: "Invalid credentials")
                }
            }
    }

    fun sendPasswordResetLink(email: String, onResult: (Boolean, String) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Password reset link sent! Check your inbox.")
                } else {
                    onResult(false, task.exception?.message ?: "Failed to send reset email")
                }
            }
    }

    // Unused when using Firebase, but kept for compatibility
    fun resetPassword(email: String, otp: String, newPasswordHash: String, onResult: (Boolean, String) -> Unit) {
        onResult(false, "Unsupported in Firebase Auth client SDK")
    }
}

class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
