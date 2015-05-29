# Wix Restaurants Availability
This library defines a model for weekly availability schedules (possibly with exceptions), and utility classes to quickly check for actual availability.

It's used extensively by the [Wix Restaurants Java SDK](https://github.com/wix/wix-restaurants-java-sdk) to handle opening times, delivery times, menus and dishes availability, etc.

Examples:
* Available Mon-Fri 10:00-22:00, Sat 11:00-22:00
* Available 24/7, except between January 3rd 2009 18:15 and January 5th 2009 16:00

A JavaScript version of this library is available [here](https://github.com/wix/availability4js).

## Usage
TODO

## Installation
### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.wix.restaurants</groupId>
  <artifactId>wix-restaurants-availability-utils</artifactId>
  <version>1.2.1</version>
</dependency>
```

## Reporting Issues

Please use [the issue tracker](https://github.com/wix/wix-restaurants-availability/issues) to report issues related to this library.

## License
This library uses the Apache License, version 2.0.
