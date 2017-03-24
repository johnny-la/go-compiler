package main

func main() {
        var x int

        // no expression
        switch {
                case x < 0:
			x := 4
			x = 4 - 5 + 3
			print(x)
                case x > 0:
			x := 2
			println(x);
                default:
			println(x);
			x += 3
			break;
        }

        // expression, no default
        switch x {
                case 0:
                case 1, 3, 5, 7, 9:
			x -= 4
			break;
			println(x)
        }

        // statement, no expression,
        switch x++; {
                case x < 0:
                case x > 0:
                default:
        }

        // default in the middle
        switch x++; x {
                case 0:
                default:
                case 1, 3, 5, 7, 9:
        }

        // empty
        switch {

        }
}
