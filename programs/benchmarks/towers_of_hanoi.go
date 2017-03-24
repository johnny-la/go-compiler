package main

func hanoi(n int, source, target, aux string) {
    if (n >= 1) {
        // Move every disk except the last to aux
        hanoi(n-1, source, aux, target)
        println("Moving disk from ", source, " to ", target)
        // Move every disk except the last to the target
        hanoi(n-1, aux, target, source)
    }
}

func main() {
    hanoi(16,"A","B","C")
}
