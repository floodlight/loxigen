# Copyright 2013, Big Switch Networks, Inc.
#
# LoxiGen is licensed under the Eclipse Public License, version 1.0 (EPL), with
# the following special exception:
#
# LOXI Exception
#
# As a special exception to the terms of the EPL, you may distribute libraries
# generated by LoxiGen (LoxiGen Libraries) under the terms of your choice, provided
# that copyright and licensing notices generated by LoxiGen are not altered or removed
# from the LoxiGen Libraries and the notice provided below is (i) included in
# the LoxiGen Libraries, if distributed in source code form and (ii) included in any
# documentation for the LoxiGen Libraries, if distributed in binary form.
#
# Notice: "Copyright 2013, Big Switch Networks, Inc. This library was generated by the LoxiGen Compiler."
#
# You may not use this file except in compliance with the EPL or LOXI Exception. You may obtain
# a copy of the EPL at:
#
# http://www.eclipse.org/legal/epl-v10.html
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# EPL for the specific language governing permissions and limitations
# under the EPL.

# Available targets: all, c, python, clean

# This Makefile is just for convenience. Users that need to pass additional
# options to loxigen.py are encouraged to run it directly.

# Where to put the generated code.
LOXI_OUTPUT_DIR = loxi_output

# Generated files depend on all Loxi code and input files
LOXI_PY_FILES=$(shell find \( -name loxi_output -prune \
                             -o -name templates -prune \
                             -o -name tests -prune \
                             -o -true \
                           \) -a -name '*.py')
LOXI_TEMPLATE_FILES=$(shell find */templates -type f -a \
                                 \! \( -name '*.cache' -o -name '.*' \))
INPUT_FILES = $(wildcard openflow_input/*)

all: c python

c: .loxi_ts.c

.loxi_ts.c: ${LOXI_PY_FILES} ${LOXI_TEMPLATE_FILES} ${INPUT_FILES}
	./loxigen.py --install-dir=${LOXI_OUTPUT_DIR} --lang=c
	touch $@

python: .loxi_ts.python

.loxi_ts.python: ${LOXI_PY_FILES} ${LOXI_TEMPLATE_FILES} ${INPUT_FILES}
	./loxigen.py --install-dir=${LOXI_OUTPUT_DIR} --lang=python
	touch $@

python-doc: python
	rm -rf ${LOXI_OUTPUT_DIR}/pyloxi-doc
	mkdir -p ${LOXI_OUTPUT_DIR}/pyloxi-doc
	cp -a py_gen/sphinx ${LOXI_OUTPUT_DIR}/pyloxi-doc/input
	PYTHONPATH=${LOXI_OUTPUT_DIR}/pyloxi sphinx-apidoc -o ${LOXI_OUTPUT_DIR}/pyloxi-doc/input ${LOXI_OUTPUT_DIR}/pyloxi
	sphinx-build ${LOXI_OUTPUT_DIR}/pyloxi-doc/input ${LOXI_OUTPUT_DIR}/pyloxi-doc
	rm -rf ${LOXI_OUTPUT_DIR}/pyloxi-doc/input
	@echo "HTML documentation output to ${LOXI_OUTPUT_DIR}/pyloxi-doc"

java: .loxi_ts.java

.loxi_ts.java: ${LOXI_JAVA_FILES} ${LOXI_TEMPLATE_FILES} ${INPUT_FILES}
	./loxigen.py --install-dir=${LOXI_OUTPUT_DIR} --lang=java
	touch $@


clean:
	rm -rf loxi_output # only delete generated files in the default directory
	rm -f loxigen.log loxigen-test.log .loxi_ts.c .loxi_ts.python

debug:
	@echo "LOXI_OUTPUT_DIR=\"${LOXI_OUTPUT_DIR}\""
	@echo
	@echo "LOXI_PY_FILES=\"${LOXI_PY_FILES}\""
	@echo
	@echo "LOXI_TEMPLATE_FILES=\"${LOXI_TEMPLATE_FILES}\""
	@echo
	@echo "INPUT_FILES=\"${INPUT_FILES}\""

check:
	PYTHONPATH=. ./utest/test_parser.py
	PYTHONPATH=. ./utest/test_frontend.py
	PYTHONPATH=. ./utest/test_test_data.py

check-py: python
	PYTHONPATH=${LOXI_OUTPUT_DIR}/pyloxi:. python py_gen/tests/generic_util.py
	PYTHONPATH=${LOXI_OUTPUT_DIR}/pyloxi:. python py_gen/tests/of10.py
	PYTHONPATH=${LOXI_OUTPUT_DIR}/pyloxi:. python py_gen/tests/of11.py
	PYTHONPATH=${LOXI_OUTPUT_DIR}/pyloxi:. python py_gen/tests/of12.py
	PYTHONPATH=${LOXI_OUTPUT_DIR}/pyloxi:. python py_gen/tests/of13.py

CTEST_EXEC = ${LOXI_OUTPUT_DIR}/locitest/locitest
CTEST_SOURCE = ${LOXI_OUTPUT_DIR}/locitest/src/*.c
CTEST_SOURCE += ${LOXI_OUTPUT_DIR}/loci/src/*.c
CTEST_INC = -I ${LOXI_OUTPUT_DIR}/loci/inc
CTEST_INC += -I ${LOXI_OUTPUT_DIR}/locitest/inc
CTEST_INC += -I ${LOXI_OUTPUT_DIR}/loci/src
CTEST_CFLAGS = -Wall -Werror -g

check-c: c
	gcc ${CTEST_CFLAGS} -o ${CTEST_EXEC} ${CTEST_SOURCE} ${CTEST_INC}
	${CTEST_EXEC}

pylint:
	pylint -E ${LOXI_PY_FILES}

.PHONY: all clean debug check pylint c python
