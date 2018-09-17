package com.wbrawner.budget.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.wbrawner.budget.data.model.Transaction
import com.wbrawner.budget.data.model.TransactionCategory
import com.wbrawner.budget.data.TransactionType
import com.wbrawner.budget.data.model.TransactionWithCategory

@Dao
interface TransactionDao {
    @Insert(onConflict = REPLACE)
    fun save(transaction: Transaction)

    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun load(id: Int): LiveData<TransactionWithCategory>

    @Query("SELECT * FROM `Transaction` LIMIT :count")
    fun loadMultiple(count: Int): LiveData<List<TransactionWithCategory>>

    @Query("SELECT * FROM `Transaction` WHERE type = :type LIMIT :count")
    fun loadMultipleByType(count: Int, type: TransactionType): LiveData<List<TransactionWithCategory>>

    @Query("SELECT (SELECT TOTAL(amount) from `Transaction` WHERE type = 'INCOME') - (SELECT TOTAL(amount) from `Transaction` WHERE type = 'EXPENSE')")
    fun getBalance(): LiveData<Double>

    @Delete
    fun delete(transaction: Transaction)

    @Query("DELETE FROM `Transaction` WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT id,name from `Category`")
    fun loadCategories(): LiveData<List<TransactionCategory>>
}