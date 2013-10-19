SRCS := $(wildcard src/*.c)
SRCS += $(wildcard ../loci/src/*.c)

OBJS := $(SRCS:.c=.o)

CFLAGS := -Wall -Werror -g
CFLAGS += -Iinc -I../loci/inc -I ../loci/src

all: locitest

locitest: $(OBJS)
	$(CC) $^ -o $@

# BSN build system magic
MODULE := locitest
