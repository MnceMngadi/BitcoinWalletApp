package com.mncemngadi.bitcoinwalletapp.domain.util

/**
 * Represents a value of one of two possible types (a disjoint union).
 * Instances of [Either] are either an instance of [Left] or [Right].
 * FP convention dictates that [Left] is used for "failure"
 * and [Right] is used for "success".
 */
sealed class Either<out L, out R> {
    data class Left<out L>(val a: L) : Either<L, Nothing>()

    data class Right<out R>(val b: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <L> left(a: L) = Left(a)

    fun <R> right(b: R) = Right(b)

    fun fold(
        fnL: (L) -> Any,
        fnR: (R) -> Any,
    ): Any =
        when (this) {
            is Left -> fnL(a)
            is Right -> fnR(b)
        }
}

fun <A, B, C> ((A) -> B).c(f: (B) -> C): (A) -> C =
    {
        f(this(it))
    }

fun <L, R, T> Either<L, R>.flatMap(fn: (R) -> Either<L, T>): Either<L, T> =
    when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> fn(b)
    }

fun <L, R, T> Either<L, R>.map(fn: (R) -> T): Either<L, T> =
    when (this) {
        is Either.Left -> Either.Left(a)
        is Either.Right -> Either.Right(fn(b))
    }
