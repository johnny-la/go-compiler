package main

// Non-matching return types
func giveMeAFloat() float64 {
	return 5;
}

func main() {
	for true {
		giveMeAFloat();
	}
}