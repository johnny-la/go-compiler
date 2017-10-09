package hello

// Binary ops on incompatible types
func arb (a int, b int, c int) int {
       var y bool = true
       var z int = 5
       y = 5 <= 7.0

       return y
}
