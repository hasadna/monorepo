
# Noloan2

## Installation

Download and add these 2 jars to `app/lib/`:
- [IText 7 License Key Library](https://mvnrepository.com/artifact/com.itextpdf/itext-licensekey "IText 7 License Key Library")
- [pdfCalligraph](https://mvnrepository.com/artifact/com.itextpdf/typography "typography")

Change versions on app level `build.gradle` to fit the ones you downloaded:
```java
 dependencies {
 	...
    implementation files('lib/typography-2.0.3.jar')
    implementation files('lib/itext-licensekey-3.0.4.jar')
}
```

