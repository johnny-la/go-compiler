package main

// Returns the fibonacci of n
func fibonacci(n int) int {
	if (n <= 1) {
		return n
	}

    i_1 := fibonacci(n-1)
    i_2 := fibonacci(n-2)
	return i_1 + i_2
}

func main() {
	println("Fibonacci of 47 is: ", fibonacci(47))
}

