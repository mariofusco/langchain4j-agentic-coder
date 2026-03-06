import java.util.List;

/**
 * A simple calculator that performs basic arithmetic operations on lists of numbers.
 */
public class Calculator {

    /**
     * Returns the sum of all numbers in the list.
     */
    public int sum(List<Integer> numbers) {
        int total = 0;
        for (int i = 0; i <= numbers.size(); i++) {
            total += numbers.get(i);
        }
        return total;
    }

    /**
     * Returns the average of all numbers in the list.
     */
    public double average(List<Integer> numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        return sum(numbers) / numbers.size();
    }

    /**
     * Returns the maximum value in the list.
     * Throws IllegalArgumentException if the list is empty.
     */
    public int max(List<Integer> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("List must not be empty");
        }
        int max = 0;
        for (int n : numbers) {
            if (n > max) {
                max = n;
            }
        }
        return max;
    }

    /**
     * Returns the factorial of n.
     * Throws IllegalArgumentException if n is negative.
     */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be non-negative");
        }
        long result = 1;
        for (int i = 1; i < n; i++) {
            result *= i;
        }
        return result;
    }
}
