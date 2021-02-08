package com.nevmem.qms.fragments.registration.checks

class EmptinessCheck : Check {
    override suspend fun check(value: String?): Check.Result {
        return if (value == null || value.isEmpty()) {
            Check.Result.Error("Should not be empty")
        } else {
            Check.Result.Ok
        }
    }
}

class EmailCheck : Check {
    override suspend fun check(value: String?): Check.Result {
        val nonNull = value!!
        return if (nonNull.count { it == '@' } != 1) {
            Check.Result.Error("Not a valid email")
        } else {
            Check.Result.Ok
        }
    }
}

class SimplePasswordCheck : Check {
    override suspend fun check(value: String?): Check.Result {
        val nonNull = value!!
        val checkList = listOf(
            'a'..'z',
            '0'..'9'
        )
        return if (checkList.all { range -> nonNull.any { it in range } }) {
            Check.Result.Ok
        } else {
            Check.Result.Error("Password is too simple")
        }
    }
}

class TooShortCheck : Check {
    override suspend fun check(value: String?): Check.Result {
        return if ((value?.length ?: 0) < 6) {
            Check.Result.Error("Password is too short")
        } else {
            Check.Result.Ok
        }
    }
}
