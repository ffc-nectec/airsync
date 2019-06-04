package ffc.airsync.utils

import java.util.LinkedList

private val jobs = LinkedList<Any>()

fun jobCount(): Int {
    synchronized(jobs) {
        return jobs.count()
    }
}

private fun jobAdd(job: Any): Int {
    synchronized(jobs) {
        jobs.add(job)
        return jobs.count()
    }
}

private fun jobRemove(job: Any): Int {
    synchronized(jobs) {
        jobs.remove(job)
        return jobs.count()
    }
}

fun jobFFC(t: () -> Unit) {
    jobAdd(t)
    t()
    jobRemove(t)
}
