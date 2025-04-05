# Detekt Rules

These are rules for detekt that enforce kord-extension conventions

## Rules

### `InterfaceNameCannotBePrefixedWithI`

Ensures that interfaces are not prefixed with `i`

#### Example

Fails:

```kotlin
interface IThing
```

Succeeds:

```kotlin
interface Thing
```
