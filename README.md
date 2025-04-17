# KMVNRepo

This project is a Maven repository application, but made with Kotlin Native,
with low memory usage(Running for 12h only 10Mb usage).
It does not have a "Regular" frontend, instead, the frontend module
is just a file listing page.

I have already used it to store my artifacts, See backend https://repo.maven.rtast.cn and
frontend https://mvnrepo.rtast.cn/#/releases/

# Build backend from source

This app only support mingwX64 or linuxX64 (Maybe you can add the target by yourself)

Running the flowing commands to build an executable binary file

```shell
$ ./gradlew :backend:linkReleaseExecutableLinuxX64  # This is for linuxX64
$ ./gradlew.bat :backend:linkReleaseExecutableMingwX64  # This is for Windows64(MingwX64)
```

Then you can find the binary in the `./backend/build/bin/*X64/releaseExecutable/` folder.

# Running the backend

On windows just double-click the exe the server will run on 9098 port by default.

On Linux make sure you have already made it executable.

# Build frontend from source

```shell
$ ./gradlew :frontend:jsBrowserDevelopmentExecutableDistribution
```

The dist static files can be found at `./frontend/build/dist/js/developmentExecutable/`

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