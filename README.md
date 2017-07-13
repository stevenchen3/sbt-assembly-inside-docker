# Reproduce `sbt assembly` with shading

Reproduce the issue with shading when run `sbt assembly` inside Docker container. Briefly, the issue
is that running `sbt assembly` inside Linux Docker container to package a Jar would encounter
`Fully-qualified classname does not match jar entry` when `shading` is enabled in `build.sbt`.

The background here is that, recently, I need to shade a library named `shapeless` in my project so
that my code can work in Spark. However, I ran into the issue described above.

Interestingly, when run `sbt assembly` on Linux or Mac OS bare meta machine, it works just fine.
I have tested on the following bare meta envoriments:

+ Mac OS macOS Sierra
+ CentOS 7
+ CentOS 7.1
+ Centos 7.3
+ Ubuntu 16.04

But it fails on the following Docker Containers:

+ CentOS 7
+ CentOS 7.1
+ Centos 7.3
+ Ubuntu 16.04

This issue can be easily reproduced by following steps below.

# Reproduce steps

* set a Docker container with `sbt` and `jdk8` installed
* `git clone` this repository to Docker container running Linux
* change directory to `sbt-assembly-shading` and run `sbt assembly`

## Error message

```shell
Fully-qualified classname does not match jar entry:
  jar entry: shapeless/$tilde$qmark$greater$?.class
  class name: shapeless/$tilde$qmark$greater$?.class
Omitting shapeless/$tilde$qmark$greater$?.class.
```
