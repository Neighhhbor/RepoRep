def greet(name):
    """A simple greeting function"""
    return f"Hello, {name}!"

class Person:
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def introduce(self):
        return f"My name is {self.name} and I'm {self.age} years old."

if __name__ == "__main__":
    person = Person("Alice", 30)
    print(greet(person.name))
    print(person.introduce())

