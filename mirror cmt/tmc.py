from binary_reader import BinaryReader
import os
import sys

cwd = os.getcwd()
dir = os.path.join(cwd,"mirrored")
if not os.path.exists(dir):
    os.mkdir(dir)

for u in sys.argv[1:]:

    f = open(u, "rb")
    r = BinaryReader(f.read())
    r.set_endian(True)

    w = BinaryReader()
    w.set_endian(True)

    magic = r.read_str(3)

    if magic != 'CMT': continue

    header = r.read_bytes(33)
    count = r.read_uint32()
    header2 = r.read_bytes(8)

    w.write_str(magic)
    w.write_bytes(header)
    w.write_uint32(count)
    w.write_bytes(header2)

    for i in range(count):
        x = r.read_float()
        y = r.read_float()
        z = r.read_float()
        fov = r.read_float()
        Xfocus = r.read_float()
        Yfocus = r.read_float()
        Zfocus = r.read_float()
        rotation = r.read_float()

        w.write_float(-x)
        w.write_float(y)
        w.write_float(z)
        w.write_float(fov)
        w.write_float(-Xfocus)
        w.write_float(Yfocus)
        w.write_float(Zfocus)
        w.write_float(rotation)
    
    file = os.path.join(dir,os.path.basename(u))
    with open(file, "wb") as fw: fw.write(w.buffer())
