// Copyright © 2025 RTAkland
// Date: 25-5-13 上午2:19
// Open Source Under Apache-2.0 License
// https://www.apache.org/licenses/LICENSE-2.0

//
// Created by RTAkl on 2025/5/13.
//

#include "file.h"

#include <stdio.h>
#include <sys/stat.h>


int is_directory(const char *path) {
    struct stat path_stat;
    if (stat(path, &path_stat) != 0) return 0;
    return S_ISDIR(path_stat.st_mode);
}

int is_regular_file(const char *path) {
    struct stat path_stat;
    if (stat(path, &path_stat) != 0) return 0;
    return S_ISREG(path_stat.st_mode);
}

// int main(int argc, char *argv[]) {
    // printf("%d", is_regular_file("E:\\projects\\KMVNRepo\\kmvn-clib\\disk_usage.c"));
    // return 0;
// }
