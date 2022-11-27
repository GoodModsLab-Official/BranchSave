################
#   Made By:   #
#   play-go    #
#              #
################

import subprocess
import os.path
import os
import json

if os.path.exists("BranchSave.jar"):
    pass
else:
    import urllib.request
    urllib.request.urlretrieve("https://github.com/GoodModsLab-Official/BranchSave/releases/download/BranchSave1.0.3/BranchSave.jar", "BranchSave.jar")
    print("BranchSave.jar installed in your dir!")

#if subprocess.run("java", capture_output=True, shell=True).stderr != b""+ b'"java"' + b" \xad\xa5 \xef\xa2\xab\xef\xa5\xe2\xe1\xef \xa2\xad\xe3\xe2\xe0\xa5\xad\xad\xa5\xa9 \xa8\xab\xa8 \xa2\xad\xa5\xe8\xad\xa5\xa9\r\n\xaa\xae\xac\xa0\xad\xa4\xae\xa9, \xa8\xe1\xaf\xae\xab\xad\xef\xa5\xac\xae\xa9 \xaf\xe0\xae\xa3\xe0\xa0\xac\xac\xae\xa9 \xa8\xab\xa8 \xaf\xa0\xaa\xa5\xe2\xad\xeb\xac \xe4\xa0\xa9\xab\xae\xac.\r\n":
#    pass
#else:
#    raise SyntaxError("Java is not installed!")

class Branch():
    code: str = None
    def __init__(self, code) -> None:
        self.code = code
    def compile(self):
        file = open("compil.brch", "w")
        file.write(self.code)
        file.close()
        code = subprocess.run(["java", "-jar", "BranchSave.jar"], capture_output=True, shell=True, input=b"brch -f compil.brch")
        a = code.stdout.split(b"\n")
        try:
            del [a[0], a[0], a[0], a[1]]
            out = b"".join(a)
            return (json.loads(out.replace(b"\r", b"").replace(b"Branch.panel: ", b"").replace(b">> Error: java.util.NoSuchElementException: No line found", b"").decode("utf-8")))
        except IndexError:
            return ("Null")
    def decompile(self):
        file = open("compil.json", "w")
        file.write(json.dumps(self.code))
        file.close()
        code = subprocess.run(["java", "-jar", "BranchSave.jar"], capture_output=True, shell=True, input=b"brch -f compil.json")
        a = code.stdout.split(b"\n")
        try:
            del [a[0], a[0], a[0]]
            out = b"".join(a)
            return (out.replace(b"\r", b"").replace(b"Branch.panel: ", b"").replace(b">> Error: java.util.NoSuchElementException: No line found", b"").replace(b";", b";\n").decode("utf-8")[:-1])
        except IndexError:
            return ("Null")

if __name__ == "__main__":
    """Example how to work this. Just run this."""
    branch = Branch("")
    test = Branch("""
Table = My cool table;
ValueTable: GoodModsLab;
import = ($)Table;""").compile()
    a = Branch(test).decompile()
    test["Table"] = "Test"
    print(type(branch), type(a), type(test))
    print(a)
    print(test)