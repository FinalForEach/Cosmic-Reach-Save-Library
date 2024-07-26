package finalforeach.cosmicreach.savelib.utils;

@FunctionalInterface
public interface TriConsumer<A, B, C>
{

    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     * @param c the second input argument
     */
    void accept(A a, B b, C c);
}