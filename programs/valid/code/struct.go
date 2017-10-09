package main;

//testing type declarations
type struct_1 []struct {
    str string
    innerStruct struct {
        a, b, c float64
    }
}

func foo(a int, b int, c int) {
    type struct_2 struct {
        str string
        innerStruct struct {
            a, b, c float64
        }
    }
    var x struct {
        str string
        innerStruct struct {
            a, b, c float64
        }
    }

    var y struct {
        str string
        innerStruct struct {
            a, b, c float64
        }
    }
    
    // Test struct assignments
    var z struct_1
    z = append(z, y)
    x.str = "apple"
    x.innerStruct.a = 5.3
    x.innerStruct.b = 3.7
    x.innerStruct.c = 4.7
}
