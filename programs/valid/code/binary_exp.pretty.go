package main; 
func binaryOperationsIntegers (a int, b int, c int, j rune, k rune, str1 string, str2 string) {
    var addition = (a+b);
    var runeAdd = (j+k);
    var strConcatenation = (str1+str2);
    var subtraction = (a-b);
    var multiplication = (a*b);
    var division = (a/b);
    var remainder = (a%b);
    var piping = (a|b);
    var careting = (a^b);
    var ampersand = (a&b);
    var ampersand_caret = (a&^b);
    var shift_left = (a<<b);
    var shift_right = (a>>b);
    var bool_val1 = (a==b);
    var bool_val2 = (a!=b);
    var bool_val3 = (a<b);
    var bool_val4 = (a<=b);
    var bool_val5 = (a>b);
    var bool_val6 = (a>=b);
    var bool_val7 = (bool_val6||bool_val5);
    var bool_val8 = (bool_val4&&bool_val3);
    var d = ((a*b)+2);
    var e = (a^(2/b));
    var l = ((a+b)-c);
    var m = ((a*b)/c);
    var n = ((a==b)!=false);
    var f = (a*(b+2));
    var g = ((bool_val1&&bool_val2)||bool_val3);
    var h = ((a<=b)&&false);
    var i = ((a!=b)||true);
    var o = ((true&&false)||true);
    var complicated_expression1 = ((addition*(multiplication/remainder))^((((shift_right%2)%5)&^6)>>(((55<<2)*piping)|42354)));
    var complicated_expression2 = ((bool_val1&&(bool_val8==bool_val6))||((bool_val1&&bool_val7)&&(2<3)));
}

