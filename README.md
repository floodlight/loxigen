Introduction
============

LoxiGen is a tool that generates OpenFlow protocol libraries for a number of
languages. It is composed of a frontend that parses wire protocol descriptions
and a backend for each supported language (currently C and Python, with Java on
the way).

Prerequisites
=============

Running the unit tests requires [nosetests](http://nose.readthedocs.org/en/latest/).
You can install it via easy_install,
```
easy_install nose
```
pip,
```
pip install nose
```
or via your distribution's package manager (example for Debian/Ubuntu):
```
sudo apt-get install python-nose
```

Nosetests is only required for running the unit tests, not for running loxigen
itself. We do ask you to install it and use it before submitting pull requests,
though.

Usage
=====

You can run LoxiGen directly from the repository. There's no need to install it,
and it has no dependencies beyond Python 2.7+.

To generate the libraries for all languages:

```
make
```

To generate the library for a single language:

```
make c
```

The currently supported languages are `c` and `python`.

The generated libraries will be under the `loxi_output` directory. This can be
changed with the `LOXI_OUTPUT_DIR` environment variable when using the Makefile.

Each generated library comes with its own set of documentation in the standard
format for that language. Please see that documentation for more details on
using the generated libraries.

Contributing
============

Please fork the repository on GitHub and send us a pull request. You might also
be interested in the INTERNALS file which has notes about how LoxiGen works.

Loxigen comes with a set of internal unit-tests, as well as with a set of tests
for the generated artifacts. Be sure to run

```
make check-all
```

and correct any problems before submitting a pull request.
