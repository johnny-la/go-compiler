package main;
//testing type declarations
func int(a int, b int, c int) {
	//aliasing
	type strAlias string
	type int float64
	//struct nesting
	type struct_1 struct 
	{
		aliasedStr strAlias
		innerStruct struct 
		{
			a, b, c float64
		}
	}
	//struct declarations with arrays
	type struct_2 []struct 
	{
		innerStruct struct 
		{
			a, b, c float64
		}
	}

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
