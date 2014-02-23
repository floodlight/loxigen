:: # Copyright 2013, Big Switch Networks, Inc.
:: #
:: # LoxiGen is licensed under the Eclipse Public License, version 1.0 (EPL), with
:: # the following special exception:
:: #
:: # LOXI Exception
:: #
:: # As a special exception to the terms of the EPL, you may distribute libraries
:: # generated by LoxiGen (LoxiGen Libraries) under the terms of your choice, provided
:: # that copyright and licensing notices generated by LoxiGen are not altered or removed
:: # from the LoxiGen Libraries and the notice provided below is (i) included in
:: # the LoxiGen Libraries, if distributed in source code form and (ii) included in any
:: # documentation for the LoxiGen Libraries, if distributed in binary form.
:: #
:: # Notice: "Copyright 2013, Big Switch Networks, Inc. This library was generated by the LoxiGen Compiler."
:: #
:: # You may not use this file except in compliance with the EPL or LOXI Exception. You may obtain
:: # a copy of the EPL at:
:: #
:: # http://www.eclipse.org/legal/epl-v10.html
:: #
:: # Unless required by applicable law or agreed to in writing, software
:: # distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
:: # WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
:: # EPL for the specific language governing permissions and limitations
:: # under the EPL.
::
:: include('_copyright.c')

/*
 * @fixme THIS FILE NEEDS CLEANUP.  It may just go away.
 * 
 * Low level internal header file.  Defines inheritance mechanism for
 * LOCI objects.  In general, the routines in this file should not be
 * called directly.  Rather the class-specific operations should be 
 * used from loci.h.
 *
 * TREAT THESE FUNCTIONS AS PRIVATE.  THEY ARE GENERALLY HELPER
 * FUNCTIONS FOR LOCI TYPE SPECIFIC IMPLEMENTATIONS
 */

#if !defined(_OF_OBJECT_H_)
#define _OF_OBJECT_H_

#include <loci/of_buffer.h>
#include <loci/of_match.h>
#include <loci/loci_base.h>
#include <loci/of_message.h>
#include <loci/of_wire_buf.h>

/**
 * This is the number of bytes reserved for metadata in each
 * of_object_t instance.
 */
#define OF_OBJECT_METADATA_BYTES 32

/*
 * Generic accessors:
 *
 * Many objects have a length represented in the wire buffer
 * wire_length_get and wire_length_set access these values directly on the
 * wire.
 *
 * Many objects have a length represented in the wire buffer
 * wire_length_get and wire_length_set access these values directly on the
 * wire.
 *
 * FIXME: TBD if wire_length_set and wire_type_set are required.
 */
typedef void (*of_wire_length_get_f)(of_object_t *obj, int *bytes);
typedef void (*of_wire_length_set_f)(of_object_t *obj, int bytes);
typedef void (*of_wire_type_get_f)(of_object_t *obj, of_object_id_t *id);
typedef void (*of_wire_type_set_f)(of_object_t *obj);

/****************************************************************
 * General list operations: first, next, append_setup, append_advance
 ****************************************************************/

/* General list first operation */
extern int of_list_first(of_object_t *parent, of_object_t *child);

/* General list next operation */
extern int of_list_next(of_object_t *parent, of_object_t *child);

/* General list append bind operation */
extern int of_list_append_bind(of_object_t *parent, of_object_t *child);

/* Append a copy of item to list */
extern int of_list_append(of_object_t *list, of_object_t *item);

extern of_object_t *of_object_new(int bytes);
extern of_object_t *of_object_dup(of_object_t *src);

/**
 * Callback function prototype for deleting an object
 */
typedef void (*of_object_delete_callback_f)(of_object_t *obj);

typedef struct of_object_track_info_s {
    of_object_delete_callback_f delete_cb;  /* To be implemented */
    void *delete_cookie;
} of_object_track_info_t;

extern int of_object_xid_set(of_object_t *obj, uint32_t xid);
extern int of_object_xid_get(of_object_t *obj, uint32_t *xid);

/* Bind a buffer to an object, usually for parsing the buffer */
extern int of_object_buffer_bind(of_object_t *obj, uint8_t *buf, 
                                 int bytes, of_buffer_free_f buf_free);


/**
 * Steal a wire buffer from an object.
 * @param obj The object whose buffer is being removed
 * @param buffer[out] A handle for the pointer to the uint8_t * returned
 *
 * The wire buffer is taken from the object and its wirebuffer is set to
 * NULL.  The ref_count of the wire buffer is not changed.
 */
extern void of_object_wire_buffer_steal(of_object_t *obj, uint8_t **buffer);
extern int of_object_append_buffer(of_object_t *dst, of_object_t *src);

extern of_object_t *of_object_new_from_message(of_message_t msg, int len);

typedef struct of_object_storage_s of_object_storage_t;

of_object_t *of_object_new_from_message_preallocated(
    of_object_storage_t *storage, uint8_t *buf, int len);

/* Delete an OpenFlow object without reference to its type */
extern void of_object_delete(of_object_t *obj);

int of_object_can_grow(of_object_t *obj, int new_len);

void of_object_parent_length_update(of_object_t *obj, int delta);

struct of_object_s {
    /* The control block for the underlying data buffer */
    of_wire_object_t wire_object;
    /* The LOCI type enum value of the object */
    of_object_id_t object_id;

    /*
     * Objects need to track their "parent" so that updates to the
     * object that affect its length can be pushed to the parent.
     * Treat as private.
     */
    of_object_t *parent;

    /*
     * Not all objects have length and version on the wire so we keep
     * them here.  NOTE: Infrastructure manages length and version.
     * Treat length as private and version as read only.
     */
    int length;
    of_version_t version;

    /*
     * Many objects have a length and/or type represented in the wire buffer
     * These accessors get and set those value when present.  Treat as private.
     */
    of_wire_length_get_f wire_length_get;
    of_wire_length_set_f wire_length_set;
    of_wire_type_get_f wire_type_get;
    of_wire_type_set_f wire_type_set;

    of_object_track_info_t track_info;

    /*
     * Metadata available for applications.  Ensure 8-byte alignment, but
     * that buffer is at least as large as requested.  This data is not used
     * or inspected by LOCI.
     */
    uint64_t metadata[(OF_OBJECT_METADATA_BYTES + 7) / 8];
};

struct of_object_storage_s {
    of_object_t obj;
    of_wire_buffer_t wbuf;
};

#endif /* _OF_OBJECT_H_ */
