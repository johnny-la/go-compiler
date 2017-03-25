package main;
//testing type declarations
func apple(a int, b int, c int) {
	//aliasing
	type strAlias string
	type floAlias float64
	type runAlias rune
	type boolAlias bool
	type intAlias int

	//struct nesting
	type struct_1 struct 
	{
		aliasedStr strAlias
		arrayOfInts [][7]int
		innerStruct struct 
		{
			a, b, c float64
		}
	}
	//struct declarations with arrays
	type struct_2 []struct 
	{
		b int
		innerStruct struct 
		{
			a, b, c float64
		}
	}
	var arrstr struct_2
	//distributed declarations
	type (
		s1_alias struct_1
		x float64
		n []struct_2
		struct_3 struct {
			random int
		}
	)
}
