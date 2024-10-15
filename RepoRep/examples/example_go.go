package main

import "fmt"

func multiply(a int, b int) int {
    return a * b
}

func main() {
    result := multiply(5, 3)
    fmt.Println("Multiplication:", result)
}
