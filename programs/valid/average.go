package math

func main() {
    var (
        n int = 5
        array [5]int
    )

    // Populate the array
    array[0] = 10;
    array[1] = 20;
    array[2] = 30
    array[3] = 40
    array[4] = 50

    /**
     * /-/ Print the average of the array //
     */
    println("The average is", average(array, 5))
}

// Returns the average of an array
// with length n
func average(array [5]int, n int) {
    var (
        total int = 0
    )

    for i := 0; i < n; i++ {
        total += array[i];
    }

    return total / n
}