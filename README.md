# JControls

A library that provide useful functional control types.

- [x] Either
- [x] Try
- [ ] Maybe

## Add Maven Dependency

```xml
<dependency>
  <groupId>io.github.s-frick</groupId>
  <artifactId>controls</artifactId>
  <version>0.3.1</version>
</dependency>
```

## Example Usage Either

```java
  interface BusinessError {}
  interface BusinessObject {}
  interface Account extends BusinessObject {}
  interface User extends BusinessObject {}

  Either<BusinessError, BusinessObject> methodThatCanFail(boolean fail) {
    return fail ? Either.failure(new BusinessError() {}) : Either.success(new BusinessObject() {});
  }

  Either<BusinessError, User> getUser(Account acc) {
    return Either.success(new User() {});
  }

  void doStuff() {
    Either<BusinessError, BusinessObject> aBusinessObject = methodThatCanFail(true);

    aBusinessObject
      .map(this::toAccount)
      .flatMap(this::getUser)
      .ifPresent(this::doOtherStuff);
  }
```

## From Try&lt;T&gt; to Either&lt;BusinessError, T&gt;

```java
  void doStuff(){
    Try<BusinessObject> tryStuff = methodThatCanThrow();
    Either<Throwable, BusinessObject> aBusinessObject = tryStuff.toEither();

    Either<BusinessError, BusinessObject> transformed =
        aBusinessObject.mapLeft(FailureTransformer::transformFailure);
  }
```

If you using Java < 21

```java
  BusinessError transformFailure(Throwable t) {
    if (t instanceof RuntimeException e) {
      return new SomeUsefulError("anError");
    }
    if (t instanceof Exception e) {
      return new OtherUsefulError("otherError");
    }
    return new BusinessError() {};
  }
```

If you using Java > 21

```java
  BusinessError transformFailure2(Throwable t) {
    return switch(t) {
      case RuntimeException e -> new SomeUsefulError("anError");
      case Exception e        -> new OtherUsefulError("anError");
      default                 -> new BusinessError() {};
    };
  }
```
