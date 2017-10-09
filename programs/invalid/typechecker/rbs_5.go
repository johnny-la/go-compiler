// non matching return types

package main

func giveMeAFloat() float64{
	return 5;
}

func main(){
	
	for true {
		giveMeAFloat();
	}
}