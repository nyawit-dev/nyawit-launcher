from PIL import Image, ImageDraw, ImageFont
from pathlib import Path

OUT = Path(__file__).resolve().parent
S = 4
W, H = 164, 314

PAPER = (255, 249, 238, 255)
INK = (20, 17, 15, 255)
RED = (206, 17, 38, 255)
EMERALD = (61, 220, 151, 255)
MUTED = (111, 103, 95, 255)


def font(size, bold=False):
    name = "DejaVuSans-Bold.ttf" if bold else "DejaVuSans.ttf"
    return ImageFont.truetype("/usr/share/fonts/truetype/dejavu/" + name, size * S)


def text(draw, xy, value, size, fill=INK, bold=False, anchor=None, spacing=1):
    draw.text(xy, value, font=font(size, bold), fill=fill, anchor=anchor, spacing=spacing)


def halftone(draw, box, step=9, radius=1):
    x0, y0, x1, y1 = box
    for y in range(y0, y1, step):
        for x in range(x0, x1, step):
            r = radius * S
            draw.ellipse((x - r, y - r, x + r, y + r), fill=RED)


def build_wizard_image():
    im = Image.new("RGBA", (W * S, H * S), PAPER)
    d = ImageDraw.Draw(im)

    # Red action wedge and halftone field.
    d.polygon([(0, 120 * S), (W * S, 40 * S), (W * S, 190 * S), (0, 270 * S)], fill=RED)
    halftone(d, (0, 175 * S, W * S, H * S), step=10, radius=1)

    # Black ink speed lines.
    for i in range(7):
        y = (70 + i * 24) * S
        d.polygon([(0, y), ((40 + i * 7) * S, y - 8 * S), (0, y + 10 * S)], fill=INK)

    # Comic panel frame.
    d.rectangle((8 * S, 8 * S, (W - 8) * S, (H - 8) * S), outline=INK, width=2 * S)

    # Top masthead.
    text(d, (16 * S, 20 * S), "NYAWIT", 24, INK, True)
    text(d, (16 * S, 42 * S), "LAUNCHER", 15, INK, True)
    d.rectangle((16 * S, 60 * S, 148 * S, 64 * S), fill=INK)

    # Blank speech bubble.
    d.rounded_rectangle((72 * S, 78 * S, 150 * S, 118 * S), radius=6 * S, fill=PAPER, outline=INK, width=2 * S)
    d.polygon([(92 * S, 116 * S), (104 * S, 116 * S), (92 * S, 130 * S)], fill=PAPER, outline=INK)
    text(d, (86 * S, 90 * S), "SIAP?", 12, RED, True)

    # Three-finger hand silhouette.
    palm = [(58 * S, 214 * S), (100 * S, 214 * S), (108 * S, 250 * S), (68 * S, 266 * S), (44 * S, 238 * S)]
    d.polygon(palm, fill=INK)
    for x, top in [(58, 142), (75, 132), (92, 140)]:
        d.rounded_rectangle((x * S, top * S, (x + 13) * S, 222 * S), radius=6 * S, fill=INK)
    d.ellipse((34 * S, 188 * S, 62 * S, 216 * S), fill=INK)

    # Emerald badge.
    d.ellipse((16 * S, 220 * S, 56 * S, 260 * S), fill=EMERALD, outline=INK, width=2 * S)
    text(d, (36 * S, 240 * S), "3", 22, INK, True, anchor="mm")

    # Bottom strip.
    d.rectangle((8 * S, 278 * S, (W - 8) * S, (H - 8) * S), fill=INK)
    text(d, (16 * S, 288 * S), "EDISI INSTALASI", 11, PAPER, True)
    text(d, (16 * S, 302 * S), "pasang • jalankan • update", 8, PAPER)

    im.convert("RGB").resize((W, H), Image.Resampling.LANCZOS).save(OUT / "wizard-image.bmp")


def build_small_image():
    size = 147
    im = Image.new("RGBA", (size * S, size * S), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    cx, cy, r = size * S // 2, size * S // 2, 66 * S
    pts = []
    for i in range(16):
        import math
        a = 2 * math.pi * i / 16
        rr = r if i % 2 == 0 else r * 0.72
        pts.append((cx + rr * math.cos(a), cy + rr * math.sin(a)))
    d.polygon(pts, fill=RED)
    d.ellipse((cx - 48 * S, cy - 48 * S, cx + 48 * S, cy + 48 * S), fill=PAPER, outline=INK, width=3 * S)
    for x, top in [(48, 42), (68, 34), (88, 44)]:
        d.rounded_rectangle((x * S, top * S, (x + 15) * S, 96 * S), radius=7 * S, fill=INK)
    d.ellipse((44 * S, 78 * S, 102 * S, 116 * S), fill=INK)
    d.ellipse((100 * S, 96 * S, 116 * S, 112 * S), fill=EMERALD, outline=INK, width=2 * S)
    im.resize((size, size), Image.Resampling.LANCZOS).save(OUT / "wizard-small-image.png")


if __name__ == "__main__":
    build_wizard_image()
    build_small_image()
    print("wrote wizard-image.bmp and wizard-small-image.png")
