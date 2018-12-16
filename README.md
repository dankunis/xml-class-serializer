# XML Class Serializer
Serialize any class to XML-type using annotations and Reflection API.

## Insrtuctions
1. Mark a class with the @XMLObject annotation
> By default the name equals to the name of the class

`@XMLObject(name = "Person")`

2. Mark fields and methods with the @XMLTag annotation that you want to become tags in the resulted XML.
> By default the name equals to the name of the field/method

`@XMLTag(name = "Person")`

3. Mark fields and methods with the @XMLAttribute annotation that you want to become attributes of the chosen tag in the resulted XML
> By default added attribute will refer to the root tag

`@XMLAttribute(tag = "Child", name = "age")`

4. Call the serialized method of the XMLConverter

`XMLConverter.serialized(person)`

## Specifications and requirements

- Methods must not have any parameters
- Methods cannot return void
- Access modifiers will be ignored
- The code requires jaxen and dom4j libraries

## Usage examples

Define a class:

```java
@XMLObject
public class Person {
    @XMLTag(name = "fullname")
    private final String name;

    @XMLAttribute(tag = "fullname")
    private final String lang;

    @XMLAttribute
    private final int age;

    public Person(String name, String lang, int age, ChildPerson child) {
        this.name = name;
        this.lang = lang;
        this.age = age;
        this.child = child;
    }
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
```

Serialize

```java
public class Main {
    public static void main(String[] args) {
        Person person = new Person("Sergey", "RUS", 32, new ChildPerson());

        try {
            var serialized = XMLConverter.serialized(person);
            OutputFormat format = OutputFormat.createPrettyPrint();
            var outputStream = new FileOutputStream("output.xml");
            var writer = new XMLWriter(outputStream, format);
            writer.write(serialized);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }
}
```

output.xml

    <?xml version="1.0" encoding="UTF-8"?>
    
    <Person age="32">
      <fullname lang="RUS">Sergey</fullname>
      <Child>
        <test>test</test>
      </Child>
    </Person>
