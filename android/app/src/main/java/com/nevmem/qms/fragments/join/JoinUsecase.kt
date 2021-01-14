package com.nevmem.qms.fragments.join

import androidx.lifecycle.LiveData
import com.nevmem.qms.status.FetchStatus

interface JoinUsecase {
    var fetchStatus: FetchStatus?

    val invite: LiveData<String>
}
