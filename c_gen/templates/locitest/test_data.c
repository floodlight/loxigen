/* Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior University */
/* Copyright (c) 2011, 2012 Open Networking Foundation */
/* Copyright (c) 2012, 2013 Big Switch Networks, Inc. */
/* See the file LICENSE.loci which should have been included in the source distribution */

/**
 *
 * AUTOMATICALLY GENERATED FILE.  Edits will be lost on regen.
 *
 * Data file tests for all versions.
 */

#include <locitest/test_common.h>

<?py
def hexarray(data, indent):
    i = 0
    text = []
    text.append(" " * indent)
    for byte in data:
        text.append("0x%02x, " % ord(byte))
        i += 1
        if i == 8:
            text.append("\n" + " " * indent)
            i = 0
        #endif
    #endfor
    return "".join(text)
#end
?>

static void
hexdump(const uint8_t *data, int len)
{
    int i = 0, j;
    while (i < len) {
	printf("%02x: ", i);
	for (j = 0; j < 8 && i < len; j++, i++) {
	    printf("%02x ", data[i]);
	}
	printf("\n");
    }
}

static void
show_failure(const uint8_t *a, int a_len, const uint8_t *b, int b_len)
{
    printf("\n--- Expected: (len=%d)\n", a_len);
    hexdump(a, a_len);
    printf("\n--- Actual: (len=%d)\n", b_len);
    hexdump(b, b_len);
}

:: for test in tests:
/* Generated from ${test['filename']} */
static int
test_${test['name']}(void) {
    uint8_t binary[] = {
${hexarray(test['binary'], indent=8)}
    };

    of_object_t *obj;

${'\n'.join([' ' * 4 + x for x in test['c'].split("\n")])}

    if (sizeof(binary) != WBUF_CURRENT_BYTES(OF_OBJECT_TO_WBUF(obj))
        || memcmp(binary, WBUF_BUF(OF_OBJECT_TO_WBUF(obj)), sizeof(binary))) {
	show_failure(binary, sizeof(binary),
		     WBUF_BUF(OF_OBJECT_TO_WBUF(obj)),
		     WBUF_CURRENT_BYTES(OF_OBJECT_TO_WBUF(obj)));
	of_object_delete(obj);
	return TEST_FAIL;
    }

    of_object_delete(obj);
    return TEST_PASS;
}

:: #endfor

int
test_datafiles(void)
{
:: for test in tests:
    RUN_TEST(${test['name']});
:: #endfor
    return TEST_PASS;
}
