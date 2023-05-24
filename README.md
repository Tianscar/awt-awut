# Abstract Window Toolkit Utilities & Bugfixes
Utilities and bug fixes for Java Abstract Window Toolkit (AWT)

### Running on Java 16+
Add the following lines to the JVM args:
```sh
--add-exports java.desktop/sun.awt=ALL-UNNAMED
--add-exports java.desktop/sun.awt.X11=ALL-UNNAMED  # X11 platform (e.g. GNU/Linux, *BSD) only
```

## Usage
[Examples](src/test/java/com/tianscar/awt/)

## License
[MIT](/LICENSE)  

### Dependencies
| Library                                                     | License    | Comptime | Runtime |
|-------------------------------------------------------------|------------|----------|---------|
| [Java Native Runtime - FFI](https://github.com/jnr/jnr-ffi) | Apache-2.0 | Yes      | Yes     |
