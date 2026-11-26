import fs from "fs";
import path from "path";
import { renderHtmlToPng, ROOT_DIR, BRAND_MARK_URI } from "./render_helper.js";

// Authentic Comic Typography & Styles
const COMIC_TITLE_FONT = `font-family: "Impact", "Arial Black", -apple-system, sans-serif; text-transform: uppercase;`;
const COMIC_BODY_FONT = `font-family: "Trebuchet MS", "Segoe UI", sans-serif; font-weight: 700;`;

// Dynamic Comic Backgrounds: Comic Action Rays & Ben-Day Halftone Dots
const COMIC_BG_EFFECTS = `
  background-color: #0B0F14;
  background-image: 
    repeating-conic-gradient(from 0deg, rgba(61, 220, 151, 0.09) 0deg 15deg, transparent 15deg 30deg),
    radial-gradient(circle at 85% 20%, rgba(255, 94, 94, 0.12) 0%, transparent 55%),
    radial-gradient(#3DDC97 18%, transparent 18%),
    radial-gradient(rgba(255, 230, 0, 0.15) 15%, transparent 15%);
  background-size: 100% 100%, 100% 100%, 20px 20px, 40px 40px;
  background-position: 0 0, 0 0, 0 0, 10px 10px;
`;

// 2. assets/app-icons/nyawit-launcher.png (512x512, transparent PNG)
// "Emblem yang sama, aksen emerald, tanpa teks"
function getNyawitLauncherIconHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 512px;
    height: 512px;
    background: transparent;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    ${COMIC_BODY_FONT}
  }
  .comic-tile {
    width: 440px;
    height: 440px;
    background: #101720;
    background-image: 
      repeating-conic-gradient(from 0deg, rgba(61, 220, 151, 0.15) 0deg 18deg, transparent 18deg 36deg),
      radial-gradient(#3DDC97 20%, transparent 20%);
    background-size: 100% 100%, 18px 18px;
    border: 7px solid #000000;
    border-radius: 96px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 10px 10px 0px #000000, 0 0 36px rgba(61, 220, 151, 0.4);
    position: relative;
    overflow: hidden;
  }
  .inner-emerald-rim {
    position: absolute;
    top: 10px; left: 10px; right: 10px; bottom: 10px;
    border: 4px solid #3DDC97;
    border-radius: 82px;
    pointer-events: none;
  }
  .emblem-img {
    width: 330px;
    height: 330px;
    object-fit: contain;
    position: relative;
    z-index: 2;
    filter: drop-shadow(6px 6px 0px #000000);
  }
</style>
</head>
<body>
  <div class="comic-tile">
    <div class="inner-emerald-rim"></div>
    <img class="emblem-img" src="${BRAND_MARK_URI}" alt="Nyawit Comic Emblem" />
  </div>
</body>
</html>`;
}

// 3. play/ic_launcher-512.png (512x512, opaque PNG)
// "Emblem di atas rounded-square navy, lingkaran emerald di belakang, tanpa teks"
function getPlayIconHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 512px;
    height: 512px;
    background: #0B0F14;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    ${COMIC_BODY_FONT}
  }
  .comic-square-navy {
    width: 450px;
    height: 450px;
    background: #141C24;
    border: 6px solid #000000;
    border-radius: 96px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 10px 10px 0px #000000, 0 0 40px rgba(61, 220, 151, 0.3);
    position: relative;
    overflow: hidden;
  }
  .comic-sunburst {
    position: absolute;
    width: 320px;
    height: 320px;
    border-radius: 50%;
    background: 
      repeating-conic-gradient(from 0deg, #3DDC97 0deg 20deg, #20A66D 20deg 40deg);
    border: 5px solid #000000;
    box-shadow: 0 0 24px rgba(61, 220, 151, 0.5);
  }
  .emblem-img {
    width: 290px;
    height: 290px;
    object-fit: contain;
    position: relative;
    z-index: 2;
    filter: drop-shadow(6px 6px 0px #000000);
  }
</style>
</head>
<body>
  <div class="comic-square-navy">
    <div class="comic-sunburst"></div>
    <img class="emblem-img" src="${BRAND_MARK_URI}" alt="Play Icon Comic" />
  </div>
</body>
</html>`;
}

// 4. assets/logo-new.png (1024x256, transparent PNG)
// "Emblem di kotak navy bulat di kiri; teks NYAWIT (putih) + LAUNCHER (emerald) + tagline ANDROID • ANDROID TV • DESKTOP"
function getLogoNewHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1024px;
    height: 256px;
    background: transparent;
    overflow: hidden;
    display: flex;
    align-items: center;
    padding-left: 40px;
  }
  .comic-box {
    width: 176px;
    height: 176px;
    background: #141C24;
    border: 5px solid #000000;
    border-radius: 44px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 6px 6px 0px #000000, 0 0 24px rgba(61, 220, 151, 0.3);
    flex-shrink: 0;
    position: relative;
    overflow: hidden;
  }
  .comic-box::before {
    content: "";
    position: absolute;
    width: 130px;
    height: 130px;
    border-radius: 50%;
    background: repeating-conic-gradient(from 0deg, rgba(61, 220, 151, 0.3) 0deg 20deg, transparent 20deg 40deg);
  }
  .comic-box img {
    width: 146px;
    height: 146px;
    object-fit: contain;
    position: relative;
    z-index: 2;
    filter: drop-shadow(4px 4px 0px #000000);
  }
  .text-content {
    margin-left: 36px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .wordmark-row {
    display: flex;
    align-items: baseline;
    gap: 16px;
    transform: skew(-4deg, -1deg);
  }
  .brand-nyawit {
    ${COMIC_TITLE_FONT}
    font-size: 78px;
    letter-spacing: 3px;
    color: #FFFFFF;
    line-height: 1;
    -webkit-text-stroke: 3px #000000;
    text-shadow: 5px 5px 0px #000000;
  }
  .brand-launcher {
    ${COMIC_TITLE_FONT}
    font-size: 78px;
    letter-spacing: 3px;
    color: #3DDC97;
    line-height: 1;
    -webkit-text-stroke: 3px #000000;
    text-shadow: 5px 5px 0px #000000, 0 0 20px rgba(61, 220, 151, 0.4);
  }
  .comic-caption-box {
    margin-top: 10px;
    align-self: flex-start;
    background: #FFE600;
    color: #000000;
    border: 3px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    padding: 6px 14px;
    font-size: 14px;
    font-weight: 900;
    letter-spacing: 3px;
    ${COMIC_TITLE_FONT}
    transform: skew(-4deg);
  }
</style>
</head>
<body>
  <div class="comic-box">
    <img src="${BRAND_MARK_URI}" alt="Comic Sawit Emblem" />
  </div>
  <div class="text-content">
    <div class="wordmark-row">
      <span class="brand-nyawit">NYAWIT</span>
      <span class="brand-launcher">LAUNCHER</span>
    </div>
    <div class="comic-caption-box">
      ANDROID &bull; ANDROID TV &bull; DESKTOP
    </div>
  </div>
</body>
</html>`;
}

// 5. assets/tv-banner.png + androidApp/.../tv_banner.png (1280x720, PNG)
// "Background navy, frame emerald, emblem kiri ~440px, teks EDISI TV & ANDROID, NYAWIT LAUNCHER, Katalog Aplikasi & Modul Open-Source"
function getTvBannerHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 720px;
    ${COMIC_BG_EFFECTS}
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .comic-frame {
    width: 1220px;
    height: 660px;
    background: #101720;
    background-image: 
      radial-gradient(circle at 10% 90%, rgba(255, 94, 94, 0.15) 0%, transparent 50%),
      radial-gradient(#3DDC97 15%, transparent 15%);
    background-size: 100% 100%, 24px 24px;
    border: 6px solid #3DDC97;
    border-radius: 28px;
    display: flex;
    align-items: center;
    padding: 0 64px;
    position: relative;
    box-shadow: 12px 12px 0px #000000, 0 0 40px rgba(61, 220, 151, 0.3);
  }
  .emblem-wrapper {
    width: 440px;
    height: 440px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
    border: 6px solid #000000;
    background: #0F172A;
    box-shadow: 10px 10px 0px #000000, 0 0 32px rgba(61, 220, 151, 0.4);
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .emblem-wrapper img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .text-wrapper {
    margin-left: 56px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    z-index: 2;
  }
  .comic-badge {
    align-self: flex-start;
    background: #FFE600;
    color: #000000;
    ${COMIC_TITLE_FONT}
    font-size: 22px;
    letter-spacing: 3px;
    padding: 8px 22px;
    border: 3px solid #000000;
    border-radius: 8px;
    box-shadow: 4px 4px 0px #000000;
    margin-bottom: 20px;
    transform: rotate(-2deg);
  }
  .brand-title-row {
    transform: skew(-4deg, -1deg);
  }
  .brand-title {
    ${COMIC_TITLE_FONT}
    font-size: 80px;
    letter-spacing: 4px;
    color: #FFFFFF;
    line-height: 0.95;
    -webkit-text-stroke: 3px #000000;
    text-shadow: 6px 6px 0px #000000;
  }
  .brand-sub {
    ${COMIC_TITLE_FONT}
    font-size: 44px;
    letter-spacing: 5px;
    color: #3DDC97;
    margin-top: 10px;
    -webkit-text-stroke: 2.5px #000000;
    text-shadow: 4px 4px 0px #000000, 0 0 20px rgba(61, 220, 151, 0.4);
  }
  .comic-narrative-box {
    margin-top: 24px;
    background: rgba(20, 28, 36, 0.95);
    border: 3px solid #000000;
    box-shadow: 5px 5px 0px #000000;
    border-left: 8px solid #3DDC97;
    padding: 12px 20px;
    border-radius: 8px;
    max-width: 580px;
  }
  .brand-desc {
    ${COMIC_BODY_FONT}
    font-size: 20px;
    color: #F1F5F7;
    letter-spacing: 0.5px;
  }
</style>
</head>
<body>
  <div class="comic-frame">
    <div class="emblem-wrapper">
      <img src="${BRAND_MARK_URI}" alt="Comic Sawit Emblem" />
    </div>
    <div class="text-wrapper">
      <div class="comic-badge">EDISI TV & ANDROID</div>
      <div class="brand-title-row">
        <div class="brand-title">NYAWIT</div>
        <div class="brand-sub">LAUNCHER</div>
      </div>
      <div class="comic-narrative-box">
        <div class="brand-desc">Katalog Aplikasi & Modul Open-Source</div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// 6. play/feature-graphic.png (1024x500, PNG)
// "Teks NYAWIT LAUNCHER, tagline 'Katalog aplikasi dan game multiplatform…', chip platform, mockup kartu"
function getFeatureGraphicHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1024px;
    height: 500px;
    ${COMIC_BG_EFFECTS}
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 48px;
  }
  .content {
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
  }
  .left-col {
    max-width: 530px;
  }
  .header-badge-row {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 12px;
  }
  .emblem-mini {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    border: 3px solid #000000;
    box-shadow: 3px 3px 0 #000000, 0 0 16px rgba(61,220,151,0.5);
  }
  .comic-tag {
    background: #FFE600;
    color: #000000;
    border: 2.5px solid #000000;
    box-shadow: 3px 3px 0 #000000;
    padding: 4px 12px;
    font-size: 11px;
    font-weight: 900;
    ${COMIC_TITLE_FONT}
    letter-spacing: 2px;
    transform: rotate(-2deg);
  }
  .brand-head {
    ${COMIC_TITLE_FONT}
    font-size: 52px;
    color: #FFFFFF;
    line-height: 1;
    -webkit-text-stroke: 2.5px #000000;
    text-shadow: 4px 4px 0px #000000;
    transform: skew(-4deg);
  }
  .brand-head span { color: #3DDC97; }
  .comic-bubble {
    margin-top: 14px;
    background: #141C24;
    border: 2.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    border-radius: 12px;
    padding: 10px 16px;
    ${COMIC_BODY_FONT}
    font-size: 13px;
    line-height: 1.5;
    color: #E2E8F0;
  }
  .chips-row {
    margin-top: 18px;
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  .comic-chip {
    background: #1B2630;
    border: 2px solid #000000;
    box-shadow: 2px 2px 0px #000000;
    border-radius: 8px;
    padding: 6px 12px;
    font-size: 11px;
    font-weight: 800;
    color: #3DDC97;
    ${COMIC_TITLE_FONT}
  }
  .right-col {
    display: flex;
    align-items: center;
    gap: 14px;
  }
  .comic-panel {
    background: #141C24;
    border: 3px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    border-radius: 18px;
    padding: 14px;
  }
  .phone-panel {
    width: 176px;
    height: 330px;
    border-color: #3DDC97;
    display: flex;
    flex-direction: column;
    gap: 10px;
    transform: rotate(2deg);
  }
  .app-item {
    background: #1C2732;
    border: 2px solid #000;
    border-radius: 8px;
    padding: 8px;
    display: flex;
    align-items: center;
    gap: 8px;
    box-shadow: 2px 2px 0 #000;
  }
  .app-letter {
    width: 28px;
    height: 28px;
    border-radius: 6px;
    border: 1.5px solid #000;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 900;
    font-size: 12px;
    color: #000;
    flex-shrink: 0;
  }
  .tv-panel {
    width: 210px;
    height: 240px;
    border-color: #FFE600;
    display: flex;
    flex-direction: column;
    justify-content: center;
    gap: 10px;
    transform: rotate(-2deg);
  }
</style>
</head>
<body>
  <div class="content">
    <div class="left-col">
      <div class="header-badge-row">
        <img class="emblem-mini" src="${BRAND_MARK_URI}" alt="Comic Emblem" />
        <div class="comic-tag">KOTLIN MULTIPLATFORM</div>
      </div>
      <div class="brand-head">NYAWIT<br><span>LAUNCHER</span></div>
      <div class="comic-bubble">
        Katalog aplikasi dan game multiplatform yang cepat, ringan, dan transparan langsung dari repositori GitHub.
      </div>
      <div class="chips-row">
        <div class="comic-chip">Android</div>
        <div class="comic-chip">Android TV</div>
        <div class="comic-chip">Windows</div>
        <div class="comic-chip">macOS</div>
        <div class="comic-chip">Linux</div>
      </div>
    </div>
    <div class="right-col">
      <div class="comic-panel phone-panel">
        <div style="font-size:10px;font-weight:900;color:#FFE600;display:flex;justify-content:space-between;letter-spacing:1px;">
          <span>KATALOG</span>
          <span style="color:#3DDC97;">● ONLINE</span>
        </div>
        <div class="app-item">
          <div class="app-letter" style="background:#5AA9E6;">M</div>
          <div>
            <div style="font-size:10px;font-weight:800;color:#FFF;">Nyawit Music</div>
            <div style="font-size:8px;color:#94A3B0;">Audio TV</div>
          </div>
        </div>
        <div class="app-item">
          <div class="app-letter" style="background:#FFB454;">A</div>
          <div>
            <div style="font-size:10px;font-weight:800;color:#FFF;">Garis Arcade</div>
            <div style="font-size:8px;color:#94A3B0;">Retro Game</div>
          </div>
        </div>
        <div class="app-item">
          <div class="app-letter" style="background:#F2789F;">T</div>
          <div>
            <div style="font-size:10px;font-weight:800;color:#FFF;">Garis Game</div>
            <div style="font-size:8px;color:#94A3B0;">Tetris KMP</div>
          </div>
        </div>
        <div style="margin-top:auto;background:#3DDC97;border:2px solid #000;box-shadow:2px 2px 0 #000;border-radius:6px;padding:6px;text-align:center;font-size:10px;font-weight:900;color:#07130E;">PASANG SEKARANG</div>
      </div>
      <div class="comic-panel tv-panel">
        <div style="background:#FFE600;color:#000;border:2px solid #000;box-shadow:2px 2px 0 #000;padding:4px;font-size:10px;font-weight:900;text-align:center;">DUKUNGAN ANDROID TV</div>
        <div style="font-size:10px;color:#94A3B0;line-height:1.4;">Optimasi navigasi D-pad Leanback dan fokus visual yang jelas di layar televisi.</div>
        <div style="background:#1B2630;border:2px solid #3DDC97;box-shadow:2px 2px 0 #000;border-radius:8px;padding:8px;display:flex;align-items:center;gap:8px;">
          <div style="width:10px;height:10px;border-radius:50%;background:#3DDC97;border:1px solid #000;"></div>
          <span style="font-size:10px;font-weight:800;color:#F1F5F7;">Fokus D-Pad Responsif</span>
        </div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// 7. docs/images/readme-hero.png (1280x400, PNG)
// "Emblem + NYAWIT LAUNCHER, chip: Kotlin 2.0+, Compose Multiplatform, Direct GitHub API, Leanback TV Focus, SHA-256 Checksum"
function getReadmeHeroHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 400px;
    ${COMIC_BG_EFFECTS}
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 64px;
  }
  .left {
    z-index: 2;
    display: flex;
    align-items: center;
    gap: 32px;
  }
  .comic-mark-box {
    width: 124px;
    height: 124px;
    background: #141C24;
    border: 4px solid #000000;
    border-radius: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 6px 6px 0px #000000, 0 0 24px rgba(61, 220, 151, 0.4);
  }
  .comic-mark-box img {
    width: 104px;
    height: 104px;
    object-fit: contain;
    filter: drop-shadow(4px 4px 0 #000);
  }
  .brand-group {
    transform: skew(-4deg, -1deg);
  }
  .h-title {
    ${COMIC_TITLE_FONT}
    font-size: 58px;
    color: #FFFFFF;
    line-height: 1;
    letter-spacing: 2px;
    -webkit-text-stroke: 2.5px #000000;
    text-shadow: 5px 5px 0px #000000;
  }
  .h-title span { color: #3DDC97; }
  .h-sub {
    margin-top: 8px;
    ${COMIC_BODY_FONT}
    font-size: 15px;
    color: #FFE600;
    letter-spacing: 1px;
    text-shadow: 2px 2px 0 #000;
  }
  .right {
    z-index: 2;
    display: flex;
    gap: 12px;
    flex-wrap: wrap;
    max-width: 500px;
    justify-content: flex-end;
  }
  .badge-sticker {
    background: #141C24;
    border: 3px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    border-radius: 10px;
    padding: 10px 18px;
    font-size: 13px;
    font-weight: 900;
    color: #F1F5F7;
    ${COMIC_TITLE_FONT}
  }
</style>
</head>
<body>
  <div class="left">
    <div class="comic-mark-box">
      <img src="${BRAND_MARK_URI}" alt="Comic Sawit Emblem" />
    </div>
    <div class="brand-group">
      <div class="h-title">NYAWIT <span>LAUNCHER</span></div>
      <div class="h-sub">COMPOSE MULTIPLATFORM &bull; ANDROID, TV & DESKTOP</div>
    </div>
  </div>
  <div class="right">
    <div class="badge-sticker" style="background:#3DDC97; color:#000;">Kotlin 2.0+</div>
    <div class="badge-sticker" style="background:#FFE600; color:#000;">Compose Multiplatform</div>
    <div class="badge-sticker" style="border-color:#5AA9E6; color:#5AA9E6;">Direct GitHub API</div>
    <div class="badge-sticker" style="border-color:#3DDC97; color:#3DDC97;">Leanback TV Focus</div>
    <div class="badge-sticker" style="background:#FF5E5E; color:#FFF;">SHA-256 Checksum</div>
  </div>
</body>
</html>`;
}

// 8. play/promo-graphic.png (1280x720, PNG)
// "NYAWIT LAUNCHER + tagline + emblem + platform"
function getPromoGraphicHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 720px;
    ${COMIC_BG_EFFECTS}
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .inner-frame {
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    max-width: 960px;
  }
  .emblem-ring {
    width: 180px;
    height: 180px;
    border-radius: 50%;
    border: 6px solid #000000;
    background: repeating-conic-gradient(from 0deg, #3DDC97 0deg 20deg, #20A66D 20deg 40deg);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 10px 10px 0px #000000, 0 0 48px rgba(61, 220, 151, 0.5);
    margin-bottom: 24px;
  }
  .emblem-ring img {
    width: 154px;
    height: 154px;
    object-fit: contain;
    filter: drop-shadow(4px 4px 0 #000);
  }
  .main-h {
    ${COMIC_TITLE_FONT}
    font-size: 72px;
    color: #FFFFFF;
    letter-spacing: 4px;
    line-height: 1;
    -webkit-text-stroke: 3px #000000;
    text-shadow: 6px 6px 0px #000000;
    transform: skew(-4deg, -1deg);
  }
  .main-h span { color: #3DDC97; }
  .comic-narrative {
    margin-top: 18px;
    background: #FFE600;
    color: #000000;
    border: 3px solid #000000;
    box-shadow: 5px 5px 0px #000000;
    border-radius: 12px;
    padding: 12px 24px;
    ${COMIC_BODY_FONT}
    font-size: 18px;
    max-width: 820px;
    transform: skew(-2deg);
  }
  .pills {
    margin-top: 32px;
    display: flex;
    gap: 16px;
  }
  .pill-sticker {
    background: #141C24;
    border: 3px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    border-radius: 14px;
    padding: 10px 22px;
    font-size: 14px;
    font-weight: 900;
    color: #FFFFFF;
    ${COMIC_TITLE_FONT}
    letter-spacing: 1px;
    display: flex;
    align-items: center;
    gap: 10px;
  }
  .pill-sticker span { color: #3DDC97; }
</style>
</head>
<body>
  <div class="inner-frame">
    <div class="emblem-ring">
      <img src="${BRAND_MARK_URI}" alt="Comic Sawit Emblem" />
    </div>
    <div class="main-h">NYAWIT <span>LAUNCHER</span></div>
    <div class="comic-narrative">
      Platform distribusi dan peluncur aplikasi multiplatform berbasis GitHub tanpa server dengan verifikasi integritas paket!
    </div>
    <div class="pills">
      <div class="pill-sticker"><span>●</span> Android & TV</div>
      <div class="pill-sticker"><span>●</span> Windows EXE / MSI</div>
      <div class="pill-sticker"><span>●</span> Linux DEB / RPM</div>
      <div class="pill-sticker"><span>●</span> macOS DMG</div>
    </div>
  </div>
</body>
</html>`;
}

// 9. docs/images/social-preview.png (1280x640, PNG)
// "NYAWIT LAUNCHER, emblem, tagline multiplatform"
function getSocialPreviewHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 640px;
    ${COMIC_BG_EFFECTS}
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 72px;
  }
  .content {
    z-index: 2;
    display: flex;
    width: 100%;
    justify-content: space-between;
    align-items: center;
  }
  .left {
    max-width: 650px;
  }
  .issue-badge {
    display: inline-flex;
    background: #FFE600;
    color: #000000;
    border: 3px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    padding: 6px 16px;
    font-size: 13px;
    ${COMIC_TITLE_FONT}
    letter-spacing: 2px;
    margin-bottom: 20px;
    transform: rotate(-2deg);
  }
  .title-row {
    display: flex;
    align-items: center;
    gap: 24px;
    margin-bottom: 16px;
  }
  .emblem-social {
    width: 104px;
    height: 104px;
    border-radius: 50%;
    border: 4px solid #000000;
    background: #141C24;
    box-shadow: 6px 6px 0px #000000, 0 0 24px rgba(61,220,151,0.4);
    flex-shrink: 0;
  }
  .comic-title {
    ${COMIC_TITLE_FONT}
    font-size: 60px;
    color: #FFFFFF;
    line-height: 0.95;
    -webkit-text-stroke: 2.5px #000000;
    text-shadow: 5px 5px 0px #000000;
    transform: skew(-4deg);
  }
  .comic-title span { color: #3DDC97; }
  .comic-narration {
    background: #141C24;
    border: 3px solid #000000;
    box-shadow: 5px 5px 0px #000000;
    border-radius: 12px;
    padding: 14px 20px;
    ${COMIC_BODY_FONT}
    font-size: 16px;
    line-height: 1.5;
    color: #F1F5F7;
  }
  .badges-row {
    margin-top: 20px;
    display: flex;
    gap: 12px;
  }
  .badge-chip {
    background: #1B2630;
    border: 2px solid #000000;
    box-shadow: 3px 3px 0px #000000;
    border-radius: 8px;
    padding: 8px 14px;
    font-size: 12px;
    ${COMIC_TITLE_FONT}
    color: #3DDC97;
  }
  .right {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  .comic-card {
    width: 380px;
    background: #141C24;
    border: 3px solid #000000;
    border-radius: 16px;
    padding: 16px 20px;
    display: flex;
    align-items: center;
    gap: 16px;
    box-shadow: 6px 6px 0px #000000;
  }
  .cc-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    border: 2px solid #000000;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 900;
    font-size: 20px;
    color: #000000;
    flex-shrink: 0;
  }
</style>
</head>
<body>
  <div class="content">
    <div class="left">
      <div class="issue-badge">OPEN SOURCE &bull; GPL-3.0 &bull; MULTIPLATFORM</div>
      <div class="title-row">
        <img class="emblem-social" src="${BRAND_MARK_URI}" alt="Comic Sawit Emblem" />
        <div class="comic-title">Nyawit<br><span>Launcher</span></div>
      </div>
      <div class="comic-narration">
        A modern Kotlin Multiplatform app launcher with Compose Multiplatform for Android, Android TV, and JVM Desktop.
      </div>
      <div class="badges-row">
        <div class="badge-chip">Direct GitHub REST API</div>
        <div class="badge-chip">Zero Backend Server</div>
        <div class="badge-chip">SHA-256 Verified</div>
      </div>
    </div>
    <div class="right">
      <div class="comic-card" style="border-left: 8px solid #3DDC97;">
        <div class="cc-icon" style="background:#3DDC97;">N</div>
        <div>
          <div style="font-size:16px;font-weight:900;color:#FFF;${COMIC_TITLE_FONT}">Nyawit Launcher</div>
          <div style="font-size:12px;color:#94A3B0;margin-top:2px;">v1.4.3 &bull; Multiplatform Release</div>
        </div>
      </div>
      <div class="comic-card" style="border-left: 8px solid #5AA9E6;">
        <div class="cc-icon" style="background:#5AA9E6;">M</div>
        <div>
          <div style="font-size:16px;font-weight:900;color:#FFF;${COMIC_TITLE_FONT}">Nyawit Music</div>
          <div style="font-size:12px;color:#94A3B0;margin-top:2px;">v1.2.0 &bull; Audio TV & Mobile</div>
        </div>
      </div>
      <div class="comic-card" style="border-left: 8px solid #FFB454;">
        <div class="cc-icon" style="background:#FFB454;">A</div>
        <div>
          <div style="font-size:16px;font-weight:900;color:#FFF;${COMIC_TITLE_FONT}">Garis Arcade</div>
          <div style="font-size:12px;color:#94A3B0;margin-top:2px;">v1.1.4 &bull; Retro Arcade Game</div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// 10. assets/ui/hero-desktop.png (1600x600, PNG)
// "Hero desktop, kartu aplikasi, brand NYAWIT"
function getHeroDesktopHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1600px;
    height: 600px;
    ${COMIC_BG_EFFECTS}
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 80px;
  }
  .left-side {
    z-index: 2;
    max-width: 680px;
  }
  .brand-header-row {
    display: flex;
    align-items: center;
    gap: 24px;
    margin-bottom: 20px;
  }
  .hero-emblem {
    width: 104px;
    height: 104px;
    border-radius: 50%;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000, 0 0 28px rgba(61,220,151,0.5);
    flex-shrink: 0;
  }
  .brand-title {
    ${COMIC_TITLE_FONT}
    font-size: 64px;
    color: #FFFFFF;
    line-height: 1;
    -webkit-text-stroke: 3px #000000;
    text-shadow: 5px 5px 0px #000000;
    transform: skew(-4deg);
  }
  .brand-title span { color: #3DDC97; }
  .comic-narrative-box {
    background: #141C24;
    border: 3.5px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    border-radius: 16px;
    padding: 16px 24px;
    ${COMIC_BODY_FONT}
    font-size: 17px;
    line-height: 1.6;
    color: #F1F5F7;
    margin-bottom: 24px;
  }
  .platforms-row {
    display: flex;
    gap: 12px;
  }
  .chip-sticker {
    background: #FFE600;
    color: #000000;
    border: 2.5px solid #000000;
    box-shadow: 3px 3px 0px #000000;
    padding: 8px 18px;
    border-radius: 10px;
    font-size: 13px;
    font-weight: 900;
    ${COMIC_TITLE_FONT}
  }
  .right-side {
    z-index: 2;
    display: flex;
    gap: 24px;
    perspective: 1000px;
  }
  .comic-card-preview {
    width: 220px;
    height: 330px;
    background: #141C24;
    border: 4px solid #000000;
    border-radius: 20px;
    padding: 24px 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    box-shadow: 8px 8px 0px #000000;
  }
  .c-card-1 { transform: rotate(-5deg) translateY(10px); }
  .c-card-2 { border-color: #3DDC97; transform: translateY(-15px); }
  .c-card-3 { transform: rotate(5deg) translateY(15px); }
  .icon-wrap {
    width: 76px;
    height: 76px;
    border-radius: 18px;
    border: 3px solid #000000;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 3px 3px 0 #000;
  }
  .c-title {
    ${COMIC_TITLE_FONT}
    font-size: 18px;
    color: #FFF;
    margin-bottom: 4px;
    text-align: center;
  }
  .c-sub {
    font-size: 12px;
    color: #94A3B0;
    text-align: center;
    margin-bottom: 24px;
  }
  .c-btn {
    margin-top: auto;
    width: 100%;
    padding: 10px;
    border-radius: 8px;
    border: 2px solid #000;
    box-shadow: 3px 3px 0 #000;
    font-size: 12px;
    font-weight: 900;
    ${COMIC_TITLE_FONT}
    text-align: center;
  }
</style>
</head>
<body>
  <div class="left-side">
    <div class="brand-header-row">
      <img class="hero-emblem" src="${BRAND_MARK_URI}" alt="Comic Sawit Emblem" />
      <div class="brand-title">NYAWIT <span style="color:#3DDC97;">LAUNCHER</span></div>
    </div>
    <div class="comic-narrative-box">
      Jelajahi, instal, dan perbarui aplikasi serta game multiplatform langsung dari GitHub API tanpa perantara backend terpusat.
    </div>
    <div class="platforms-row">
      <div class="chip-sticker">Android</div>
      <div class="chip-sticker">Android TV</div>
      <div class="chip-sticker">Windows</div>
      <div class="chip-sticker">macOS</div>
      <div class="chip-sticker">Linux</div>
    </div>
  </div>
  <div class="right-side">
    <div class="comic-card-preview c-card-1">
      <div class="icon-wrap" style="background:#5AA9E6;">
        <svg viewBox="0 0 24 24" width="38" height="38" fill="none" stroke="#000" stroke-width="2.5"><circle cx="12" cy="12" r="9"/><path d="M9 18V6l10 6-10 6z"/></svg>
      </div>
      <div class="c-title">Nyawit Music</div>
      <div class="c-sub">Audio Player TV</div>
      <div class="c-btn" style="background:#5AA9E6; color:#000;">BUKA</div>
    </div>
    <div class="comic-card-preview c-card-2">
      <div class="icon-wrap" style="background:#3DDC97; overflow:hidden;">
        <img src="${BRAND_MARK_URI}" style="width:100%;height:100%;object-fit:cover;" />
      </div>
      <div class="c-title" style="color:#3DDC97;">Nyawit Launcher</div>
      <div class="c-sub">Multiplatform Hub</div>
      <div class="c-btn" style="background:#3DDC97; color:#000;">TERBARU v1.4.3</div>
    </div>
    <div class="comic-card-preview c-card-3">
      <div class="icon-wrap" style="background:#FFB454;">
        <svg viewBox="0 0 24 24" width="38" height="38" fill="none" stroke="#000" stroke-width="2.5"><rect x="2" y="6" width="20" height="12" rx="2"/><line x1="6" y1="12" x2="10" y2="12"/><line x1="8" y1="10" x2="8" y2="14"/><circle cx="15" cy="11" r="1.5" fill="#000"/><circle cx="17" cy="13" r="1.5" fill="#000"/></svg>
      </div>
      <div class="c-title">Garis Arcade</div>
      <div class="c-sub">Retro Game</div>
      <div class="c-btn" style="background:#FFB454; color:#000;">PASANG</div>
    </div>
  </div>
</body>
</html>`;
}

// 11. OS Installer Mockups (1920x1080 each)
function getInstallerMockupHtml(osType) {
  const isWin = osType === "windows";
  const isMac = osType === "macos";
  const isLinux = osType === "linux";

  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1920px;
    height: 1080px;
    ${COMIC_BG_EFFECTS}
    color: #F1F5F7;
    ${COMIC_BODY_FONT}
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
    overflow: hidden;
  }
  .desktop-topbar {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 46px;
    background: #0E161F;
    border-bottom: 3px solid #000000;
    display: flex;
    align-items: center;
    padding: 0 28px;
    font-size: 14px;
    font-weight: 800;
    ${COMIC_TITLE_FONT}
    color: #FFE600;
    z-index: 10;
    justify-content: space-between;
  }
  .comic-window {
    z-index: 5;
    width: ${isWin ? "980px" : isMac ? "900px" : "1000px"};
    background: #141C24;
    border: 5px solid #000000;
    border-radius: 28px;
    box-shadow: 16px 16px 0px #000000, 0 0 50px rgba(61, 220, 151, 0.25);
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
  .win-titlebar {
    height: 52px;
    background: #1C2732;
    border-bottom: 3px solid #000000;
    display: flex;
    align-items: center;
    padding: 0 24px;
    justify-content: space-between;
  }
  .mac-traffic-lights {
    display: flex;
    gap: 10px;
  }
  .tl { width: 14px; height: 14px; border-radius: 50%; border: 1.5px solid #000; }
  .tl-red { background: #FF5F56; }
  .tl-yellow { background: #FFBD2E; }
  .tl-green { background: #27C93F; }
  .win-btns { display: flex; gap: 16px; font-size: 20px; font-weight: 900; color: #FFF; }
  .body-content {
    display: flex;
    background: #141C24;
  }
  .win-side {
    width: 340px;
    background: linear-gradient(180deg, #101720 0%, #080C10 100%);
    border-right: 4px solid #000000;
    padding: 44px 28px;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  .win-main {
    flex: 1;
    padding: 44px 40px;
    display: flex;
    flex-direction: column;
  }
  .mac-dmg-flow {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 36px 44px;
  }
  .dmg-drag-row {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 72px;
    margin: 40px 0;
  }
  .dmg-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 14px;
  }
  .dmg-icon-box {
    width: 120px;
    height: 120px;
    border-radius: 28px;
    border: 4px solid #000000;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 8px 8px 0px #000000;
  }
  .arrow-drag {
    font-size: 54px;
    color: #3DDC97;
    font-weight: 900;
    text-shadow: 3px 3px 0 #000;
  }
  .linux-header {
    display: flex;
    align-items: center;
    gap: 28px;
    margin-bottom: 28px;
  }
  .comic-btn-primary {
    background: #3DDC97;
    color: #07130E;
    ${COMIC_TITLE_FONT}
    font-size: 16px;
    letter-spacing: 1px;
    padding: 12px 32px;
    border: 3px solid #000000;
    border-radius: 10px;
    box-shadow: 4px 4px 0px #000000;
    align-self: flex-start;
  }
</style>
</head>
<body>
  <div class="desktop-topbar">
    <div>${isMac ? "🍎 macOS Sonoma &bull; Finder" : isWin ? "Windows 11 x64 Native Setup" : "Ubuntu Linux 24.04 LTS &bull; GNOME Software"}</div>
    <div>NYAWIT LAUNCHER v1.4.3 &bull; RELEASE INSTALLER</div>
  </div>

  <div class="comic-window">
    <div class="win-titlebar">
      ${isMac ? `
        <div class="mac-traffic-lights">
          <div class="tl tl-red"></div>
          <div class="tl tl-yellow"></div>
          <div class="tl tl-green"></div>
        </div>
        <div style="font-weight:900;font-size:15px;color:#FFE600;${COMIC_TITLE_FONT}">Nyawit Launcher &bull; DMG Installer</div>
        <div style="width:40px;"></div>
      ` : `
        <div style="font-weight:900;font-size:15px;color:#FFE600;${COMIC_TITLE_FONT}">${isWin ? "Setup - NYAWIT LAUNCHER" : "Software Center - NYAWIT LAUNCHER"}</div>
        <div class="win-btns"><span>&minus;</span><span>&#9633;</span><span>&times;</span></div>
      `}
    </div>

    ${isWin ? `
      <div class="body-content">
        <div class="win-side">
          <img src="${BRAND_MARK_URI}" style="width:144px;height:144px;border-radius:50%;border:4px solid #000;box-shadow:5px 5px 0 #000;margin-bottom:24px;" />
          <div style="${COMIC_TITLE_FONT}font-size:24px;color:#FFF;line-height:1.1;-webkit-text-stroke:1.5px #000;text-shadow:3px 3px 0 #000;">NYAWIT<br><span style="color:#3DDC97;">LAUNCHER</span></div>
          <div style="background:#FFE600;color:#000;border:2px solid #000;box-shadow:2px 2px 0 #000;padding:4px 10px;font-size:12px;font-weight:900;${COMIC_TITLE_FONT}margin-top:16px;">
            WINDOWS 64-BIT
          </div>
          <div style="margin-top:auto;font-size:12px;color:#3DDC97;font-weight:800;">v1.4.3 Setup (.exe & .msi)</div>
        </div>
        <div class="win-main">
          <div style="${COMIC_TITLE_FONT}font-size:28px;color:#FFF;-webkit-text-stroke:1.5px #000;text-shadow:3px 3px 0 #000;margin-bottom:12px;">Selamat Datang di Wizard Instalasi NYAWIT LAUNCHER</div>
          <div style="font-size:14px;color:#94A3B0;line-height:1.6;margin-bottom:24px;">
            Program ini akan memasang NYAWIT LAUNCHER versi 1.4.3 pada komputer Anda. Didukung untuk Windows 10 dan Windows 11 (64-bit).
            <br><br>
            Format paket: Standalone Executable (.exe) & Windows Installer (.msi) dengan bundling runtime OpenJDK 21.
          </div>
          <div style="background:#0F161E;border:3px solid #000;box-shadow:4px 4px 0 #000;border-radius:12px;padding:16px;margin-bottom:24px;">
            <div style="font-size:12px;color:#94A3B0;font-weight:700;">Lokasi Instalasi:</div>
            <div style="font-size:15px;font-weight:800;color:#3DDC97;margin-top:4px;">C:\\Program Files\\Nyawit Launcher</div>
          </div>
          <div style="margin-top:auto;display:flex;justify-content:flex-end;gap:14px;">
            <div style="background:#1B2630;border:2.5px solid #000;box-shadow:3px 3px 0 #000;padding:10px 22px;border-radius:8px;font-size:13px;font-weight:800;color:#94A3B0;">&lt; Kembali</div>
            <div style="background:#3DDC97;color:#07130E;border:2.5px solid #000;box-shadow:3px 3px 0 #000;padding:10px 26px;border-radius:8px;font-size:13px;font-weight:900;${COMIC_TITLE_FONT}">Pasang &gt;</div>
            <div style="background:#1B2630;border:2.5px solid #000;box-shadow:3px 3px 0 #000;padding:10px 22px;border-radius:8px;font-size:13px;font-weight:800;color:#94A3B0;">Batal</div>
          </div>
        </div>
      </div>
    ` : isMac ? `
      <div class="body-content">
        <div class="mac-dmg-flow">
          <div style="${COMIC_TITLE_FONT}font-size:32px;color:#FFF;-webkit-text-stroke:2px #000;text-shadow:4px 4px 0 #000;">NYAWIT <span style="color:#3DDC97;">LAUNCHER</span> for macOS</div>
          <div style="background:#FFE600;color:#000;border:2.5px solid #000;box-shadow:3px 3px 0 #000;padding:6px 16px;font-size:14px;font-weight:900;margin-top:14px;transform:rotate(-1deg);">
            Tarik Nyawit Launcher ke folder Applications untuk menginstal
          </div>
          <div class="dmg-drag-row">
            <div class="dmg-item">
              <div class="dmg-icon-box" style="background:#141C24;border-color:#3DDC97;">
                <img src="${BRAND_MARK_URI}" style="width:100px;height:100px;object-fit:contain;" />
              </div>
              <div style="font-size:15px;font-weight:900;color:#FFF;${COMIC_TITLE_FONT}">Nyawit Launcher.app</div>
            </div>
            <div class="arrow-drag">&xrarr;</div>
            <div class="dmg-item">
              <div class="dmg-icon-box" style="background:#0F1C2E;border-color:#5AA9E6;">
                <svg viewBox="0 0 24 24" width="64" height="64" fill="#5AA9E6"><path d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/></svg>
              </div>
              <div style="font-size:15px;font-weight:900;color:#FFF;${COMIC_TITLE_FONT}">Applications</div>
            </div>
          </div>
          <div style="background:#0F161E;border:2.5px solid #000;box-shadow:4px 4px 0 #000;border-radius:12px;padding:12px 24px;font-size:13px;color:#3DDC97;font-weight:800;">
            Universal Binary &bull; Apple Silicon (M1/M2/M3) & Intel x86_64 DMG
          </div>
        </div>
      </div>
    ` : `
      <div class="body-content" style="flex-direction:column;padding:44px;">
        <div class="linux-header">
          <img src="${BRAND_MARK_URI}" style="width:116px;height:116px;border-radius:50%;border:4px solid #000;box-shadow:5px 5px 0 #000;" />
          <div>
            <div style="${COMIC_TITLE_FONT}font-size:36px;color:#FFF;-webkit-text-stroke:2px #000;text-shadow:4px 4px 0 #000;">NYAWIT <span style="color:#3DDC97;">LAUNCHER</span></div>
            <div style="font-size:14px;color:#94A3B0;margin-top:4px;">Open-source application and game catalog for Linux & Multiplatform</div>
            <div style="display:flex;gap:12px;margin-top:12px;">
              <span style="background:#FFE600;color:#000;border:2px solid #000;box-shadow:2px 2px 0 #000;padding:4px 10px;border-radius:6px;font-size:12px;font-weight:900;${COMIC_TITLE_FONT}">v1.4.3</span>
              <span style="background:#1B2630;border:2px solid #000;box-shadow:2px 2px 0 #000;padding:4px 10px;border-radius:6px;font-size:12px;color:#94A3B0;font-weight:700;">GPL-3.0 License</span>
              <span style="background:#5AA9E6;color:#000;border:2px solid #000;box-shadow:2px 2px 0 #000;padding:4px 10px;border-radius:6px;font-size:12px;font-weight:900;${COMIC_TITLE_FONT}">Debian (.deb) & RPM (.rpm)</span>
            </div>
          </div>
        </div>
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:24px;">
          <div class="comic-btn-primary">Pasang / Install</div>
          <div style="font-size:14px;color:#3DDC97;font-weight:800;">● SHA-256 Checksum Verified</div>
        </div>
        <div style="background:#0F161E;border:3px solid #000;box-shadow:5px 5px 0 #000;border-radius:14px;padding:20px;font-size:14px;color:#94A3B0;line-height:1.6;">
          Nyawit Launcher dibangun dengan Kotlin 2.0+ dan Compose Multiplatform. Menyediakan katalog aplikasi tanpa ketergantungan server terpusat langsung melalui GitHub REST API publik.
        </div>
      </div>
    `}
  </div>
</body>
</html>`;
}

async function run() {
  console.log("=== Generating All Graphics with True Comic Book & Sawit Critique Style ===");

  const tasks = [
    // 2. assets/app-icons/nyawit-launcher.png (512x512, transparent PNG)
    {
      name: "assets/app-icons/nyawit-launcher.png",
      html: getNyawitLauncherIconHtml(),
      out: path.join(ROOT_DIR, "assets", "app-icons", "nyawit-launcher.png"),
      w: 512,
      h: 512,
      transparent: true
    },
    // 3. play/ic_launcher-512.png (512x512, opaque PNG)
    {
      name: "play/ic_launcher-512.png",
      html: getPlayIconHtml(),
      out: path.join(ROOT_DIR, "play", "ic_launcher-512.png"),
      w: 512,
      h: 512,
      transparent: false
    },
    // 4. assets/logo-new.png (1024x256, transparent PNG)
    {
      name: "assets/logo-new.png",
      html: getLogoNewHtml(),
      out: path.join(ROOT_DIR, "assets", "logo-new.png"),
      w: 1024,
      h: 256,
      transparent: true
    },
    // 5. assets/tv-banner.png (1280x720, PNG)
    {
      name: "assets/tv-banner.png",
      html: getTvBannerHtml(),
      out: path.join(ROOT_DIR, "assets", "tv-banner.png"),
      w: 1280,
      h: 720,
      transparent: false
    },
    // 6. play/feature-graphic.png (1024x500, PNG)
    {
      name: "play/feature-graphic.png",
      html: getFeatureGraphicHtml(),
      out: path.join(ROOT_DIR, "play", "feature-graphic.png"),
      w: 1024,
      h: 500,
      transparent: false
    },
    // 7. docs/images/readme-hero.png (1280x400, PNG)
    {
      name: "docs/images/readme-hero.png",
      html: getReadmeHeroHtml(),
      out: path.join(ROOT_DIR, "docs", "images", "readme-hero.png"),
      w: 1280,
      h: 400,
      transparent: false
    },
    // 8. play/promo-graphic.png (1280x720, PNG)
    {
      name: "play/promo-graphic.png",
      html: getPromoGraphicHtml(),
      out: path.join(ROOT_DIR, "play", "promo-graphic.png"),
      w: 1280,
      h: 720,
      transparent: false
    },
    // 9. docs/images/social-preview.png (1280x640, PNG)
    {
      name: "docs/images/social-preview.png",
      html: getSocialPreviewHtml(),
      out: path.join(ROOT_DIR, "docs", "images", "social-preview.png"),
      w: 1280,
      h: 640,
      transparent: false
    },
    // 10. assets/ui/hero-desktop.png (1600x600, PNG)
    {
      name: "assets/ui/hero-desktop.png",
      html: getHeroDesktopHtml(),
      out: path.join(ROOT_DIR, "assets", "ui", "hero-desktop.png"),
      w: 1600,
      h: 600,
      transparent: false
    },
    // 11. OS Installer Mockups (1920x1080 each)
    {
      name: "docs/images/release-windows.png",
      html: getInstallerMockupHtml("windows"),
      out: path.join(ROOT_DIR, "docs", "images", "release-windows.png"),
      w: 1920,
      h: 1080,
      transparent: false
    },
    {
      name: "docs/images/release-linux.png",
      html: getInstallerMockupHtml("linux"),
      out: path.join(ROOT_DIR, "docs", "images", "release-linux.png"),
      w: 1920,
      h: 1080,
      transparent: false
    },
    {
      name: "docs/images/release-macos.png",
      html: getInstallerMockupHtml("macos"),
      out: path.join(ROOT_DIR, "docs", "images", "release-macos.png"),
      w: 1920,
      h: 1080,
      transparent: false
    }
  ];

  for (const t of tasks) {
    try {
      const res = await renderHtmlToPng(t.html, t.out, t.w, t.h, t.transparent);
      console.log(`[OK] Generated ${t.name} (${res.width}x${res.height}, ${Math.round(res.size / 1024)} KB)`);
    } catch (e) {
      console.error(`[FAIL] ${t.name}:`, e);
    }
  }

  // Synchronize TV Banner to Android App res
  const tvSrc = path.join(ROOT_DIR, "assets", "tv-banner.png");
  const tvDest = path.join(ROOT_DIR, "androidApp", "src", "main", "res", "drawable-nodpi", "tv_banner.png");
  fs.copyFileSync(tvSrc, tvDest);
  console.log(`[OK] Synchronized tv_banner.png to androidApp/src/main/res/drawable-nodpi/tv_banner.png`);
}

run();
