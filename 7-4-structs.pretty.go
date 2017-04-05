package main; 
func main () {
    var p struct {
        x, y, zint
};
        (x).p=1;
        (y).p=2;
        (z).p=3;
        var x1, y1, z1 int;
        x1=(x).p;
        y1=(y).p;
        z1=(z).p;
        var q struct {
            nstruct {
                x, y, zint
}
};
                (x).(n).q=1;
                (y).(n).q=2;
                (z).(n).q=3;
                var x2, y2, z2 int;
                x2=(x).(n).q;
                y2=(y).(n).q;
                z2=(z).(n).q;
                var t [3]struct {
                    x, y, zint
};
                    (x).(t[0])=1;
                    (y).(t[0])=2;
                    (z).(t[0])=3;
                }

