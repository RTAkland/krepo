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

## Configure backend

Open `config.json` file, and change the `frontend` value to your own frontend url.
(This setting applies only to the `/listing` route so that redirect the user to the frontend to view repository content)

## Build and deploy frontend

NOTE: Frontend is optional, without frontend backend can also work

Before building the frontend please modify the config.json at `kmvn-frontend/src/jsMain/resources/config.json`
and change the backend url to your own backend url.

```shell
$ ./gradlew jsBrowserDistribution
```

You can find the web static files at `kmvn-frontend/build/dist/js/productionExecutable`

Deploy to vercel is recommended, See [.github/workflows/deploy-to-vercel.yml](.github/workflows/deploy-to-vercel.yml)

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
