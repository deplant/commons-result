# Deplant Commons Result API

[![JDK version](https://img.shields.io/badge/Java-21-green.svg)](https://shields.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-brown.svg)](https://shields.io/)

* Discuss in
  Telegram: [![Channel on Telegram](https://img.shields.io/badge/chat-on%20telegram-9cf.svg)](https://t.me/deplant\_chat\_en)
* Read full
  docs: [![javadoc](https://javadoc.io/badge2/tech.deplant.commons/commons-core/javadoc.svg)](https://javadoc.io/doc/tech.deplant.commons/result)

**Deplant Commons Result API** implements **Result Pattern** for Java. It is a small 
Java library with a wrapper class named `Result` with sealed implementations `Ok` 
and `Err`. 
Idea comes from Rust [std.Result crate](https://doc.rust-lang.org/std/result/) with 
some changes in API to better suite Java needs.

Made in functional style, it perfectly matches with **Pattern matching** techniques 
or more traditional conditional statements
to manage results.

## Writing function that can throw
```java
public static Result<Integer> functionCanFail(Integer i) {
    return Result.of(() -> 2 / i);
}
```

## Pattern matching result variants

As `Result` is a **sealed** interface, you can **pattern match** it without `default` branch.

```java
var result = Result.of(() -> 8 / 0);

switch (result) {
    case Ok(Integer i) when i > 2 -> System.out.println("Good!");
    case Ok ok -> System.out.println("Ok: " + ok.result());
    case Err err -> System.out.println("Err: " + err.error().getMessage());
}
// prints "Err: / by zero"
```

## Mapping and unwrapping results

Results can be chained with other results through `.result.mapResult( functionThatCanFail )`,
wrapped value and type can be transformed though `.map( functionThatShouldntFail )`. 
If you like to transpose `Result<>` to `Optional<>` you can do it with `Result.ofOptional()` 
, `.ok()` and `.err()` methods.

```java
Result<Integer> goodResult = Result.of(() -> 7);
Result<Integer> multipliedResult = goodResult.map(i -> i * 2); // transformations
Result<String> stringResult = goodResult.map(Object::toString); // type transformations
Result<Integer> combinedResult = goodResult.mapResult(s -> functionCanFail(s)); // transformations with other results
Integer digit = combinedResult.orElse(2); // get or default when fail
Optional<Integer> optionalDigit = combinedResult.ok(); // get as optional (empty when fail)
```

#### Add to your Maven of Gradle setup:

* Gradle

```groovy
dependencies {
    implementation 'tech.deplant.commons:result:0.1.0'
}
```

* Maven

```xml
<dependency>
    <groupId>tech.deplant.commons</groupId>
    <artifactId>result</artifactId>
    <version>0.1.0</version>
</dependency>
```