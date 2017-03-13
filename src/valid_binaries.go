package main

func temp () {
	var a int;
	var b string;
	var c rune;
	var d bool;
	var e float64;
	type here struct {
		x int
	}
	var f here;
	
	d = d || d

	d = d && d

	d = f == f

	d = c!= c

	d = c >= c

	d = a > a

	d = a <= a

	d = e < e

	b = b + b
	a = a + a
	c = c + c
	e = e + e

	a = a - a
	c = c - c
	e = e - e

	a = a / a
	c = c / c
	e = e / e

	a = a * a
	c = c * c
	e = e * e

	a = a % a
	a = a | a
	a = a & a
	a = a << a
	a = a >> a
	a = a &^ a
	a = a ^ a
}
