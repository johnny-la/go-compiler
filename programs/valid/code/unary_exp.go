package main;

func unaryOperationsIntegers(){
	var a = +1;
	var b = -2;
	var c = !(false);
	var d = ^4;

    var multiplePlus = + + + + 1;
    var multipleMinus = - - - - -1;
    var mixedPlusMinus = + - + - - +4;
	var mixedMultipleExclam = !!!!!!!!(false);
	var mixedMultipleCaret = ^^^^^5;

	var complicatedExpression1 = +2-(-5)*(^^^^^^(^6*((-5)*-9+-8%(^2))));
	var complicatedExpression2 = !!!!!(^^^^(^^5^5)>>222 == +1634);
}
