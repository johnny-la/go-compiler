package main

func temp () {
	type int1 int;
	var a int;
	var b string;
	var c rune;
	var _ bool;
	var d bool;
	var e float64;
	var x [7]int1;
	type here struct {
		x int
	}
	var f here;

	var z = append(x, int1(bool(3)))

	if (_ || d) {
	}

	if (d && e) {
	}

	if (e == f) {
	}

	if (c != d) {
	}

	if (a >= a) {
	}

	if (b > a) {
	}

	if (a <= e) {
	}

	if (e < f) {
	}

	b = b + b
	a = a + a
	c = c + c
	e = e + e

	a = a - a
	c = c - a
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
