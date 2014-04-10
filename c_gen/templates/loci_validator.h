/* Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior University */
/* Copyright (c) 2011, 2012 Open Networking Foundation */
/* Copyright (c) 2012, 2013 Big Switch Networks, Inc. */
/* See the file LICENSE.loci which should have been included in the source distribution */

/**
 *
 * AUTOMATICALLY GENERATED FILE.  Edits will be lost on regen.
 *
 * Declarations of message validation functions. These functions check that an
 * OpenFlow message is well formed. Specifically, they check internal length
 * fields.
 */

#if !defined(_LOCI_VALIDATOR_H_)
#define _LOCI_VALIDATOR_H_

#include <loci/loci.h>

/*
 * Validate an OpenFlow message.
 * @return 0 if message is valid, -1 otherwise.
 */
extern int of_validate_message(of_message_t msg, int len);

#endif /* _LOCI_VALIDATOR_H_ */
