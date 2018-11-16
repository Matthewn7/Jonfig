# Jonfig
Basic JSON configuration parser for Java

## Usage

* Create a JSON config file

```json
{
  "test": "Hello, World!"
}
```

* Create an interface to represent the config file

```java
public interface Config {

    String getTest();
}
```

* Load the JSON file

```java
Jonfig jonfig = new Jonfig(directory, this.getClass(), logger);

Config config = jonfig.load("/config.json", json -> {
    JSONObject jsonObject = (JSONObject) json;

    // return new Test()...
    return () -> (String) jsonObject.get("test");
});

System.out.println(config.getTest());
```
