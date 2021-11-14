package me.schooltests.shakespeare.tokens

/**
 * @author Dominick
 * @param collection The backing collection of this queue.
 *
 * This class is a helper class meant to read tokens in a line. The class also provides
 * methods that assert certain tokens ahead, useful for matching patterns in a list of
 * tokens.
 */
class TokenQueue(private var collection: List<Token>) {
    /**
     * The index of the next token in line. The head should always be the index one above
     * the last one returned.
     */
    var nextIndex = 0

    /**
     * Whether the queue is considered empty. This is true only if collections.size is equal
     * to or less than the value of nextIndex.
     */
    private val isEmpty: Boolean
        get() = collection.size <= nextIndex

    fun poll(): Token {
        if (isEmpty)
            throw TokenQueueEmptyException

        val token = collection[nextIndex]
        nextIndex++
        return token
    }

    fun pollOrNull(): Token? {
        if (isEmpty)
            return null

        return poll()
    }

    fun peek(): Token {
        if (isEmpty)
            throw TokenQueueEmptyException

        return collection[nextIndex]
    }

    fun peekOrNull(): Token? {
        if (isEmpty)
            return null

        return peek()
    }

    fun visitIndex(next: Int) {
        nextIndex = next
    }

    fun requireAndConsume(next: Token) {
        val token = poll()
        if (token != next)
            throw TokenQueueAssertionException
    }

    fun requireAndConsumeAny(next: Set<Token>) {
        val token = poll()
        var found = false
        for (possibility in next)
            if (token == next) found = true

        if (!found)
            throw TokenQueueAssertionException
    }

    /**
     * @author Dominick
     * @param other The TokenQueue object that this instance should clone properties from.
     *
     * This method will clone the properties of the provided TokenQueue into this instance.
     */
    fun adopt(other: TokenQueue) {
        collection = other.collection
        nextIndex = other.nextIndex
    }

    /**
     * @author Dominick
     *
     * This method will return a new TokenQueue instance, with a new ArrayList instance as the
     * backing collection, containing the same properties as the current TokenQueue. This is
     * helpful to branch TokenQueue instances when checking for matches of ParserRules.
     */
    fun clone(): TokenQueue {
        return TokenQueue(ArrayList(collection)).also {
            it.nextIndex = this.nextIndex
        }
    }
}

sealed class TokenQueueException(message: String) : Exception(message, null, true, false)
object TokenQueueEmptyException : TokenQueueException("The queue is empty; no more tokens are left to visit.")
object TokenQueueAssertionException : TokenQueueException("The required token is not present.")