package main

import (
	"fmt"
	"time"
	"math/rand"
	"strings"
	"encoding/json"
	"os"
	"log"
	"net/http"
)

type User struct {
	ID   int    `json:"id"`
	Name string `json:"name"`
	Age  int    `json:"age"`
}

func generateRandomUser() User {
	names := []string{"张三", "李四", "王五", "赵六", "钱七"}
	return User{
		ID:   rand.Intn(1000),
		Name: names[rand.Intn(len(names))],
		Age:  rand.Intn(50) + 18,
	}
}

func printUserInfo(user User) {
	fmt.Printf("用户 ID: %d, 姓名: %s, 年龄: %d\n", user.ID, user.Name, user.Age)
}

func saveUserToFile(user User) error {
	data, err := json.Marshal(user)
	if err != nil {
		return err
	}
	return os.WriteFile(fmt.Sprintf("user_%d.json", user.ID), data, 0644)
}

func handleUserRequest(w http.ResponseWriter, r *http.Request) {
	user := generateRandomUser()
	printUserInfo(user)
	err := saveUserToFile(user)
	if err != nil {
		http.Error(w, "保存用户信息失败", http.StatusInternalServerError)
		return
	}
	json.NewEncoder(w).Encode(user)
}

func main() {
	rand.Seed(time.Now().UnixNano())

	http.HandleFunc("/user", handleUserRequest)

	fmt.Println("服务器正在运行在 http://localhost:8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
