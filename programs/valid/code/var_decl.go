package main;

// Testing var declarations of base types (float64, rune, string, int)
func temp(n int, m int, l int) float64 {
    // Check declarations of all base types
    var a string = "pears"
    var b int = 3
    var c float64 = 3.5
    var d rune = 'a'
    var e bool = false

    // Check inferred type declarations
    var aa = "pears"
    var bb = 3
    var cc = 3.5
    var ee = true
    var dd = 'a'
    // Check multi declarations of all types
    var x1, x2 int
    var x3, x4 string
    var x5, x6 float64
    var x7, x8 rune
    var x9, x10 bool

    // Check multi inferred type declarations
    var y1, y2, y3, y4 = 62.4, 'a', "string", 4
    var z1, z2 rune = 'c', 'k'
    // Distributed decl
    var (
        a1 string
        x11, x22 int
        y11, y22 = 42, 43
        z11, z22 float64 = 1.3, 2.8
    )

    // Arrays and slice declarations
    var x [][7]int
    var z [12][]string
    // Short decl
    y, j := 0.7, "corn"
    y, _, j, k := 0.7, 'a', "corn", 13

    return z11
}
