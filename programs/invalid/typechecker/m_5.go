package test

type point struct {
    x,y int
}

var p1 point

func test() {
    type point struct {
        x,y int
    }

    var p2 point
 
    if p1 < p2 {

    }
}

