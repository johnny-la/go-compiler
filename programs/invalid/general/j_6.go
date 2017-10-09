package hello

// Incorrect number of args
func arb (a int, b int, c int) int {
       var y = 3
       return y
}

func temp () int {
    return arb(3, 7.0)

}
