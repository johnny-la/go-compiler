package main

func main() {
        var x struct {
        	a, b int
        	innerStruct struct {
        		c, d int
        	}
        }

        type z struct {
        	 a, b int
        	innerStruct struct {
        		c, d int
        	}
        }
        type m z
        var n z
        var k m

        if (n == z(k)) {
        	 print(3)
        }
}
