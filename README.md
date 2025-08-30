# krepo

`krepo` is a maven repository server, built with Kotlin Native, it supports `linux amd64` and `linux arm64` only,
15MB ~ 20MB was used after running for 12h.

# Features

1. Low memory usage
2. High performance
3. Support mirror other repositories

# Build and deploy backend

NOTE: If you want to use it quickly just download the executable binary file in the releases.

## Build binary

```shell
$ ./init.sh && ./gradlew replaceDef generateResources linkReleaseExecutableLinuxX64
```

This might take a long time to build the binary file

> If you want to build linux arm64 executable, run the following command

```shell
$ ./gradlew replaceDef generateResources linkReleaseExecutableLinuxArm64
```

## Deploy

Just run the executable file.

## Configure backend

Open the `config.json` change the `frontend` value to your own frontend url.
(This setting is only for `/listing` route to redirect the user to the frontend to list repository content)

## Build and deploy frontend

NOTE: Frontend is optional, without frontend backend can also work

Before you build the frontend please modify the config.json at `kmvn-frontend/src/jsMain/resources/config.json`
and change the backend url to your own backend address.

```shell
$ ./gradlew jsBrowserDistribution
```

Then you can find the static files at `kmvn-frontend/build/dist/js/productionExecutable`

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
