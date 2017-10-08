# coding=utf-8
"""
 github.com/wensheng/word2banner
"""

import sys
import os
import math
import platform
from io import BytesIO
from PIL import Image, ImageDraw, ImageFont

if sys.version_info[0] == 3:
    from configparser import ConfigParser
else:
    from ConfigParser import ConfigParser

system = platform.system()
if system == 'Windows':
    sname = 'win'
elif system == 'Darwin':
    sname = 'mac'
elif system == 'Linux':
    sname = 'linux'
else:
    print("Unknown sytem, can't continue")
    exit()

cfg = ConfigParser()
cfg.read(os.path.join(os.path.realpath(os.path.dirname(__file__)), 'word2banner.ini'))

margin = cfg.getint(sname, 'margin')


def fmt(b):
    return list(map(lambda x: int(x), "{:08b}".format(b)))


def word2banner(txt, font_id=4, font_size=24):
    font_file = cfg.get(sname, "font%02d" % font_id)
    font = ImageFont.truetype(font_file, font_size)
    im = Image.new('RGB', (font_size*len(txt), font_size+margin), (0, 0, 0))
    draw = ImageDraw.Draw(im)
    # txt must be unicode
    draw.text((0, 0), txt, font=font, fill=(255, 255, 255))
    del draw
    # 直接'1'不行，字残缺，不经过point也不行
    # im = im.convert('1')
    # 这样字有地方多出来了，总比残缺强
    im = im.convert('L').point(lambda i: i and 255).convert('1')
    f = BytesIO()
    im.save(f, "BMP")
    bs = bytearray(f.getvalue())
    f.close()

    offset = bs[10] + bs[11] * 256
    bwidth = bs[18] + bs[19] * 256  # bit width, 不需要去后两位啦
    pwb = int(math.ceil(bwidth/32.0)) * 4  # padded width in bytes
    height = bs[22] + bs[23] * 256  # 也不需要取后两位

    output = []
    for h in range(height-1, -1, -1):
        c = []
        for i in range(pwb):
            c += fmt(bs[offset+h*pwb+i])
        output.append(c)
    return output


if "__main__" == __name__:
    if len(sys.argv) < 2:
        exit("Usage: word2banner.py word [font_id] [size]")

    if sys.version_info[0] == 3:
        txt = sys.argv[1]
    else:
        txt = unicode(sys.argv[1], 'UTF-8')

    if len(sys.argv) > 2:
        font_id = int(sys.argv[2])
        if font_id < 1:
            exit("Dude!")
    else:
        font_id = 4

    if len(sys.argv) > 3:
        size = int(sys.argv[3])
        if size < 8:
            exit("can't print that small")
    else:
        size = 20

    banner = word2banner(txt, font_id, size)
    for r in banner:
        line = "".join(map(lambda x: x and '@@' or '  ', r))
        print(line.rstrip())
