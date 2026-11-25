import { renderHtmlToPng, ROOT_DIR } from "./render_helper.js";
import fs from "fs";
import path from "path";

const sourceIconPath = path.join(ROOT_DIR, "androidApp", "src", "main", "ic_launcher-playstore.png");
const sourceIconBase64 = fs.readFileSync(sourceIconPath).toString("base64");
const iconDataUri = `data:image/png;base64,${sourceIconBase64}`;

async function generateCleanIcon(outputPath, size) {
  const html = `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  body {
    width: ${size}px;
    height: ${size}px;
    background: transparent;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }
  .icon-circle {
    width: ${size}px;
    height: ${size}px;
    border-radius: 50%;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .icon-circle img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transform: scale(1.02); /* slightly scale up so border-radius trims white edge */
  }
</style>
</head>
<body>
  <div class="icon-circle">
    <img src="${iconDataUri}" />
  </div>
</body>
</html>`;

  await renderHtmlToPng(html, outputPath, size, size, true);
  console.log(`Generated clean icon: ${outputPath} (${size}x${size})`);
}

async function generateTvBanner(outputPath) {
  const width = 1280;
  const height = 720;
  const html = `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  body {
    width: ${width}px;
    height: ${height}px;
    background: #0B0F14;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
  }
  .banner-frame {
    width: 1216px;
    height: 656px;
    background: linear-gradient(135deg, #101720 0%, #080C10 100%);
    border: 4px solid #3DDC97;
    border-radius: 28px;
    display: flex;
    align-items: center;
    padding: 0 80px;
    position: relative;
    box-shadow: 0 0 32px rgba(61, 220, 151, 0.15);
  }
  .halftone {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background-image: radial-gradient(circle, rgba(61, 220, 151, 0.08) 1.5px, transparent 1.5px);
    background-size: 24px 24px;
    pointer-events: none;
  }
  .icon-wrapper {
    width: 440px;
    height: 440px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
    box-shadow: 0 16px 40px rgba(0, 0, 0, 0.7), 0 0 24px rgba(61, 220, 151, 0.25);
    border: 4px solid #1E293B;
    background: #0F172A;
    position: relative;
    z-index: 2;
  }
  .icon-wrapper img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transform: scale(1.03);
  }
  .text-wrapper {
    margin-left: 64px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    z-index: 2;
  }
  .badge {
    align-self: flex-start;
    background: #3DDC97;
    color: #07130E;
    font-size: 20px;
    font-weight: 900;
    letter-spacing: 2px;
    padding: 6px 18px;
    border-radius: 8px;
    text-transform: uppercase;
    margin-bottom: 20px;
    box-shadow: 3px 3px 0 #000000;
  }
  .brand-title {
    font-size: 76px;
    font-weight: 900;
    letter-spacing: 4px;
    color: #FFFFFF;
    line-height: 1;
    text-shadow: 4px 4px 0 #000000;
  }
  .brand-sub {
    font-size: 38px;
    font-weight: 900;
    letter-spacing: 5px;
    color: #3DDC97;
    margin-top: 10px;
    text-shadow: 2px 2px 0 #000000;
  }
  .brand-desc {
    font-size: 22px;
    font-weight: 600;
    color: #94A3B8;
    margin-top: 18px;
    letter-spacing: 1px;
  }
</style>
</head>
<body>
  <div class="banner-frame">
    <div class="halftone"></div>
    <div class="icon-wrapper">
      <img src="${iconDataUri}" />
    </div>
    <div class="text-wrapper">
      <div class="badge">EDISI TV & ANDROID</div>
      <div class="brand-title">NYAWIT</div>
      <div class="brand-sub">LAUNCHER</div>
      <div class="brand-desc">Katalog Aplikasi & Modul Open-Source</div>
    </div>
  </div>
</body>
</html>`;

  await renderHtmlToPng(html, outputPath, width, height, false);
  console.log(`Generated TV banner: ${outputPath} (${width}x${height})`);
}

async function main() {
  console.log("Starting icon and banner generation...");

  const targets = [
    { path: path.join(ROOT_DIR, "shared", "src", "commonMain", "composeResources", "drawable", "nyawit_launcher.png"), size: 512 },
    { path: path.join(ROOT_DIR, "shared", "src", "commonMain", "composeResources", "drawable", "app_icon.png"), size: 512 },
    { path: path.join(ROOT_DIR, "desktopApp", "src", "desktopMain", "resources", "nyawit.png"), size: 512 },
    { path: path.join(ROOT_DIR, "desktopApp", "icons", "linux", "nyawit.png"), size: 512 },
    { path: "C:/Users/ringga/.gemini/antigravity-cli/brain/7a45cf05-e3fd-40fd-8ec9-17ebd9d2c7ba/refined_icon.png", size: 512 },
  ];

  for (const t of targets) {
    await generateCleanIcon(t.path, t.size);
  }

  const tvBannerPath = path.join(ROOT_DIR, "androidApp", "src", "main", "res", "drawable-nodpi", "tv_banner.png");
  await generateTvBanner(tvBannerPath);

  const artifactBannerPath = "C:/Users/ringga/.gemini/antigravity-cli/brain/7a45cf05-e3fd-40fd-8ec9-17ebd9d2c7ba/refined_tv_banner.png";
  await generateTvBanner(artifactBannerPath);

  console.log("All icons and banners successfully generated!");
}

main().catch(err => {
  console.error("Error in generation:", err);
  process.exit(1);
});
