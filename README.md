# krepo

krepo is a maven repository server software, it has many parts, include `frontend` and `backend` implemented in
different languages and on different platforms.

[krepo(Kotlin JVM)](krepo-azure-functions/server) runs on `Azure Functions` only. ***It's serverless*** and
***mainly supported***

[krepo(Rust)](krepo-rs) runs on any platform that Rust can compile for. ***Publish and Download only***

# Note

# Build frontend

```shell
$ ./gradlew :krepo-frontend:jsBrowserDistribution
```

# Build backend

> Before build functions, rename `users.template.json` to `users.json` in
`krepo-azure-functions/server/src/main/resources/`,
> and edit the users.

> Rename `local.settings.template.json` to "local.settings.json" in `krepo-azure-functions/` if you want to run
> functions locally.

```shell
$ ./gradlew :krepo-azure-functions:generateResources :krepo-azure-functions:azureFunctionsPackageZip
```

Function zip file at `krepo-azure-functions/build/azure-functions/krepo-server.zip`

## Rust platforms

See [Rust README](krepo-rs/README.md)

# LICENSE

- This project is open source under [Apache-2.0](./LICENSE) license, that is:
    - You can directly use the functions provided by this project without any authorization
    - You can distribute, modify and derive the source code at will under the condition of **indicating the source
      copyright information**
