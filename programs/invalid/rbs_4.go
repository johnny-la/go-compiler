// function call with semicolon separating args

package main

var x int = 2;
var sum int;

func getSum(a int, b int) int{
	return a+b;
}

func main() {
	sum = getSum(2;3);
}