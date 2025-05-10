#include <sys/stat.h>
#include <time.h>
#include "file_time.h"
#ifdef _WIN32
  #ifndef S_ISDIR
    #define S_ISDIR(mode)  (((mode) & S_IFMT) == S_IFDIR)
  #endif
  #ifndef S_ISREG
    #define S_ISREG(mode)  (((mode) & S_IFMT) == S_IFREG)
  #endif
#endif

long get_file_modified_time(const char *file_path) {
    struct stat attrib;
    if (stat(file_path, &attrib) == 0) {
        return attrib.st_mtime;
    }
    return -1;
}

const char *get_file_modified_time_str(const char *file_path) {
    struct stat attrib;
    static char buffer[30];
    if (stat(file_path, &attrib) == 0) {
        const struct tm *tm_info = localtime(&attrib.st_mtime);
        strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", tm_info);
        return buffer;
    }
    return NULL;
}

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
