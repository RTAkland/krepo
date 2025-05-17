#include "disk_usage.h"

#ifdef _WIN32

#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct PathNode {
    char path[MAX_PATH];
    struct PathNode *next;
} PathNode;

static void push(PathNode **stack, const char *path) {
    PathNode *node = malloc(sizeof(PathNode));
    if (!node) return;
    strncpy(node->path, path, MAX_PATH - 1);
    node->path[MAX_PATH - 1] = '\0';
    node->next = *stack;
    *stack = node;
}

static char *pop(PathNode **stack) {
    if (!*stack) return NULL;
    PathNode *node = *stack;
    *stack = node->next;
    char *result = _strdup(node->path);
    free(node);
    return result;
}

static long long get_file_disk_size_win(const char *utf8_path) {
    const int wlen = MultiByteToWideChar(CP_UTF8, 0, utf8_path, -1, NULL, 0);
    if (wlen <= 0) return -1;

    wchar_t *wpath = (wchar_t *) malloc(wlen * sizeof(wchar_t));
    if (!wpath) return -1;

    MultiByteToWideChar(CP_UTF8, 0, utf8_path, -1, wpath, wlen);

    DWORD high;
    const DWORD low = GetCompressedFileSizeW(wpath, &high);
    free(wpath);

    if (low == INVALID_FILE_SIZE && GetLastError() != NO_ERROR) return -1;
    return ((long long) high << 32) | low;
}

long long get_disk_usage(const char *root_path) {
    PathNode *stack = NULL;
    push(&stack, root_path);
    long long total = 0;
    while (stack) {
        char *path = pop(&stack);
        if (!path) continue;

        const DWORD attrs = GetFileAttributesA(path);
        if (attrs == INVALID_FILE_ATTRIBUTES) {
            free(path);
            continue;
        }

        long long size = get_file_disk_size_win(path);
        if (size >= 0) total += size;

        if (attrs & FILE_ATTRIBUTE_DIRECTORY) {
            char search_path[MAX_PATH];
            snprintf(search_path, MAX_PATH, "%s\\*", path);
            WIN32_FIND_DATAA find_data;
            const HANDLE hFind = FindFirstFileA(search_path, &find_data);
            if (hFind == INVALID_HANDLE_VALUE) {
                free(path);
                continue;
            }

            do {
                if (strcmp(find_data.cFileName, ".") == 0 || strcmp(find_data.cFileName, "..") == 0)
                    continue;

                char full_path[MAX_PATH];
                snprintf(full_path, MAX_PATH, "%s\\%s", path, find_data.cFileName);
                push(&stack, full_path);
            } while (FindNextFileA(hFind, &find_data));

            FindClose(hFind);
        }

        free(path);
    }
    return total;
}

#else // POSIX

#include <sys/stat.h>
#include <dirent.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <limits.h>

typedef struct PathNode {
    char path[PATH_MAX];
    struct PathNode *next;
} PathNode;

static void push(PathNode **stack, const char *path) {
    PathNode *node = malloc(sizeof(PathNode));
    if (!node) return;
    strncpy(node->path, path, PATH_MAX - 1);
    node->path[PATH_MAX - 1] = '\0';
    node->next = *stack;
    *stack = node;
}

static char *pop(PathNode **stack) {
    if (!*stack) return NULL;
    PathNode *node = *stack;
    *stack = node->next;
    char *result = strdup(node->path);
    free(node);
    return result;
}

long long get_disk_usage(const char *root_path) {
    PathNode *stack = NULL;
    push(&stack, root_path);
    long long total = 0;
    while (stack) {
        char *path = pop(&stack);
        if (!path) continue;

        struct stat st;
        if (lstat(path, &st) != 0) {
            free(path);
            continue;
        }

        total += st.st_blocks * 512LL;

        if (S_ISDIR(st.st_mode)) {
            DIR *dir = opendir(path);
            if (!dir) {
                free(path);
                continue;
            }

            struct dirent *entry;
            while ((entry = readdir(dir)) != NULL) {
                if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0) continue;

                char fullpath[PATH_MAX];
                snprintf(fullpath, sizeof(fullpath), "%s/%s", path, entry->d_name);
                push(&stack, fullpath);
            }

            closedir(dir);
        }

        free(path);
    }
    return total;
}

#endif

//
// int main(int argc, char *argv[]) {
//     const char *path = argv[1];
//     const long long usage = get_disk_usage(path);
//     printf("Disk usage of %s: %lld bytes\n", path, usage);
//     return 0;
// }