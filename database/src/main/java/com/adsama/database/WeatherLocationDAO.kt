package com.adsama.database

import androidx.paging.PagingSource
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<PersistedWeatherModel>)

    @Query("SELECT * FROM `PersistedWeatherModel`")
    fun getAllSavedLocations(): Flow<List<PersistedWeatherModel>>

    @Query("SELECT * FROM `PersistedWeatherModel`")
    fun getPagingSavedLocations(): PagingSource<Int, PersistedWeatherModel>

    @Query("SELECT * FROM `PersistedWeatherModel`")
    suspend fun getAllSavedLocationsOnce(): List<PersistedWeatherModel>

    @Delete
    suspend fun deleteLocationInfo(persistedWeatherModel: PersistedWeatherModel)

    @Query("DELETE FROM `PersistedWeatherModel` WHERE locationId = :locationId")
    suspend fun deleteLocationById(locationId: Long)

}
