package com.wy8162.model.hr

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

data class Country(
    val country_id: String,
    val countryName: String,
    val regionId: Int
)

object CountryEntity : Table(name = "countries") {
    val country_id = varchar("country_id", 2).uniqueIndex("countries_pkey")
    val countryName = varchar("country_name", 255)
    val regionId = integer("region_id") references RegionEntity.regionId

    override val primaryKey = PrimaryKey(country_id, name = "countries_pkey")
}

fun ResultRow.toCountry(): Country = Country(
    country_id = this[CountryEntity.country_id],
    countryName = this[CountryEntity.countryName],
    regionId = this[CountryEntity.regionId]
)
