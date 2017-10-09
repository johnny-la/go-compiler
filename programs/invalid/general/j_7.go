package hello

// Binary ops on incompatible types
func arb (a int, b int, c int) int {
       type int1 int
       var x []int1
       append(x, 1)
       return 3
}
