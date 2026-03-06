import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class CalculatorTest {

    private final Calculator calc = new Calculator();

    @Test
    void sum_of_positive_numbers() {
        assertEquals(10, calc.sum(List.of(1, 2, 3, 4)));
    }

    @Test
    void sum_of_empty_list() {
        assertEquals(0, calc.sum(List.of()));
    }

    @Test
    void average_of_positive_numbers() {
        assertEquals(2.5, calc.average(List.of(1, 2, 3, 4)));
    }

    @Test
    void average_of_empty_list() {
        assertEquals(0, calc.average(List.of()));
    }

    @Test
    void max_of_positive_numbers() {
        assertEquals(9, calc.max(List.of(3, 9, 1, 7)));
    }

    @Test
    void max_with_negative_numbers() {
        assertEquals(-1, calc.max(List.of(-5, -1, -3)));
    }

    @Test
    void max_of_empty_list_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.max(List.of()));
    }

    @Test
    void factorial_of_5() {
        assertEquals(120, calc.factorial(5));
    }

    @Test
    void factorial_of_0() {
        assertEquals(1, calc.factorial(0));
    }

    @Test
    void factorial_of_1() {
        assertEquals(1, calc.factorial(1));
    }

    @Test
    void factorial_of_negative_throws() {
        assertThrows(IllegalArgumentException.class, () -> calc.factorial(-1));
    }
}
