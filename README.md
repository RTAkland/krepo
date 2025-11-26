# krepo

`krepo` is a maven repository server, built with Kotlin Native, it supports `linux amd64` and `linux arm64`.
Memory usage is around 15~20 MB after running continuously for 12 hours.

# Features

1. Low memory usage
2. High performance
3. Mirroring repositories
4. Etag and last-modified header verification
5. Staged upload

# Build and deploy backend

## Build binary

> Copy the following commands and execute it line by line(Important!!!)

```shell
$ ./init.sh
$ ./gradlew replaceDef jsBrowserDistribution
$ ./gradlew generateResources linkReleaseExecutableLinuxX64
```

This might take a long time to build the binary file

> If you want to build linux arm64 executable, run the following command

```shell
$ ./gradlew replaceDef generateResources linkReleaseExecutableLinuxArm64
```

## Run krepo

> At the first startup krepo will generate the web static resources into disk and exit quickly.

# Licenses

- This project is open source under [Apache-2.0](./LICENSE) license, that is:
    - You can directly use the functions provided by this project without any authorization
    - You can distribute, modify and derive the source code at will under the condition of **indicating the source
      copyright information**
