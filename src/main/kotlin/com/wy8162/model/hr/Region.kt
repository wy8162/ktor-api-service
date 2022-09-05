package com.wy8162.model.hr

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

data class Region(
    val regionId: Int,
    val regionName: String
)

object RegionEntity : Table(name = "regions") {
    val regionId = integer("region_id").uniqueIndex("regions_pkey")
    val regionName = varchar("region_name", 64)

    override val primaryKey = PrimaryKey(regionId, name = "regions_pkey")
}

fun ResultRow.toRegion(): Region = Region(
    regionId = this[RegionEntity.regionId],
    regionName = this[RegionEntity.regionName]
)
