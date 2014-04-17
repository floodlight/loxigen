LOCITEST_SRCS := $(wildcard src/*.c)
LOCI_SRCS := $(wildcard ../loci/src/*.c)

LOCITEST_OBJS := $(LOCITEST_SRCS:.c=.o)
LOCI_OBJS := $(LOCI_SRCS:.c=.o)

CFLAGS := -Wall -Werror -g -Os
CFLAGS += -Iinc -I../loci/inc -I ../loci/src

all: locitest

locitest: $(LOCITEST_OBJS) loci.a
	$(CC) -Wl,--whole-archive $^ -Wl,--no-whole-archive -o $@

loci.a: $(LOCI_OBJS)
	ar rc $@ $^

clean:
	rm -f locitest loci.a $(LOCITEST_OBJS) $(LOCI_OBJS)

# BSN build system magic
MODULE := locitest
