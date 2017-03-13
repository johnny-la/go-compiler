package test 

func test() int {

}

func test2() {
    x := test()

    for y := 4; true; test() {
    } 

    y &= 4
}
