# CauldronRecipesMod

## Features
Cauldron recipes can be added with a datapack.

Recipes can also be added with the mod's datagen.

## Datagen
The mod's datagen can be used just like the advancements datagen, so look into that to use it.

## How to implement as a dependency

In your settings.gradle, add...
```groovy
pluginManagement {
    repositories {
        mavenCentral()
    }
}

```

And then in your build.gradle...
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}
dependencies {
     modImplementation 'com.github.QuackImpala7321:CauldronRecipesMod:tag'
}
```