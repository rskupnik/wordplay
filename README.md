# Wordplay

Wordplay is a small library for text transformation, intended to be used in dynamic storytelling.
It's designed to work with text-based games where you want the text to change based on some input and to attach parameters to parts of text or whole scenes to be interpreted by the user.

## How to get it?

Currently, only `git clone` -> `mvn install`, but will be uploaded to a repository soon enough.

## What can it do?

Wordplay does 3 things:
* **Injection** - inject variable text from the outside or the inside of the script
* **Processing** - process simple *ternary* or *matching* expressions that resolve to different text based on provided variables
* **Emission** - emit objects with parameters that can be interpreted by an external system in an arbitrary way

This list defined also the **order of execution**. This means that, for example, at the point when *ternary* or *matching* expressions are evaluated, any *injection* expressions nested inside them will already be resolved.

### Injection

There are two types of injection: *external* and *internal*. The latter is mainly used for code clarity, separating data from code in case of advanced scripts. The first one is used to pass arbitrary text portions from the outside.

```java
// External injection
public static void main(String[] args) {
  String input = "There was a {> man} standing in the middle of the room.";
  Wordplay wordplay = new WordplayImpl();
  wordplay.inject("man", "tired, old man");
  WordplayOutput output = wordplay.process(input);
  System.out.println(output.getText());
  // Produces: There was a tired, old man standing in the middle of the room.
}
```

```java
// Internal injection
// Notice how we don't need to use wordplay.inject() in this case
public static void main(String[] args) {
  String input = "There was a {> 0} standing in the middle of the room.\n" +
                 "$\n" +                  // A line with just the $ sign separates data from code
                 "> 0 tired, old man";    // Syntax: > name text
  Wordplay wordplay = new WordplayImpl();
  WordplayOutput output = wordplay.process(input);
  System.out.println(output.getText());
  // Produces: There was a tired, old man standing in the middle of the room.
}
```

You can nest injections:

```java
String input = "There was a {> 0} standing in the middle of the room.\n" +
               "$\n" +
               "> 0 {> man}";
(...)
wordplay.inject("man", "tired, old man");
(...)
```

But be careful not to create a cyclic nested injection - it will cause an endless loop:

```java
// Don't do this!
// Wordplay will keep finding an expression to be processed and will never finish!
String input = "There was a {> 0} standing in the middle of the room.\n" +
               "$\n" +
               "> 0 {> man}";
(...)
wordplay.inject("man", "{> 0}");
(...)
```

### Processing

There are two types of expressions currently supported: *ternary* and *matching*.
You can nest them in each other and you can use them together with injection expressions.

#### Ternary expressions

Syntax: `{ param ? value if true | value if false }`

Usage:

```java
public static void main(String[] args) {
  String input = "It was a {weather_sunny ? sunny | rainy} day.";
  Wordplay wordplay = new WordplayImpl();
  wordplay.setVariable("weather_sunny", true);
  WordplayOutput output = wordplay.process(input);
  System.out.println(output.getText());
  // Produces: It was a sunny day.
}
```

#### Matching expressions

Syntax: `{ param:value text if matches value |:different_value text if matches different_value | text if matches nothing }`

Usage:

```java
public static void main(String[] args) {
  String input = "It was a {weather:sunny sunny |:cloudy cloudy | normal} day.";
  Wordplay wordplay = new WordplayImpl();
  wordplay.setVariable("weather", "sunny");
  WordplayOutput output = wordplay.process(input);
  System.out.println(output.getText());
  // Produces: It was a sunny day.
}
```

### Emission

If *injection* and *processing* are means of providing input to the transformed text, *emission* is a means of getting an output.
There are two kinds of emitted objects: *anchored objects* and *meta objects*. The difference is that *anchored objects* are anchored in text - they have a fixed position, while *meta objects* are just data in form of a *list* or a *map* that is attached to the whole scene.

#### Anchored objects

Syntax: `{< param1:value1|param2:value2 text }`

Usage:

```java
public static void main(String[] args) {
  String input = "The magic stone was {< effect:vibrate|tint:blue vibrating} slightly.";
  Wordplay wordplay = new WordplayImpl();
  WordplayOutput output = wordplay.process(input);
  System.out.println(output.getText());
  // Produces: The magic stone was vibrating slightly.
  AnchoredObject obj = output.getAnchoredObjects().get(0);
  System.out.println(obj.getText());                      // vibrating
  System.out.println(obj.getParameters().get("effect");   // vibrate
  System.out.println(obj.getPosition());                  // 20
}
```

They can only be used in the data section of the script - because they need to be anchored to something.
You can use *internal injection* if you want to define them in the code section, or even *external injection* to inject them from the outside.

#### Meta objects

They can only be defined in the code section and are defined per line.

Syntax for list: `<l name some parameter|another parameter|yet another one`

The `l` decides this meta object will be a list.

Syntax for map: `<m name param1:value1|param2:value2|param3:value3`

The `m` decides this meta object will be a map.

Usage:

```java
public static void main(String[] args) {
  String input = "Some unrelated text.\n" +
                 "$\n" +
                 "<l mobs Ferocious Tiger|Dreadful Devil\n" +
                 "<m flags fighting_allowed:true|terrain_type:forest";
  Wordplay wordplay = new WordplayImpl();
  WordplayOutput output = wordplay.process(input);
  System.out.println(output.getText());
  // Produces: Some unrelated text.
  List<MetaObject> metaObjects = output.getMetaObjects();
  MetaList ml = (MetaList) metaObjects.get(0);
  System.out.println(ml.getId());                     // mobs
  System.out.println(ml.getData().get(0));            // Ferocious Tiger
  System.out.println(ml.getData().get(1));            // Dreadful Devil
  MetaMap mp = (MetaMap) metaObjects.get(1);
  System.out.println(mp.getId());                     // flags
  System.out.println(mp.getBool("fighting_allowed")); // true
  System.out.println(mp.getString("terrain_type"));   // forest
}
```
