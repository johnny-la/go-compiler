package main;
//testing var declarations of base types (float64, rune, string, int)
func temp(a int, b int, c int) float64 {
	var x [][7]int
	var y = 42.3
	var z string = "pears"
	//multi variable decl
	var x1, x2 int
	var y1, y2 = 62.4, 'a'
	var z1, z2 rune = 'c', 'k'
	//distributed decl
	var (
		x11, x22 int
		y11, y22 = 42, 43
		z11, z22 float64 = 1.3, 2.8
	)
	//short decl
	y, j := 0.7, "corn"
	y, _, j, k := 0.7, 'a', "corn", 13
	return z11
}
