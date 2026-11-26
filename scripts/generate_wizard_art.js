import fs from "fs";
import path from "path";
import { inflateSync } from "zlib";
import { renderHtmlToPng, ROOT_DIR } from "./render_helper.js";

const OUT_DIR = path.join(ROOT_DIR, "installer", "windows");
const PAPER = "#fff9ee";
const INK = "#14110f";
const RED = "#ce1126";
const EMERALD = "#3ddc97";

// Inno Setup 6.6+ menggambar wizard pada area 202x386 di 100% DPI dan 336x643 di 150% DPI.
// Image 336x643 menghindari blur dan (bersama WizardImageStretch=yes) mengisi area di semua DPI.
const WIZARD_W = 336;
const WIZARD_H = 643;
const SMALL_SIZE = 294;

function svgDocument(width, height, viewBox, body) {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  html, body { width: ${width}px; height: ${height}px; overflow: hidden; background: transparent; }
  svg { display: block; width: ${width}px; height: ${height}px; }
</style>
</head>
<body>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="${viewBox}" width="${width}" height="${height}">
${body}
</svg>
</body>
</html>`;
}

function text(x, y, value, size, fill, bold = false, anchor = "start") {
  return `<text x="${x}" y="${y}" font-family="Arial, Helvetica, sans-serif" font-size="${size}" font-weight="${bold ? 700 : 400}" fill="${fill}" text-anchor="${anchor}">${value}</text>`;
}

function wizardImageSvg() {
  const halftone = [];
  for (let y = 175; y < 314; y += 10) {
    for (let x = 0; x < 164; x += 10) {
      halftone.push(`<circle cx="${x}" cy="${y}" r="1" fill="${RED}"/>`);
    }
  }
  const speedLines = [];
  for (let i = 0; i < 7; i++) {
    const y = 70 + i * 24;
    speedLines.push(`<polygon points="0,${y} ${40 + i * 7},${y - 8} 0,${y + 10}" fill="${INK}"/>`);
  }
  const fingers = [
    { x: 58, top: 142 },
    { x: 75, top: 132 },
    { x: 92, top: 140 },
  ].map(({ x, top }) => `<rect x="${x}" y="${top}" width="13" height="${222 - top}" rx="6" fill="${INK}"/>`).join("\n");

  return `
<rect width="164" height="314" fill="${PAPER}"/>
<polygon points="0,120 164,40 164,190 0,270" fill="${RED}"/>
${halftone.join("\n")}
${speedLines.join("\n")}
<rect x="8" y="8" width="148" height="298" fill="none" stroke="${INK}" stroke-width="2"/>
${text(16, 39, "NYAWIT", 24, INK, true)}
${text(16, 54, "LAUNCHER", 15, INK, true)}
<rect x="16" y="60" width="132" height="4" fill="${INK}"/>
<rect x="72" y="78" width="78" height="40" rx="6" fill="${PAPER}" stroke="${INK}" stroke-width="2"/>
<polygon points="92,116 104,116 92,130" fill="${PAPER}" stroke="${INK}" stroke-width="2"/>
${text(86, 100, "SIAP?", 12, RED, true)}
<polygon points="58,214 100,214 108,250 68,266 44,238" fill="${INK}"/>
${fingers}
<ellipse cx="48" cy="202" rx="14" ry="14" fill="${INK}"/>
<ellipse cx="36" cy="240" rx="20" ry="20" fill="${EMERALD}" stroke="${INK}" stroke-width="2"/>
${text(36, 240, "3", 22, INK, true, "middle")}
<rect x="8" y="278" width="148" height="28" fill="${INK}"/>
${text(16, 293, "EDISI INSTALASI", 10, PAPER, true)}
${text(16, 303, "pasang • jalankan • update", 7.5, PAPER)}`;
}

function smallImageSvg() {
  const stars = [];
  for (let i = 0; i < 16; i++) {
    const angle = (2 * Math.PI * i) / 16;
    const radius = i % 2 === 0 ? 66 : 66 * 0.72;
    stars.push(`${(73.5 + radius * Math.cos(angle)).toFixed(2)},${(73.5 + radius * Math.sin(angle)).toFixed(2)}`);
  }
  return `
<polygon points="${stars.join(" ")}" fill="${RED}"/>
<circle cx="73.5" cy="73.5" r="48" fill="${PAPER}" stroke="${INK}" stroke-width="3"/>
${[
  { x: 48, top: 42 },
  { x: 68, top: 34 },
  { x: 88, top: 44 },
].map(({ x, top }) => `<rect x="${x}" y="${top}" width="15" height="${96 - top}" rx="7" fill="${INK}"/>`).join("\n")}
<ellipse cx="73" cy="97" rx="29" ry="19" fill="${INK}"/>
<ellipse cx="108" cy="104" rx="8" ry="8" fill="${EMERALD}" stroke="${INK}" stroke-width="2"/>`;
}

function readPngDimensions(png) {
  if (png.subarray(0, 8).toString("hex") !== "89504e470d0a1a0a") {
    throw new Error("Output bukan file PNG yang valid");
  }
  return { width: png.readUInt32BE(16), height: png.readUInt32BE(20), colorType: png[25] };
}

// Edge menghasilkan PNG 8-bit RGB/RGBA non-interlaced. Decode minimal untuk ditulis ulang sebagai BMP 24-bit.
function pngToBmp(png) {
  const { width, height, colorType } = readPngDimensions(png);
  if (png[24] !== 8 || (colorType !== 2 && colorType !== 6)) {
    throw new Error(`Format PNG tidak didukung: bitDepth=${png[24]} colorType=${colorType}`);
  }
  const channels = colorType === 6 ? 4 : 3;
  const chunks = [];
  let offset = 8;
  while (offset < png.length) {
    const length = png.readUInt32BE(offset);
    const type = png.subarray(offset + 4, offset + 8).toString("ascii");
    if (type === "IDAT") chunks.push(png.subarray(offset + 8, offset + 8 + length));
    offset += 12 + length;
    if (type === "IEND") break;
  }
  const raw = inflateSync(Buffer.concat(chunks));
  const stride = width * channels;
  const pixels = Buffer.alloc(height * stride);
  for (let y = 0; y < height; y++) {
    const filter = raw[y * (stride + 1)];
    const src = raw.subarray(y * (stride + 1) + 1, y * (stride + 1) + 1 + stride);
    const dst = pixels.subarray(y * stride, (y + 1) * stride);
    const prev = y > 0 ? pixels.subarray((y - 1) * stride, y * stride) : Buffer.alloc(stride);
    for (let i = 0; i < stride; i++) {
      const a = i >= channels ? dst[i - channels] : 0;
      const b = prev[i];
      const c = i >= channels ? prev[i - channels] : 0;
      let value = src[i];
      if (filter === 1) value += a;
      else if (filter === 2) value += b;
      else if (filter === 3) value += (a + b) >> 1;
      else if (filter === 4) {
        const p = a + b - c;
        const pa = Math.abs(p - a);
        const pb = Math.abs(p - b);
        const pc = Math.abs(p - c);
        value += pa <= pb && pa <= pc ? a : pb <= pc ? b : c;
      } else if (filter !== 0) {
        throw new Error(`Filter PNG tidak dikenal: ${filter}`);
      }
      dst[i] = value & 0xff;
    }
  }

  const rowSize = Math.floor((24 * width + 31) / 32) * 4;
  const imageSize = rowSize * height;
  const bmp = Buffer.alloc(54 + imageSize);
  bmp.write("BM", 0, "ascii");
  bmp.writeUInt32LE(54 + imageSize, 2);
  bmp.writeUInt32LE(54, 10);
  bmp.writeUInt32LE(40, 14);
  bmp.writeInt32LE(width, 18);
  bmp.writeInt32LE(height, 22);
  bmp.writeUInt16LE(1, 26);
  bmp.writeUInt16LE(24, 28);
  bmp.writeUInt32LE(imageSize, 34);
  for (let y = 0; y < height; y++) {
    const sourceRow = pixels.subarray(y * stride, (y + 1) * stride);
    const targetRow = 54 + (height - 1 - y) * rowSize;
    for (let x = 0; x < width; x++) {
      bmp[targetRow + x * 3] = sourceRow[x * channels + 2];
      bmp[targetRow + x * 3 + 1] = sourceRow[x * channels + 1];
      bmp[targetRow + x * 3 + 2] = sourceRow[x * channels];
    }
  }
  return bmp;
}

async function renderTo(file, html, width, height) {
  const temp = path.join(ROOT_DIR, `wizard_${Date.now()}_${Math.random().toString(36).slice(2)}.png`);
  await renderHtmlToPng(html, temp, width, height, true);
  const result = fs.readFileSync(temp);
  fs.unlinkSync(temp);
  return result;
}

async function run() {
  fs.mkdirSync(OUT_DIR, { recursive: true });

  const wizardPng = await renderTo(
    "wizard-image",
    svgDocument(WIZARD_W, WIZARD_H, "0 0 164 314", wizardImageSvg()),
    WIZARD_W,
    WIZARD_H,
  );
  const wizardBmp = pngToBmp(wizardPng);
  fs.writeFileSync(path.join(OUT_DIR, "wizard-image.bmp"), wizardBmp);
  console.log(`[OK] wizard-image.bmp (${WIZARD_W}x${WIZARD_H})`);

  const smallPng = await renderTo(
    "wizard-small-image",
    svgDocument(SMALL_SIZE, SMALL_SIZE, "0 0 147 147", smallImageSvg()),
    SMALL_SIZE,
    SMALL_SIZE,
  );
  const smallDimensions = readPngDimensions(smallPng);
  if (smallDimensions.width !== SMALL_SIZE || smallDimensions.height !== SMALL_SIZE) {
    throw new Error(`Ukuran wizard-small-image salah: ${smallDimensions.width}x${smallDimensions.height}`);
  }
  fs.writeFileSync(path.join(OUT_DIR, "wizard-small-image.png"), smallPng);
  console.log(`[OK] wizard-small-image.png (${SMALL_SIZE}x${SMALL_SIZE})`);
}

run().catch((err) => {
  console.error("Gagal generate wizard art:", err);
  process.exit(1);
});
