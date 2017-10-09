package main

var x int = 2;
var sum int;

func getSum(a int, b int) int {
    return a+b;
}

func main() {
    // Function call with semicolon separating args
    sum = getSum(2;3);
}