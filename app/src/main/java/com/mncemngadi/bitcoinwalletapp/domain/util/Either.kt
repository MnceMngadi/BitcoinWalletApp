package com.mncemngadi.bitcoinwalletapp.domain.util

/**
 * A container that holds one of two possible types.
 * Convention: [Left] represents Failure, [Right] represents Success.
 */
sealed class Either<out L, out R> {
    /** Failure side */
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    /** Success side */
    data class Right<out R>(val b: R) : Either<Nothing, R>()
}

/**
 * Transforms the value if this is a [Right].
 */
fun <L, R, T> Either<L, R>.map(fn: (R) -> T): Either<L, T> =
    when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> Either.Right(fn(b))
    }
