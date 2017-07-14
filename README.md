# Reproduce `sbt assembly` inside Docker container

Reproduce the issue when run `sbt assembly` inside Docker container. Briefly, the issue
is that running `sbt assembly` inside Linux Docker container to package a Jar would encounter
`Fully-qualified classname does not match jar entry`.

The background here is that, recently, I need to shade a library named `shapeless` in my project so
that my code can work in Spark. However, I ran into the issue described above. After googling
around, it turns out that this is a known issue, see
[here](https://github.com/milessabin/shapeless/wiki/Shapeless-with-SBT-Assembly-inside-Docker)

This issue can be easily reproduced by following steps below.

# Reproduce steps

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

# How to fix this?

This is due to the `NAME_MAX` difference between bare metal and Docker container, and there won't
be a handy fix for this. If you really need to assemble your Jars inside containers, check out
workarounds from
[https://github.com/milessabin/shapeless/wiki/Shapeless-with-SBT-Assembly-inside-Docker](https://github.com/milessabin/shapeless/wiki/Shapeless-with-SBT-Assembly-inside-Docker)

