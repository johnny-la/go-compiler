package math; 
func average (array [5]int, n int) int {
    var (
        total int = 0
    );
    for i:=0; (i<n); i++ {
        total+=(array[i]);
    };
    return (total/n);
}

func main () {
    var (
        n int = 5
        array [5]int
    );
    (array[0])=10;
    (array[1])=20;
    (array[2])=30;
    (array[3])=40;
    (array[4])=50;
    (println("The average is",average(array,5)));
}

