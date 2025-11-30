/*
 * Copyright © 2025 RTAkland
 * Date: 2025/11/30 20:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */


package krepo.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private var currentJob: Job? = null

fun CoroutineScope.launchJob(block: suspend CoroutineScope.() -> Unit): Job {
    currentJob?.cancel()
    currentJob = this.launch { block() }
    return currentJob!!
}