# krepo

krepo is a maven repository server software, it has many parts, include `frontend` and `backend` implemented in
different
languages and on different platforms.

[krepo(Kotlin native)](krepo-backend) runs on `Linux amd64`, `Linux Arm64` and `Macos64`.
***Low memory usage, 25MiB+-***

[krepo(Kotlin JVM)](krepo-azure-functions/server) runs on `Azure Functions` only. ***It's serverless***

[krepo(Rust)](krepo-rs) runs on any platform that Rust can compile for. ***Publish and Download only***

# Features

1. Low memory usage
2. High performance
3. Mirroring repositories
4. Etag and last-modified header verification(native only)
5. Staged upload

# Note

> Init the project first by executing `./init.sh`

# Build frontend

```shell
$ ./gradlew :krepo-frontend:jsBrowserDistribution
```

> To deploy on Cloudflare Workers, the static files at `krepo-frontend/build/worker-dist/`.  
> To Deploy on Vercel, the static files at `krepo-frontend/build/vercel-dist/`.

# Build backend

## Azure functions

> Before build functions, rename `users.template.json` to `users.json` in
`krepo-azure-functions/server/src/main/resources/`,
> and edit the users.

> Rename `local.settings.template.json` to "local.settings.json" in `krepo-azure-functions/server/` if you want to run
> functions locally.

```shell
$ ./gradlew :krepo-azure-functions:server:azureFunctionsPackageZip
```

Function zip file at `krepo-azure-functions/server/build/azure-functions/krepo-server.zip`

## Kotlin Native platforms

```shell
# For Linux amd64
$ ./gradlew :krepo-frontend:jsBrowserDistribution generateResources :krepo-backend:linkReleaseExecutableLinuxX64

# For Linux arm64
$ ./gradlew :krepo-frontend:jsBrowserDistribution generateResources :krepo-backend:linkReleaseExecutableLinuxArm64

# For Macos64
$ ./gradlew :krepo-frontend:jsBrowserDistribution generateResources :krepo-backend:linkReleaseExecutablMacosX64
```

## Rust platforms

See [Rust README](krepo-rs/README.md)

- This project is open source under [Apache-2.0](./LICENSE) license, that is:
    - You can directly use the functions provided by this project without any authorization
    - You can distribute, modify and derive the source code at will under the condition of **indicating the source
      copyright information**
