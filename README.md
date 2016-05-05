Introduction
============

LoxiGen is a tool that generates OpenFlow protocol libraries for a number of
languages. It is composed of a frontend that parses wire protocol descriptions
and a backend for each supported language (currently C, Python, and Java, with an
auto-generated wireshark dissector in Lua on the way).

LoxiGen currently supports OpenFlow Versions **1.0**, **1.1**, **1.2**, and **1.3.1**, **1.4.1**, and **1.5.1**.
Versions 1.0, 1.3.1 and 1.4.1 are actively used in production. Support for versions 1.1 and 1.2 is considered experimental.
Support for **1.5.1** is currently experimental and limited to the Java backend.

Loxigen is free and open source software. The Loxigen tool itself is licensed under the [Eclipse Public
License,  version 1.0](http://www.eclipse.org/legal/epl-v10.html) (EPL), with an exception allowing for the distribution of the generated artifacts
under terms of your choice (copyright notices must be retained, see the [loxigen.py](loxigen.py)
header for details.)


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

Nosetests is only required for running the unit tests, not for running LoxiGen
itself. We do ask you to install it and use it before submitting pull requests,
though.

Running the Java unit tests requires Maven 3: `sudo apt-get install maven`.

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

The currently supported languages are `c`, `python` and `java`. There is an
experimental backend that generates a lua wireshark dissector
(`wireshark`).

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
