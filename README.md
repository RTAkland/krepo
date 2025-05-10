# krepo

A Maven repository software built with Kotlin/Native and Kotlin/JS

It supports linuxX64 and mingwX64 with low memory usage(Running for 12h only 10MB memory)

# Build backend

NOTE: Before you build the backend, you must edit the `.def` files,
and modify the file path of c headers

## LinuxX64

Run the following command:

```shell
$ chmod +x ./gradlew
$ ./gradlew :kmvn-backend:linkReleaseExecutableLinuxX64
```

The executable binary file was generated at `kmvn-backend/build/bin/linuxX64/releaseExecutable/kmvn-backend.kexe`

## Windows(MingwX64)

```shell
$ .\gradlew.bat :kmvn-backend:linkReleaseExecutableMingwX64
```

The executable binary file was generated at `kmvn-backend/build/bin/mingwX64/debugExecutable/kmvn-backend.exe`

# Build & Deploy Frontend

## Build

```shell
$ ./gradlew :kmvn-frontend:jsBrowserDistribution
```

When build success the static html and js files were generated at `kmvn-frontend/build/dist/js/productionExecutable`

## Deploy

There's two ways to deploy the frontend

1. Using nginx or any other http server to serve the static files.
2. Using PaaS platform to deploy the frontend like vercel, netlify, cloudflare pages and any other.

I deploy the frontend on the vercel you can check the workflow file to see the detail of deployment
`.github/workflows/deploy-to-vercel.yml`