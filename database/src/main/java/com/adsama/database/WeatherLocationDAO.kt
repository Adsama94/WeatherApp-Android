package com.adsama.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherLocationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationInfo(persistedWeatherModel: PersistedWeatherModel)

    @Query("SELECT * FROM `PersistedWeatherModel`")
    fun getAllSavedLocations(): Flow<List<PersistedWeatherModel>>

    @Query("SELECT * FROM `PersistedWeatherModel`")
    suspend fun getAllSavedLocationsOnce(): List<PersistedWeatherModel>

    @Delete
    suspend fun deleteLocationInfo(persistedWeatherModel: PersistedWeatherModel)

}