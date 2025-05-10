#ifndef FILE_TIME_H
#define FILE_TIME_H

#ifdef __cplusplus
extern "C" {
#endif

long get_file_modified_time(const char *file_path);

const char *get_file_modified_time_str(const char *file_path);

int is_directory(const char *path);

int is_regular_file(const char *path);
#ifdef __cplusplus
}
#endif

#endif // FILE_TIME_H
