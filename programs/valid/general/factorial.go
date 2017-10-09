package main

/*
 * Computes the fibonacci of 0,1,...,n
 * and stores it in memo[]
 */
func fib_helper(n int, memo []int) {
    var i int = 2;

    for i <= n {
        memo = append(memo, memo[i-1] + memo[i-2])

        continue
    }

    return
}

/* Returns the fibonacci of n */
func fib(n int) int {
    if n < 0 {
        println("Error: fibonacci cannot accept a negative number");
        return -1
    } else if n >=0 {
        var memo []int;
        memo = append(memo, 0);
        memo = append(memo, 1)

        fib_helper(n, memo)

        return memo[n];
    } else {
        return -1
    }
}

func main() {
    print(fib(3))
}
