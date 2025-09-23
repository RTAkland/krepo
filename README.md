# krepo

`krepo` is a maven repository server, built with Kotlin Native, it supports `linux amd64` and `linux arm64`.
Memory usage is around 15~20 MB after running continuously for 12 hours.

# Features

1. Low memory usage
2. High performance
3. Support mirroring other repositories

# Build and deploy backend

## Build binary

```shell
$ ./init.sh && ./gradlew replaceDef generateResources linkReleaseExecutableLinuxX64
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

# Special thanks

<div>

<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.png" alt="JetBrainsIcon" width="128">

<a href="https://www.jetbrains.com/opensource/"><code>JetBrains Open Source</code></a> provided the powerful IDE support

</div>
