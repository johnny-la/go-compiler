package main;
//testing type declarations
func int(a int, b int, c int) {
	type struct_1 struct 
	{
		str string
		innerStruct struct 
		{
			a, b, c float64
		}
	}
	var x struct_1
	x.str = "apple"
	x.innerStruct.a = 5.3
	x.innerStruct.b = 3.7
	x.innerStruct.c = 4.7
}
