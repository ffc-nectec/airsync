package ffc.airsync

import org.jdbi.v3.core.Jdbi

interface Dao {
    val instant: Jdbi
}
