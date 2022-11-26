import subprocess
import os.path
import os

if os.path.exists("BranchSave.jar"):
    pass
else:
    raise SyntaxError("BranchSave.jar не найден!")

inp = []
print("Print exit or quit for starting compiling")
while True:
    a = input("~")
    if a != "exit" and a != "quit":
        inp.append(a)
    else:
        inp = '\n'.join(inp)
        break
file = open("compil.brch", "w")
file.write(inp)
file.close()
code = subprocess.run(["java", "-jar", "BranchSave.jar"], capture_output=True, shell=True, input=b"brch -f compil.brch")
a = code.stdout.split(b"\n")
try:
    del [a[0], a[0], a[0], a[1]]
    out = b"".join(a)
    print(out.replace(b"\r", b"").decode("utf-8") )
except IndexError:
    print("Null")