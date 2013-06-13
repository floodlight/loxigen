:: include('_copyright.c')

/**
 * @file test_main
 *
 * The main kickoff point for running all tests
 */

#include <locitest/unittest.h>
#include <locitest/test_common.h>

#if !defined(__APPLE__)
#include <mcheck.h>
#define MCHECK_INIT mcheck(NULL)
#else /* mcheck not available under OS X */
#define MCHECK_INIT do { } while (0)
#endif

int
main(int argc, char *argv[])
{
    MCHECK_INIT;

    RUN_TEST(ident_macros);

    TEST_ASSERT(run_unified_accessor_tests() == TEST_PASS);
    TEST_ASSERT(run_match_tests() == TEST_PASS);

    TEST_ASSERT(run_utility_tests() == TEST_PASS);

    /* These are deprecated by the unified accessor tests */
    TEST_ASSERT(run_scalar_acc_tests() == TEST_PASS);
    TEST_ASSERT(run_list_tests() == TEST_PASS);
    TEST_ASSERT(run_message_tests() == TEST_PASS);
    TEST_ASSERT(run_setup_from_add_tests() == TEST_PASS);

    TEST_ASSERT(run_validator_tests() == TEST_PASS);

    TEST_ASSERT(run_list_limits_tests() == TEST_PASS);

    RUN_TEST(ext_objs);

    return global_error;
}
