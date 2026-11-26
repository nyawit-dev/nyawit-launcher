import fs from "fs";
import path from "path";
import { ROOT_DIR } from "./render_helper.js";

// Let's inspect png header and chunks
function inspectPng(filePath) {
  const buf = fs.readFileSync(filePath);
  const w = buf.readUInt32BE(16);
  const h = buf.readUInt32BE(20);
  const bitDepth = buf.readUInt8(24);
  const colorType = buf.readUInt8(25); // 6 is RGBA, 2 is RGB
  return { file: path.relative(ROOT_DIR, filePath), w, h, bitDepth, colorType, size: buf.length };
}

const files = [
  "assets/app-icon.png",
  "assets/emblem-tight.png",
  "assets/app-icons/nyawit-launcher.png",
  "play/ic_launcher-512.png",
  "assets/logo-new.png",
  "assets/tv-banner.png",
  "androidApp/src/main/res/drawable-nodpi/tv_banner.png",
  "play/feature-graphic.png",
  "docs/images/readme-hero.png",
  "play/promo-graphic.png",
  "docs/images/social-preview.png",
  "assets/ui/hero-desktop.png",
  "docs/images/release-windows.png",
  "docs/images/release-linux.png",
  "docs/images/release-macos.png"
];

for (const f of files) {
  const p = path.join(ROOT_DIR, f);
  if (fs.existsSync(p)) {
    console.log(JSON.stringify(inspectPng(p)));
  } else {
    console.log(`MISSING: ${f}`);
  }
}
