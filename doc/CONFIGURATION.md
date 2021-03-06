# External Configuration

## API

The Microfile Configuration API allows to externalize the parameters of an application in two different ways depending on whether you have CDI available.

### If CDI is available

In this situation you can use the ConfigProperty annotation which allows you to use CDI to inject a configuration value into a CDI aware bean. To do so, you need to add the @Inject annotation, too.

Below we show the examples implemented in the class MedicalRecordNumberService:

```
    @Inject
    @ConfigProperty(name="urlMedicalRecordNumberService", defaultValue = "http://localhost:8080")
    private String urlMedicalRecordNumberService;
```

Please note that a default value can be specified.

### If CDI is NOT available

In that case you can use the static method of org.eclipse.microprofile.config.ConfigProvider, "getConfig()", which returns you an instance of org.eclipse.microprofile.config.Config and then invoke the methods "getValue" and "getOptionalValue" to retrieve configuration values.

An example to do the same as in the previous case:

```
final Config config = ConfigProvider.getConfig();
final String strUrl = config.getValue("urlMedicalRecordNumberService", String.class);
```

Please notice the second parameter, "String.class", which indicates the expected type of the property entry.

Note: The Config instance can be used if we have CDI using using injection:

```
@Inject
final Config config;
```

## Config Sources
In order to change the configuration, whether the default value or the one set in /META-INF/microprofile-config.properties, it is possible to
set the configuration as a **System Property** (key-value pair passed down to the JVM startup prefixed with -D) or as an **OS Environment Variable**.

For more details you can check out the following blog entry: https://www.tomitribe.com/blog/an-overview-of-microprofile-configuration/

