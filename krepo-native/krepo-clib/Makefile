CC = gcc
CFLAGS = -O2 -Wall -c

ifeq ($(OS),Windows_NT)
    LIB_SUFFIX = -mingw64
else ifeq ($(shell uname),Darwin)
    LIB_SUFFIX = -macosx64
else
    LIB_SUFFIX = -linuxx64
endif

SRC_FILES := $(wildcard *.c)

OBJ_FILES := $(SRC_FILES:.c=.o)

LIB_FILES := $(patsubst %.c,lib%$(LIB_SUFFIX).a,$(SRC_FILES))

all: $(LIB_FILES)

lib%$(LIB_SUFFIX).a: %.o
	ar rcs $@ $<

%.o: %.c
	$(CC) $(CFLAGS) -o $@ $<

clean:
	rm -f *.o *.a
