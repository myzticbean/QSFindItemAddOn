# Contributing to Shop Search AddOn

If you have experience in making bukkit/spigot plugins, you can contribute to the Shop Search AddOn codebase. 
Just make a fork and then make a pull request when you're done. 
Please try to follow the [Google Java Style](https://google.github.io/styleguide/javaguide.html), or just try to copy the style of code found in the class you're editing.
But, please don't increase the plugin version number.

### How to compile?

**Requirements**
1. Java 17 JDK in PATH
2. Git
3. Maven

**Compiling from source**
```shell
git clone https://github.com/myzticbean/QSFindItemAddOn.git
cd QSFindItemAddOn/
mvn clean install
```

### After making a Fork
1. Please create your own sub-branch before adding your changes.
2. Sub-branch naming convention:
   1. If it's a new feature you are implementing, name should be `feature/<your feature name>` without the `<>`. 
   2. If it's a bug you're fixing, name should be `issuefix/<issue number>` without the `<>`.
