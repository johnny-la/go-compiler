package hello

// Binary ops on incompatible types
func arb (a int, b int, c int) int {
       var y = 3
       y = y + 3.0
       y = y - 3.0
       y = y / 3.0
       y = y * 3.0

       return y
}
