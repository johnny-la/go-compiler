package main

var x = 2;
var y = 3;

func main(){
	swap(x, y);
}

func swap(x int, y int){
	x = x ^ y;
	y = y ^ x;
	x = x ^ y;
}

