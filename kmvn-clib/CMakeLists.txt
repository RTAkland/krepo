cmake_minimum_required(VERSION 3.31)
project(time C)

set(CMAKE_C_STANDARD 11)

add_library(kmvn_ext_api STATIC
        file_time.c
        file_time.h
        disk_usage.c
        disk_usage.h
)
