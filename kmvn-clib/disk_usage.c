// Copyright © 2025 RTAkland
// Date: 25-5-12 上午1:17
// Open Source Under Apache-2.0 License
// https://www.apache.org/licenses/LICENSE-2.0

//
// Created by RTAkl on 2025/5/12.
//

#include "disk_usage.h"

#ifdef _WIN32

#include <windows.h>
#include <stdio.h>

static long long get_file_disk_size_win(const char *utf8_path) {
    int wlen = MultiByteToWideChar(CP_UTF8, 0, utf8_path, -1, NULL, 0);
    if (wlen <= 0) return -1;

    wchar_t *wpath = (wchar_t *) malloc(wlen * sizeof(wchar_t));
    if (!wpath) return -1;

    MultiByteToWideChar(CP_UTF8, 0, utf8_path, -1, wpath, wlen);

    DWORD low, high;
    low = GetCompressedFileSizeW(wpath, &high);
    if (low == INVALID_FILE_SIZE && GetLastError() != NO_ERROR) {
        free(wpath);
        return -1;
    }

    free(wpath);
    return ((long long) high << 32) | low;
}

long long get_disk_usage(const char *path) {
    WIN32_FIND_DATAA find_data;
    char search_path[MAX_PATH];

    DWORD attrs = GetFileAttributesA(path);
    if (attrs == INVALID_FILE_ATTRIBUTES) return -1;

    if (!(attrs & FILE_ATTRIBUTE_DIRECTORY)) {
        return get_file_disk_size_win(path);
    }

    snprintf(search_path, MAX_PATH, "%s\\*", path);
    HANDLE hFind = FindFirstFileA(search_path, &find_data);
    if (hFind == INVALID_HANDLE_VALUE) return -1;

    long long total = get_file_disk_size_win(path); // Include the directory entry itself

    do {
        if (strcmp(find_data.cFileName, ".") == 0 || strcmp(find_data.cFileName, "..") == 0) continue;

        char full_path[MAX_PATH];
        snprintf(full_path, MAX_PATH, "%s\\%s", path, find_data.cFileName);

        long long child_size = get_disk_usage(full_path);
        if (child_size < 0) {
            FindClose(hFind);
            return -1;
        }

        total += child_size;
    } while (FindNextFileA(hFind, &find_data));

    FindClose(hFind);
    return total;
}

#else  // POSIX

#include <sys/stat.h>
#include <dirent.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>

static long long get_file_disk_size_posix(const char *path) {
    struct stat st;
    if (lstat(path, &st) != 0) return -1;
    return st.st_blocks * 512LL;
}

long long get_disk_usage(const char *path) {
    struct stat st;
    if (lstat(path, &st) != 0) return -1;

    long long total = st.st_blocks * 512LL;

    if (S_ISDIR(st.st_mode)) {
        DIR *dir = opendir(path);
        if (!dir) return -1;

        struct dirent *entry;
        while ((entry = readdir(dir)) != NULL) {
            if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) continue;

            char *fullpath;
            size_t len = snprintf(NULL, 0, "%s/%s", path, entry->d_name) + 1;
            fullpath = malloc(len);
            if (!fullpath) {
                closedir(dir);
                return -1;
            }

            snprintf(fullpath, len, "%s/%s", path, entry->d_name);

            long long child_size = get_disk_usage(fullpath);
            free(fullpath);

            if (child_size < 0) {
                closedir(dir);
                return -1;
            }

            total += child_size;
        }

        closedir(dir);
    }

    return total;
}

#endif
