#!/usr/bin/python

import sys
import re

class ImportLine:
    def __init__(self, line):
        self.line = line
        class_name = None
        if line[len(line) - 1] == '*':
            class_name = '*'
        else:
            i = 7
            while i < len(line) - 1:
                if re.match('\.[A-Z][\..]*$', line[i - 1 : len(line) - 1]):
                    class_name = line[i : len(line) - 1]
                    break
                i = i + 1
            if class_name is None:
                class_name = line[line.rfind('.') + 1 : len(line) - 1]
        self.class_name = class_name


class ImportCleaner:
    def __init__(self, path):
        f = open(path)
        self.imp_lines = []
        self.code_lines = []
        self.imports_first_line = -1
        i = 0
        for line in f:
            if len(line) > 6 and re.match('^[ \t]*import ', line):
                self.imp_lines.append(ImportLine(line.rstrip()))
                if self.imports_first_line == -1:
                    self.imports_first_line = i
            else:
                self.code_lines.append(line.rstrip())
            i = i + 1
        f.close()

    def find_used_imports(self):
        self.used_imports = []
        for line in self.code_lines:
            temp = []
            for imp in self.imp_lines:
                if imp.class_name == '*' or line.find(imp.class_name) > -1:
                    temp.append(imp)
            for x in temp:
                self.imp_lines.remove(x)
                self.used_imports.append(x)

    def rewrite_file(self, path):
        f = open(path, 'w')
        imports_written = False
        for i in range(len(self.code_lines)):
            if not imports_written and self.imports_first_line == i:
                # Put all imports
                for imp in self.used_imports:
                    f.write(imp.line + '\n')
                imports_written = True
            # Put next code line
            f.write(self.code_lines[i] + '\n')
        f.close()

def main(argv):
    if len(argv) != 2:
        print 'Usage: ImportCleaner <java file>'
        return

    filename = argv[1]
    print 'Cleaning imports from file %s' % (filename)
    cleaner = ImportCleaner(filename)
    cleaner.find_used_imports()
    cleaner.rewrite_file(filename)

if __name__ == '__main__':
    main(sys.argv)
