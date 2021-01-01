package net.myplayplanet.services.connection.redis;

@FunctionalInterface
public interface ThrowingConsumer<T> {
    /**
     * Performs this operation on the given argument, but allowing the operation to throw exceptions.
     *
     * @param t the input argument
     */
    void accept(T t) throws Exception;
}
