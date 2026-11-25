import { renderHtmlToPng, ROOT_DIR } from "./render_helper.js";
import fs from "fs";
import path from "path";

const bannerPath = "C:/Users/ringga/.gemini/antigravity-cli/brain/7a45cf05-e3fd-40fd-8ec9-17ebd9d2c7ba/.user_uploaded/uploaded_media_1795593409612.jpg";
const iconPath = path.join(ROOT_DIR, "shared", "src", "commonMain", "composeResources", "drawable", "nyawit_launcher.png");

const bannerB64 = fs.readFileSync(bannerPath).toString("base64");
const iconB64 = fs.readFileSync(iconPath).toString("base64");

const bannerUri = `data:image/jpeg;base64,${bannerB64}`;
const iconUri = `data:image/png;base64,${iconB64}`;

async function renderBannerWithOverlay(outputPath, width, height) {
  const html = `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  body {
    width: ${width}px;
    height: ${height}px;
    background: #000;
    overflow: hidden;
    position: relative;
  }
  .base-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
  .icon-overlay-container {
    position: absolute;
    top: 53.4%;
    left: 50.4%;
    transform: translate(-50%, -50%) rotate(-4deg);
    width: ${Math.round(108 * (width / 1280))}px;
    height: ${Math.round(108 * (height / 720))}px;
    border-radius: 50%;
    background: #FFFFFF;
    box-shadow: 0 4px 14px rgba(0,0,0,0.7), 0 0 16px rgba(255, 222, 0, 0.4);
    display: flex;
    align-items: center;
    justify-content: center;
    border: ${3.5 * (width / 1280)}px solid #0F172A;
  }
  .icon-overlay-container img {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    object-fit: cover;
  }
</style>
</head>
<body>
  <img class="base-img" src="${bannerUri}" />
  <div class="icon-overlay-container">
    <img src="${iconUri}" />
  </div>
</body>
</html>`;

  await renderHtmlToPng(html, outputPath, width, height, false);
  console.log(`Rendered banner to ${outputPath} (${width}x${height})`);
}

async function main() {
  // 1. Android TV Banner (1280x720)
  const tvBanner = path.join(ROOT_DIR, "androidApp", "src", "main", "res", "drawable-nodpi", "tv_banner.png");
  await renderBannerWithOverlay(tvBanner, 1280, 720);

  // 2. Desktop hero banner (1280x720)
  const heroDesktop = path.join(ROOT_DIR, "shared", "src", "commonMain", "composeResources", "drawable", "hero_desktop.png");
  await renderBannerWithOverlay(heroDesktop, 1280, 720);

  // 3. Artifact banner for user review
  const artifactBanner = "C:/Users/ringga/.gemini/antigravity-cli/brain/7a45cf05-e3fd-40fd-8ec9-17ebd9d2c7ba/new_official_tv_banner.png";
  await renderBannerWithOverlay(artifactBanner, 1280, 720);

  console.log("All banner outputs completed successfully!");
}

main().catch(console.error);
