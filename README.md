[![test.ai sdk logo](https://testdotai.github.io/static-assets/logo-sdk.png)](https://adoptium.net)

[![JDK-11+](https://img.shields.io/badge/JDK-11%2B-blue)](https://adoptium.net)
[![Apache 2.0](https://img.shields.io/badge/Apache-2.0-blue)](https://www.apache.org/licenses/LICENSE-2.0)
[![javadoc](https://javadoc.io/badge2/ai.test.sdk/test-ai-selenium/javadoc.svg)](https://javadoc.io/doc/ai.test.sdk/test-ai-selenium)
[![Discord](https://img.shields.io/discord/853669216880295946?&logo=discord)](https://sdk.test.ai/discord)

The test.ai selenium SDK is a simple library that makes it easy to write robust cross-browser web tests backed by computer vision and artificial intelligence.

test.ai integrates seamelessly with your existing tests, and will act as backup if your selectors break/fail by attempting to visually (computer vision) identify elements.

The test.ai SDK is able to accomplish this by automatically ingesting your selenium elements (using both screenshots and element names) when you run your test cases with test.ai for the first time. 

The SDK is accompanied by a [web-based editor](https://sdk.test.ai/) which makes building visual test cases easy; you can draw boxes around your elements instead of using fragile CSS or XPath selectors.

## Install

Add the following line(s) to the dependencies section in your

**pom.xml (Maven)**
```xml
<dependency>
  <groupId>ai.test.sdk</groupId>
  <artifactId>test-ai-selenium</artifactId>
  <version>0.0.2</version>
</dependency>
````

**build.gradle (Gradle)**
```groovy
implementation 'ai.test.sdk:test-ai-selenium:0.0.2'
```

## Tutorial
We have a detailed step-by-step tutorial which will help you get set up with the SDK: https://github.com/testdotai/java-selenium-sdk-demo

## Resources
* [Register/Login to your test.ai account](https://sdk.test.ai/login)
* [API Docs](https://www.javadoc.io/doc/ai.test.sdk/test-ai-selenium)
* [Another Tutorial](https://sdk.test.ai/tutorial)