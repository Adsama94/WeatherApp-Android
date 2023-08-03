package com.adsama.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherLocationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationInfo(persistedWeatherModel: PersistedWeatherModel)

    @Query("SELECT * FROM `PersistedWeatherModel`")
    suspend fun getAllSavedLocations(): List<PersistedWeatherModel>

    @Delete
    suspend fun deleteLocationInfo(persistedWeatherModel: PersistedWeatherModel)

}