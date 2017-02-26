// function call with semicolon separating args

var x int = 2;
var sum int;

func getSum(a int, b int) int{
	return a+b;
}

func main() {
	sum = getSum(2;3);
}