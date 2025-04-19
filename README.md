# KMVNRepo

This project is a Maven repository application, but made with Kotlin Native,
with low memory usage(Running for 12h only 10Mb usage).
It does not have a "Regular" frontend, instead, the frontend module
is just a file listing page.

I have already used it to store my artifacts, See backend https://repo.maven.rtast.cn and
frontend https://pkg.rtast.cn/#/releases/

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


# Stacks

- Kotlin CInterop -> To get the modification timestamp of file or directory
