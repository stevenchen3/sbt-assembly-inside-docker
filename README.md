# Reproducing `sbt assembly` inside Docker container

Reproduce the issue when run `sbt assembly` inside Docker container. Briefly, the issue
is that running `sbt assembly` inside Linux Docker container to package a Jar would encounter
`Fully-qualified classname does not match jar entry`.

The background here is that, recently, I need to shade a library named `shapeless` in my project so
that my code can work in Spark. However, I ran into the issue described above. After googling
around, it turns out that this is a known issue:

> This is not a problem for normal use of Scala dependencies which are binaries. However this is a
problem if you assemble your application JAR inside Docker using SBT Assembly. SBT Assembly explodes
all dependency jars, so the classes become local files and rolls them into one application super
jar.


See [here](https://github.com/milessabin/shapeless/wiki/Shapeless-with-SBT-Assembly-inside-Docker)
for more details.

This issue can be easily reproduced by following steps below.

# Reproducing steps

* Install Docker on your machine
* Build the Docker image

```shell
$ docker build -t sbt-assembly-centos7 .
```

* Launch a Docker container and login the container to run `sbt assembly`

```shell
$ docker run -ti sbt-assembly-centos7:latest bash
$ cd /test
$ sbt assembly
```

## Error message

```shell
Fully-qualified classname does not match jar entry:
  jar entry: shapeless/$tilde$qmark$greater$?.class
  class name: shapeless/$tilde$qmark$greater$?.class
Omitting shapeless/$tilde$qmark$greater$?.class.
```

# How to fix?

There won't be a handy fix for this for now. (1) Assemble your Jars on bare metal machine; (2) If
you really need to assemble your Jars inside containers, check out workarounds from
[https://github.com/milessabin/shapeless/wiki/Shapeless-with-SBT-Assembly-inside-Docker](https://github.com/milessabin/shapeless/wiki/Shapeless-with-SBT-Assembly-inside-Docker)

# Updates

* Done several experiments and testing, found out that afaik, this is actually not caused by `filename too long` as inside Docker container we can observe output `255` with `getconf NAME_MAX /` and can touch files with name up to 255 characters.

* Suspect there's some issue with `sbt-assembly` shading that causes it not working properly inside container.
