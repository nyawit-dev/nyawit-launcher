import fs from "fs";
import path from "path";
import { renderHtmlToPng, ROOT_DIR, BRAND_MARK_URI } from "./render_helper.js";

// AI comic illustration showing burning palm oil plantation and environmental destruction
const AI_COMIC_PALM_PATH = path.join(ROOT_DIR, "assets", "ai-comic-palm.jpg");
const AI_COMIC_PALM_URI = fs.existsSync(AI_COMIC_PALM_PATH)
  ? "data:image/jpeg;base64," + fs.readFileSync(AI_COMIC_PALM_PATH).toString("base64")
  : BRAND_MARK_URI;

// Master Comic CSS Tokens
const COMIC_CORE_CSS = `
  @import url('https://fonts.googleapis.com/css2?family=Bangers&family=Permanent+Marker&family=Impact&display=swap');
  
  :root {
    --comic-navy: #0B1118;
    --comic-dark: #121A24;
    --comic-emerald: #3DDC97;
    --comic-yellow: #FFE600;
    --comic-orange: #FF6B00;
    --comic-red: #FF3344;
    --comic-black: #000000;
    --comic-white: #FFFFFF;
  }

  /* Authentic Comic Typography */
  .comic-title {
    font-family: "Impact", "Bangers", "Arial Black", sans-serif;
    text-transform: uppercase;
    letter-spacing: 2px;
    line-height: 1;
    -webkit-text-stroke: 3px #000000;
    text-shadow: 4px 4px 0px #000000, 7px 7px 0px rgba(0, 0, 0, 0.9);
  }

  .comic-title-large {
    font-family: "Impact", "Bangers", "Arial Black", sans-serif;
    text-transform: uppercase;
    letter-spacing: 3px;
    line-height: 0.95;
    -webkit-text-stroke: 4px #000000;
    text-shadow: 6px 6px 0px #000000, 10px 10px 0px rgba(0, 0, 0, 0.9);
  }

  /* Comic Narration Box (Classic Yellow) */
  .comic-caption {
    background: #FFE600;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 5px 5px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-weight: 900;
    text-transform: uppercase;
    letter-spacing: 1.5px;
    padding: 6px 14px;
    display: inline-block;
    transform: rotate(-1.5deg);
  }

  /* Comic Starburst Action Badge */
  .comic-starburst {
    background: #FF3344;
    color: #FFFFFF;
    border: 4px solid #000000;
    box-shadow: 5px 5px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-weight: 900;
    text-transform: uppercase;
    padding: 6px 14px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    transform: rotate(3deg);
  }

  /* Comic Warning Hazard Stripe Ribbon */
  .comic-hazard-tape {
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 14px,
      #000000 14px,
      #000000 28px
    );
    border-top: 3px solid #000000;
    border-bottom: 3px solid #000000;
    height: 18px;
    width: 100%;
    box-shadow: 0 4px 0 #000000;
  }

  /* Ben-Day Halftone Overlay */
  .comic-halftone-dots {
    background-image: radial-gradient(#3DDC97 22%, transparent 22%), radial-gradient(rgba(255, 230, 0, 0.3) 18%, transparent 18%);
    background-size: 16px 16px, 32px 32px;
    background-position: 0 0, 8px 8px;
  }

  /* Dramatic Comic Action Sunburst Rays */
  .comic-action-rays {
    background-image: 
      repeating-conic-gradient(
        from 0deg,
        rgba(61, 220, 151, 0.22) 0deg 12deg,
        transparent 12deg 24deg
      ),
      repeating-conic-gradient(
        from 6deg,
        rgba(255, 107, 0, 0.18) 0deg 8deg,
        transparent 8deg 24deg
      );
  }
`;

// ============================================================================
// 1. assets/app-icon.png: 1024x1024, PNG transparan
// Emblem komik circular, tanpa teks sama sekali, center, padding rata
// (Disalin langsung dari master artwork commit c6f8302 untuk menjaga integritas)
// ============================================================================

// ============================================================================
// 2. assets/app-icons/nyawit-launcher.png: 512x512, PNG transparan
// Emblem yang sama, aksen emerald, tanpa teks
// ============================================================================
function getNyawitLauncherIconHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 512px;
    height: 512px;
    background: transparent;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .comic-app-tile {
    width: 440px;
    height: 440px;
    background: #0D141C;
    border: 8px solid #000000;
    border-radius: 96px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 12px 12px 0px #000000, 0 0 35px rgba(61, 220, 151, 0.45);
    position: relative;
    overflow: hidden;
  }
  .comic-sunburst-bg {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 0deg,
      rgba(61, 220, 151, 0.35) 0deg 15deg,
      rgba(13, 20, 28, 0.95) 15deg 30deg
    );
  }
  .comic-dots-layer {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(rgba(61, 220, 151, 0.4) 22%, transparent 22%);
    background-size: 16px 16px;
  }
  .inner-emerald-frame {
    position: absolute;
    top: 12px; left: 12px; right: 12px; bottom: 12px;
    border: 5px solid #3DDC97;
    border-radius: 80px;
    pointer-events: none;
    z-index: 1;
  }
  .emblem-img {
    width: 340px;
    height: 340px;
    object-fit: contain;
    position: relative;
    z-index: 2;
    filter: drop-shadow(8px 8px 0px #000000);
  }
</style>
</head>
<body>
  <div class="comic-app-tile">
    <div class="comic-sunburst-bg"></div>
    <div class="comic-dots-layer"></div>
    <div class="inner-emerald-frame"></div>
    <img class="emblem-img" src="${BRAND_MARK_URI}" alt="Nyawit Comic Emblem" />
  </div>
</body>
</html>`;
}

// ============================================================================
// 3. play/ic_launcher-512.png: 512x512, PNG (opaque)
// Emblem di atas rounded-square navy, lingkaran emerald di belakang, tanpa teks
// ============================================================================
function getPlayIconHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 512px;
    height: 512px;
    background: #070B10;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .comic-navy-card {
    width: 456px;
    height: 456px;
    background: #0E1620;
    border: 8px solid #000000;
    border-radius: 96px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 12px 12px 0px #000000, 0 0 40px rgba(61, 220, 151, 0.4);
    position: relative;
    overflow: hidden;
  }
  .comic-action-bg {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: 
      repeating-conic-gradient(from 0deg, rgba(61, 220, 151, 0.2) 0deg 15deg, transparent 15deg 30deg),
      radial-gradient(rgba(255, 107, 0, 0.25) 20%, transparent 20%);
    background-size: 100% 100%, 20px 20px;
  }
  .emerald-comic-disc {
    position: absolute;
    width: 320px;
    height: 320px;
    border-radius: 50%;
    background: repeating-conic-gradient(from 0deg, #3DDC97 0deg 18deg, #20A66D 18deg 36deg);
    border: 6px solid #000000;
    box-shadow: 8px 8px 0px #000000, 0 0 30px rgba(61, 220, 151, 0.6);
  }
  .emblem-img {
    width: 300px;
    height: 300px;
    object-fit: contain;
    position: relative;
    z-index: 2;
    filter: drop-shadow(8px 8px 0px #000000);
  }
</style>
</head>
<body>
  <div class="comic-navy-card">
    <div class="comic-action-bg"></div>
    <div class="emerald-comic-disc"></div>
    <img class="emblem-img" src="${BRAND_MARK_URI}" alt="Nyawit Play Icon" />
  </div>
</body>
</html>`;
}

// ============================================================================
// 4. assets/logo-new.png: 1024x256, PNG transparan
// Emblem di kotak navy bulat di kiri; teks NYAWIT (putih) + LAUNCHER (emerald) + tagline ANDROID • ANDROID TV • DESKTOP
// ============================================================================
function getLogoNewHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1024px;
    height: 256px;
    background: transparent;
    overflow: hidden;
    display: flex;
    align-items: center;
    padding-left: 36px;
  }
  .comic-emblem-box {
    width: 180px;
    height: 180px;
    background: #0E1620;
    border: 6px solid #000000;
    border-radius: 46px;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 8px 8px 0px #000000, 0 0 24px rgba(61, 220, 151, 0.4);
    flex-shrink: 0;
    position: relative;
    overflow: hidden;
  }
  .comic-emblem-box::before {
    content: "";
    position: absolute;
    width: 100%; height: 100%;
    background-image: repeating-conic-gradient(from 0deg, rgba(61, 220, 151, 0.35) 0deg 20deg, transparent 20deg 40deg);
  }
  .comic-emblem-box img {
    width: 150px;
    height: 150px;
    object-fit: contain;
    position: relative;
    z-index: 2;
    filter: drop-shadow(5px 5px 0px #000000);
  }
  .logo-text-block {
    margin-left: 36px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .brand-row {
    display: flex;
    align-items: baseline;
    gap: 18px;
    transform: skew(-5deg, -1deg);
  }
  .brand-nyawit {
    font-size: 84px;
    color: #FFFFFF;
  }
  .brand-launcher {
    font-size: 84px;
    color: #3DDC97;
  }
  .comic-tagline {
    margin-top: 10px;
    align-self: flex-start;
    background: #FFE600;
    color: #000000;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    padding: 6px 16px;
    font-size: 15px;
    font-weight: 900;
    letter-spacing: 3px;
    font-family: "Impact", "Arial Black", sans-serif;
    transform: rotate(-1.5deg);
  }
</style>
</head>
<body>
  <div class="comic-emblem-box">
    <img src="${BRAND_MARK_URI}" alt="Comic Emblem" />
  </div>
  <div class="logo-text-block">
    <div class="brand-row">
      <span class="comic-title brand-nyawit">NYAWIT</span>
      <span class="comic-title brand-launcher">LAUNCHER</span>
    </div>
    <div class="comic-tagline">
      ANDROID &bull; ANDROID TV &bull; DESKTOP
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// 5. assets/tv-banner.png + androidApp/.../tv_banner.png: 1280x720, PNG
// Background navy, frame emerald, emblem kiri ~440px, teks EDISI TV & ANDROID, NYAWIT LAUNCHER, Katalog Aplikasi & Modul Open-Source
// ============================================================================
function getTvBannerHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 720px;
    background: #070B10;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
  }
  /* AI Comic Background Layer: Palm Environmental Disaster */
  .ai-disaster-backdrop {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0; left: 0;
    background-image: url("${AI_COMIC_PALM_URI}");
    background-size: cover;
    background-position: center 35%;
    opacity: 0.32;
    filter: saturate(1.8) contrast(1.4);
    mix-blend-mode: screen;
  }
  .comic-speed-rays {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 0deg,
      rgba(61, 220, 151, 0.25) 0deg 10deg,
      rgba(11, 17, 24, 0.85) 10deg 20deg
    );
  }
  .comic-dots-pattern {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(rgba(255, 230, 0, 0.25) 20%, transparent 20%);
    background-size: 20px 20px;
  }
  .comic-tv-frame {
    width: 1220px;
    height: 660px;
    background: rgba(11, 17, 24, 0.92);
    border: 7px solid #3DDC97;
    border-radius: 28px;
    display: flex;
    align-items: center;
    padding: 0 54px;
    position: relative;
    box-shadow: 14px 14px 0px #000000, 0 0 50px rgba(61, 220, 151, 0.4);
    z-index: 2;
  }
  .emblem-container {
    width: 440px;
    height: 440px;
    border-radius: 50%;
    overflow: hidden;
    flex-shrink: 0;
    border: 8px solid #000000;
    background: #0E1620;
    box-shadow: 12px 12px 0px #000000, 0 0 40px rgba(61, 220, 151, 0.5);
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .emblem-sunburst {
    position: absolute;
    width: 100%; height: 100%;
    background-image: repeating-conic-gradient(
      from 0deg,
      #3DDC97 0deg 15deg,
      #13221C 15deg 30deg
    );
    opacity: 0.6;
  }
  .emblem-img {
    width: 410px;
    height: 410px;
    object-fit: contain;
    position: relative;
    z-index: 3;
    filter: drop-shadow(8px 8px 0px #000000);
  }
  .tv-text-column {
    margin-left: 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex: 1;
    z-index: 3;
  }
  .comic-badge-row {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;
  }
  .tv-badge {
    background: #FFE600;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 5px 5px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 24px;
    font-weight: 900;
    letter-spacing: 3px;
    padding: 6px 18px;
    transform: rotate(-2deg);
  }
  .warning-badge {
    background: #FF3344;
    color: #FFFFFF;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 16px;
    font-weight: 900;
    letter-spacing: 2px;
    padding: 5px 14px;
    transform: rotate(2deg);
  }
  .tv-title {
    font-size: 88px;
    transform: skew(-5deg, -1deg);
    margin-bottom: 20px;
  }
  .tv-title .white-part { color: #FFFFFF; }
  .tv-title .emerald-part { color: #3DDC97; }
  .tv-subtext-box {
    background: #121A24;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    padding: 14px 22px;
    color: #E2E8F0;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 26px;
    letter-spacing: 2px;
    text-transform: uppercase;
    align-self: flex-start;
    display: flex;
    align-items: center;
    gap: 12px;
    border-left: 10px solid #3DDC97;
  }
  .caution-strip {
    position: absolute;
    bottom: -7px;
    left: 20px;
    right: 20px;
    height: 14px;
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 12px,
      #000000 12px,
      #000000 24px
    );
    border: 2px solid #000000;
    border-radius: 6px;
  }
</style>
</head>
<body>
  <div class="ai-disaster-backdrop"></div>
  <div class="comic-speed-rays"></div>
  <div class="comic-dots-pattern"></div>
  
  <div class="comic-tv-frame">
    <div class="caution-strip"></div>
    <div class="emblem-container">
      <div class="emblem-sunburst"></div>
      <img class="emblem-img" src="${BRAND_MARK_URI}" alt="Comic Emblem" />
    </div>
    <div class="tv-text-column">
      <div class="comic-badge-row">
        <div class="tv-badge">EDISI TV & ANDROID</div>
        <div class="warning-badge">&#9888; ANTI-MONOPOLI SAWIT</div>
      </div>
      <div class="comic-title-large tv-title">
        <span class="white-part">NYAWIT</span> <span class="emerald-part">LAUNCHER</span>
      </div>
      <div class="tv-subtext-box">
        Katalog Aplikasi & Modul Open-Source
      </div>
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// 6. play/feature-graphic.png: 1024x500, PNG
// Teks NYAWIT LAUNCHER, tagline "Katalog aplikasi dan game multiplatform yang cepat, ringan, dan transparan langsung dari repositori GitHub.", chip platform, mockup kartu
// ============================================================================
function getFeatureGraphicHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1024px;
    height: 500px;
    background: #070B10;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
  }
  .ai-disaster-backdrop {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: url("${AI_COMIC_PALM_URI}");
    background-size: cover;
    background-position: right 30%;
    opacity: 0.38;
    filter: saturate(1.8) contrast(1.3);
  }
  .comic-sunburst {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 45deg,
      rgba(61, 220, 151, 0.28) 0deg 12deg,
      rgba(7, 11, 16, 0.8) 12deg 24deg
    );
  }
  .comic-dots {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(#3DDC97 20%, transparent 20%);
    background-size: 18px 18px;
    opacity: 0.5;
  }
  .hazard-bar {
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 12px;
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 12px,
      #000000 12px,
      #000000 24px
    );
    border-bottom: 3px solid #000000;
    z-index: 10;
  }
  .content-wrapper {
    position: relative;
    z-index: 5;
    width: 100%;
    height: 100%;
    display: flex;
    padding: 30px 40px;
  }
  .left-side {
    width: 580px;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .badge-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
  }
  .starburst-badge {
    background: #FF3344;
    color: #FFFFFF;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 15px;
    padding: 4px 12px;
    transform: rotate(-2deg);
  }
  .open-badge {
    background: #FFE600;
    color: #000000;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 15px;
    padding: 4px 12px;
    transform: rotate(1deg);
  }
  .main-title {
    font-size: 64px;
    transform: skew(-5deg, -1deg);
    margin-bottom: 14px;
  }
  .main-title .white-text { color: #FFFFFF; }
  .main-title .emerald-text { color: #3DDC97; }
  .comic-speech-tagline {
    background: #FFE600;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    padding: 12px 18px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 17px;
    line-height: 1.25;
    letter-spacing: 0.5px;
    text-transform: uppercase;
    margin-bottom: 18px;
    transform: rotate(-1deg);
  }
  .platform-chips-row {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  .chip-pill {
    background: #121A24;
    color: #3DDC97;
    border: 3px solid #000000;
    box-shadow: 3px 3px 0px #000000;
    padding: 5px 12px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 13px;
    letter-spacing: 1px;
    text-transform: uppercase;
  }
  .right-side {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
  }
  /* Comic Phone & TV Mockup Panels */
  .comic-card-panel {
    width: 220px;
    height: 340px;
    background: #0E1620;
    border: 6px solid #000000;
    border-radius: 24px;
    box-shadow: 10px 10px 0px #000000, 0 0 30px rgba(61, 220, 151, 0.4);
    position: relative;
    overflow: hidden;
    transform: rotate(4deg);
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 16px;
  }
  .comic-card-panel-tv {
    position: absolute;
    width: 200px;
    height: 150px;
    background: #151F2C;
    border: 5px solid #3DDC97;
    border-radius: 18px;
    box-shadow: 8px 8px 0px #000000;
    left: -20px;
    bottom: 30px;
    transform: rotate(-6deg);
    overflow: hidden;
    padding: 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  .card-emblem-preview {
    width: 140px;
    height: 140px;
    border-radius: 50%;
    border: 4px solid #000000;
    background: #000000;
    margin-top: 10px;
    box-shadow: 4px 4px 0px #000000;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
  }
  .card-emblem-preview img {
    width: 130px; height: 130px; object-fit: contain;
  }
  .card-app-title {
    margin-top: 16px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 20px;
    color: #3DDC97;
    letter-spacing: 1px;
    text-transform: uppercase;
    -webkit-text-stroke: 1.5px #000;
    text-shadow: 2px 2px 0 #000;
  }
  .card-app-btn {
    margin-top: 16px;
    background: #FFE600;
    color: #000000;
    border: 3px solid #000000;
    box-shadow: 3px 3px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 15px;
    padding: 6px 20px;
    text-transform: uppercase;
  }
</style>
</head>
<body>
  <div class="hazard-bar"></div>
  <div class="ai-disaster-backdrop"></div>
  <div class="comic-sunburst"></div>
  <div class="comic-dots"></div>

  <div class="content-wrapper">
    <div class="left-side">
      <div class="badge-row">
        <div class="starburst-badge">&#9733; 100% OPEN SOURCE</div>
        <div class="open-badge">DIRECT GITHUB API</div>
      </div>
      <div class="comic-title-large main-title">
        <span class="white-text">NYAWIT</span> <span class="emerald-text">LAUNCHER</span>
      </div>
      <div class="comic-speech-tagline">
        Katalog aplikasi dan game multiplatform yang cepat, ringan, dan transparan langsung dari repositori GitHub.
      </div>
      <div class="platform-chips-row">
        <div class="chip-pill">Android</div>
        <div class="chip-pill">Android TV</div>
        <div class="chip-pill">Windows</div>
        <div class="chip-pill">macOS</div>
        <div class="chip-pill">Linux</div>
      </div>
    </div>

    <div class="right-side">
      <div class="comic-card-panel">
        <div class="card-emblem-preview">
          <img src="${BRAND_MARK_URI}" alt="App" />
        </div>
        <div class="card-app-title">NYAWIT APP</div>
        <div class="card-app-btn">INSTALL &bull; GRATIS</div>
      </div>
      <div class="comic-card-panel-tv">
        <div style="font-family: 'Impact'; color: #FFE600; font-size: 14px;">LEANBACK TV FOCUS</div>
        <div style="font-family: 'Impact'; color: #FFF; font-size: 18px; -webkit-text-stroke: 1px #000;">ANDROID TV READY</div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// 7. docs/images/readme-hero.png: 1280x400, PNG
// Emblem + NYAWIT LAUNCHER, chip: Kotlin 2.0+, Compose Multiplatform, Direct GitHub API, Leanback TV Focus, SHA-256 Checksum
// ============================================================================
function getReadmeHeroHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 400px;
    background: #070B10;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 48px;
  }
  .ai-disaster-backdrop {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: url("${AI_COMIC_PALM_URI}");
    background-size: cover;
    background-position: center 30%;
    opacity: 0.35;
    filter: saturate(1.8) contrast(1.4);
  }
  .comic-rays {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 0deg,
      rgba(61, 220, 151, 0.22) 0deg 12deg,
      rgba(7, 11, 16, 0.8) 12deg 24deg
    );
  }
  .comic-dots {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(rgba(255, 230, 0, 0.22) 20%, transparent 20%);
    background-size: 18px 18px;
  }
  .comic-frame-box {
    position: relative;
    z-index: 5;
    width: 100%;
    height: 340px;
    background: rgba(11, 17, 24, 0.94);
    border: 6px solid #3DDC97;
    border-radius: 24px;
    display: flex;
    align-items: center;
    padding: 0 40px;
    box-shadow: 12px 12px 0px #000000, 0 0 40px rgba(61, 220, 151, 0.4);
  }
  .emblem-tile {
    width: 250px;
    height: 250px;
    background: #0E1620;
    border: 7px solid #000000;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 10px 10px 0px #000000, 0 0 30px rgba(61, 220, 151, 0.5);
    flex-shrink: 0;
    overflow: hidden;
    position: relative;
  }
  .emblem-tile::before {
    content: "";
    position: absolute;
    width: 100%; height: 100%;
    background-image: repeating-conic-gradient(from 0deg, #3DDC97 0deg 15deg, #13221C 15deg 30deg);
    opacity: 0.6;
  }
  .emblem-tile img {
    width: 235px; height: 235px; object-fit: contain;
    position: relative; z-index: 2;
    filter: drop-shadow(6px 6px 0px #000000);
  }
  .content-col {
    margin-left: 44px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .top-comic-banner {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 10px;
  }
  .issue-tag {
    background: #FF3344;
    color: #FFFFFF;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 15px;
    padding: 4px 12px;
    transform: rotate(-2deg);
  }
  .hero-title {
    font-size: 74px;
    transform: skew(-5deg, -1deg);
    margin-bottom: 16px;
  }
  .hero-title .white-text { color: #FFFFFF; }
  .hero-title .emerald-text { color: #3DDC97; }
  .chips-container {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }
  .tech-chip {
    background: #FFE600;
    color: #000000;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    padding: 7px 15px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 15px;
    letter-spacing: 1px;
    text-transform: uppercase;
    transform: rotate(-1deg);
  }
  .tech-chip:nth-child(even) {
    background: #121A24;
    color: #3DDC97;
    transform: rotate(1deg);
  }
</style>
</head>
<body>
  <div class="ai-disaster-backdrop"></div>
  <div class="comic-rays"></div>
  <div class="comic-dots"></div>

  <div class="comic-frame-box">
    <div class="emblem-tile">
      <img src="${BRAND_MARK_URI}" alt="Comic Sawit Emblem" />
    </div>
    <div class="content-col">
      <div class="top-comic-banner">
        <div class="issue-tag">&#9888; CRITIQUE MONOPOLI SAWIT</div>
        <div style="font-family: 'Impact'; color: #FFE600; font-size: 16px; letter-spacing: 2px;">
          ECOSYSTEM REPO V1.4.3
        </div>
      </div>
      <div class="comic-title-large hero-title">
        <span class="white-text">NYAWIT</span> <span class="emerald-text">LAUNCHER</span>
      </div>
      <div class="chips-container">
        <div class="tech-chip">Kotlin 2.0+</div>
        <div class="tech-chip">Compose Multiplatform</div>
        <div class="tech-chip">Direct GitHub API</div>
        <div class="tech-chip">Leanback TV Focus</div>
        <div class="tech-chip">SHA-256 Checksum</div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// 8. play/promo-graphic.png: 1280x720, PNG
// NYAWIT LAUNCHER + tagline + emblem + platform
// ============================================================================
function getPromoGraphicHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 720px;
    background: #070B10;
    overflow: hidden;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
  }
  .ai-disaster-backdrop {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: url("${AI_COMIC_PALM_URI}");
    background-size: cover;
    background-position: center center;
    opacity: 0.42;
    filter: saturate(1.8) contrast(1.4);
  }
  .comic-sunburst {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 0deg,
      rgba(61, 220, 151, 0.3) 0deg 12deg,
      rgba(7, 11, 16, 0.85) 12deg 24deg
    );
  }
  .comic-dots {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(rgba(255, 107, 0, 0.3) 22%, transparent 22%);
    background-size: 20px 20px;
  }
  .hazard-tape-top {
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 16px;
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 14px,
      #000000 14px,
      #000000 28px
    );
    border-bottom: 3px solid #000;
    z-index: 10;
  }
  .hazard-tape-bottom {
    position: absolute;
    bottom: 0; left: 0; right: 0;
    height: 16px;
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 14px,
      #000000 14px,
      #000000 28px
    );
    border-top: 3px solid #000;
    z-index: 10;
  }
  .promo-center-box {
    position: relative;
    z-index: 5;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .issue-header {
    background: #FF3344;
    color: #FFFFFF;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 20px;
    padding: 6px 24px;
    letter-spacing: 3px;
    text-transform: uppercase;
    transform: rotate(-2deg);
    margin-bottom: 16px;
  }
  .emblem-ring {
    width: 250px;
    height: 250px;
    border-radius: 50%;
    background: #0E1620;
    border: 8px solid #000000;
    box-shadow: 12px 12px 0px #000000, 0 0 50px rgba(61, 220, 151, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
    margin-bottom: 18px;
  }
  .emblem-ring::before {
    content: "";
    position: absolute;
    width: 100%; height: 100%;
    background-image: repeating-conic-gradient(from 0deg, #3DDC97 0deg 18deg, #13221C 18deg 36deg);
    opacity: 0.6;
  }
  .emblem-ring img {
    width: 235px; height: 235px; object-fit: contain;
    position: relative; z-index: 2;
    filter: drop-shadow(8px 8px 0px #000000);
  }
  .promo-title {
    font-size: 92px;
    transform: skew(-6deg, -1deg);
    margin-bottom: 16px;
  }
  .promo-title .white-text { color: #FFFFFF; }
  .promo-title .emerald-text { color: #3DDC97; }
  .tagline-box {
    background: #FFE600;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    padding: 12px 30px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 24px;
    letter-spacing: 1.5px;
    text-transform: uppercase;
    transform: rotate(1deg);
    margin-bottom: 22px;
  }
  .platform-bar {
    display: flex;
    gap: 14px;
  }
  .platform-chip {
    background: #121A24;
    color: #3DDC97;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    padding: 8px 22px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 18px;
    letter-spacing: 2px;
    text-transform: uppercase;
  }
</style>
</head>
<body>
  <div class="hazard-tape-top"></div>
  <div class="hazard-tape-bottom"></div>
  <div class="ai-disaster-backdrop"></div>
  <div class="comic-sunburst"></div>
  <div class="comic-dots"></div>

  <div class="promo-center-box">
    <div class="issue-header">&#9733; MULTIPLATFORM OPEN-SOURCE DISTRIBUTION &#9733;</div>
    <div class="emblem-ring">
      <img src="${BRAND_MARK_URI}" alt="Comic Emblem" />
    </div>
    <div class="comic-title-large promo-title">
      <span class="white-text">NYAWIT</span> <span class="emerald-text">LAUNCHER</span>
    </div>
    <div class="tagline-box">
      Platform distribusi aplikasi multiplatform bebas server berbasis GitHub
    </div>
    <div class="platform-bar">
      <div class="platform-chip">Android</div>
      <div class="platform-chip">Android TV</div>
      <div class="platform-chip">Windows</div>
      <div class="platform-chip">macOS</div>
      <div class="platform-chip">Linux</div>
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// 9. docs/images/social-preview.png: 1280x640, PNG
// NYAWIT LAUNCHER, emblem, tagline multiplatform
// ============================================================================
function getSocialPreviewHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1280px;
    height: 640px;
    background: #070B10;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 54px;
  }
  .ai-disaster-backdrop {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: url("${AI_COMIC_PALM_URI}");
    background-size: cover;
    background-position: center 35%;
    opacity: 0.38;
    filter: saturate(1.8) contrast(1.4);
  }
  .comic-rays {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 20deg,
      rgba(61, 220, 151, 0.25) 0deg 12deg,
      rgba(7, 11, 16, 0.85) 12deg 24deg
    );
  }
  .comic-dots {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(#3DDC97 22%, transparent 22%);
    background-size: 20px 20px;
    opacity: 0.4;
  }
  .hazard-tape {
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 14px;
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 12px,
      #000000 12px,
      #000000 24px
    );
    border-bottom: 3px solid #000;
  }
  .comic-card-frame {
    position: relative;
    z-index: 5;
    width: 100%;
    height: 550px;
    background: rgba(11, 17, 24, 0.94);
    border: 7px solid #3DDC97;
    border-radius: 28px;
    display: flex;
    align-items: center;
    padding: 0 48px;
    box-shadow: 14px 14px 0px #000000, 0 0 50px rgba(61, 220, 151, 0.4);
  }
  .emblem-column {
    width: 360px;
    height: 360px;
    background: #0E1620;
    border: 8px solid #000000;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 12px 12px 0px #000000, 0 0 40px rgba(61, 220, 151, 0.5);
    flex-shrink: 0;
    overflow: hidden;
    position: relative;
  }
  .emblem-column::before {
    content: "";
    position: absolute;
    width: 100%; height: 100%;
    background-image: repeating-conic-gradient(from 0deg, #3DDC97 0deg 18deg, #13221C 18deg 36deg);
    opacity: 0.6;
  }
  .emblem-column img {
    width: 340px; height: 340px; object-fit: contain;
    position: relative; z-index: 2;
    filter: drop-shadow(8px 8px 0px #000000);
  }
  .text-column {
    margin-left: 50px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .badge-row {
    display: flex;
    align-items: center;
    gap: 14px;
    margin-bottom: 12px;
  }
  .badge-issue {
    background: #FF3344;
    color: #FFFFFF;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 16px;
    padding: 5px 14px;
    transform: rotate(-2deg);
  }
  .social-title {
    font-size: 82px;
    transform: skew(-5deg, -1deg);
    margin-bottom: 16px;
  }
  .social-title .white-text { color: #FFFFFF; }
  .social-title .emerald-text { color: #3DDC97; }
  .tagline-box {
    background: #FFE600;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    padding: 12px 22px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 22px;
    line-height: 1.25;
    letter-spacing: 1px;
    text-transform: uppercase;
    transform: rotate(1deg);
    margin-bottom: 20px;
  }
  .platform-chips {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }
  .chip {
    background: #121A24;
    color: #3DDC97;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    padding: 6px 16px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 16px;
    letter-spacing: 1.5px;
    text-transform: uppercase;
  }
</style>
</head>
<body>
  <div class="hazard-tape"></div>
  <div class="ai-disaster-backdrop"></div>
  <div class="comic-rays"></div>
  <div class="comic-dots"></div>

  <div class="comic-card-frame">
    <div class="emblem-column">
      <img src="${BRAND_MARK_URI}" alt="Comic Emblem" />
    </div>
    <div class="text-column">
      <div class="badge-row">
        <div class="badge-issue">&#9888; CRITIQUE: DAMPAK BURUK SAWIT</div>
        <div style="font-family: 'Impact'; color: #FFE600; font-size: 17px; letter-spacing: 2px;">
          OPEN SOURCE ECOSYSTEM
        </div>
      </div>
      <div class="comic-title-large social-title">
        <span class="white-text">NYAWIT</span> <span class="emerald-text">LAUNCHER</span>
      </div>
      <div class="tagline-box">
        Katalog & Peluncur Multiplatform Open-Source Bebas Server Berbasis GitHub
      </div>
      <div class="platform-chips">
        <div class="chip">Android</div>
        <div class="chip">Android TV</div>
        <div class="chip">Windows</div>
        <div class="chip">macOS</div>
        <div class="chip">Linux</div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// 10. assets/ui/hero-desktop.png: 1600x600, PNG
// Hero desktop, kartu aplikasi, brand NYAWIT
// ============================================================================
function getHeroDesktopHtml() {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1600px;
    height: 600px;
    background: #070B10;
    overflow: hidden;
    position: relative;
    display: flex;
    align-items: center;
    padding: 0 60px;
  }
  .ai-disaster-backdrop {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: url("${AI_COMIC_PALM_URI}");
    background-size: cover;
    background-position: right 40%;
    opacity: 0.38;
    filter: saturate(1.8) contrast(1.4);
  }
  .comic-sunburst {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 10deg,
      rgba(61, 220, 151, 0.25) 0deg 10deg,
      rgba(7, 11, 16, 0.8) 10deg 20deg
    );
  }
  .comic-dots {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(rgba(255, 107, 0, 0.25) 20%, transparent 20%);
    background-size: 20px 20px;
  }
  .hazard-bar {
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 14px;
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 12px,
      #000000 12px,
      #000000 24px
    );
    border-bottom: 3px solid #000;
  }
  .hero-container {
    position: relative;
    z-index: 5;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .left-content {
    width: 820px;
    display: flex;
    flex-direction: column;
  }
  .top-label-row {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 12px;
  }
  .label-critique {
    background: #FF3344;
    color: #FFFFFF;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 16px;
    padding: 5px 16px;
    transform: rotate(-2deg);
  }
  .hero-title {
    font-size: 92px;
    transform: skew(-5deg, -1deg);
    margin-bottom: 16px;
  }
  .hero-title .white-text { color: #FFFFFF; }
  .hero-title .emerald-text { color: #3DDC97; }
  .narration-box {
    background: #FFE600;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    padding: 12px 24px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 24px;
    letter-spacing: 1.5px;
    text-transform: uppercase;
    transform: rotate(-1deg);
    margin-bottom: 22px;
    align-self: flex-start;
  }
  .chips-row {
    display: flex;
    gap: 12px;
  }
  .feature-pill {
    background: #121A24;
    color: #3DDC97;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    padding: 8px 18px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 16px;
    letter-spacing: 1.5px;
    text-transform: uppercase;
  }
  .right-cards-cluster {
    position: relative;
    width: 600px;
    height: 480px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .comic-card {
    position: absolute;
    width: 220px;
    height: 320px;
    background: #0E1620;
    border: 6px solid #000000;
    border-radius: 24px;
    box-shadow: 10px 10px 0px #000000, 0 0 30px rgba(61, 220, 151, 0.4);
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 18px;
    overflow: hidden;
  }
  .card-1 {
    transform: rotate(-10deg) translate(-140px, -20px);
    z-index: 2;
  }
  .card-2 {
    transform: rotate(4deg) translate(0px, 10px);
    z-index: 4;
    border-color: #3DDC97;
  }
  .card-3 {
    transform: rotate(14deg) translate(140px, -10px);
    z-index: 3;
  }
  .card-icon-circle {
    width: 120px;
    height: 120px;
    border-radius: 50%;
    border: 4px solid #000000;
    background: #000000;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    margin-top: 10px;
    box-shadow: 4px 4px 0px #000000;
  }
  .card-icon-circle img {
    width: 110px; height: 110px; object-fit: contain;
  }
  .card-name {
    margin-top: 18px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 20px;
    color: #3DDC97;
    letter-spacing: 1px;
    text-transform: uppercase;
    -webkit-text-stroke: 1.5px #000;
    text-shadow: 2px 2px 0 #000;
  }
  .card-btn {
    margin-top: 16px;
    background: #FFE600;
    color: #000000;
    border: 3px solid #000000;
    box-shadow: 3px 3px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 14px;
    padding: 6px 18px;
    text-transform: uppercase;
  }
</style>
</head>
<body>
  <div class="hazard-bar"></div>
  <div class="ai-disaster-backdrop"></div>
  <div class="comic-sunburst"></div>
  <div class="comic-dots"></div>

  <div class="hero-container">
    <div class="left-content">
      <div class="top-label-row">
        <div class="label-critique">&#9888; DAMPAK BURUK SAWIT: DEFORESTASI & MONOPOLI</div>
        <div style="font-family: 'Impact'; color: #FFE600; font-size: 18px; letter-spacing: 2px;">
          OPEN SOURCE FREEDOM
        </div>
      </div>
      <div class="comic-title-large hero-title">
        <span class="white-text">NYAWIT</span> <span class="emerald-text">LAUNCHER</span>
      </div>
      <div class="narration-box">
        Katalog Aplikasi Cepat &bull; Satu Ekosistem Multiplatform
      </div>
      <div class="chips-row">
        <div class="feature-pill">Zero Server</div>
        <div class="feature-pill">GitHub API</div>
        <div class="feature-pill">SHA-256 Checksum</div>
        <div class="feature-pill">Android &bull; TV &bull; Desktop</div>
      </div>
    </div>

    <div class="right-cards-cluster">
      <div class="comic-card card-1">
        <div class="card-icon-circle">
          <img src="${BRAND_MARK_URI}" alt="App 1" />
        </div>
        <div class="card-name">NYAWIT MUSIC</div>
        <div class="card-btn">INSTALL &bull; FREE</div>
      </div>
      <div class="comic-card card-2">
        <div class="card-icon-circle">
          <img src="${BRAND_MARK_URI}" alt="App 2" />
        </div>
        <div class="card-name" style="color: #FFE600;">NYAWIT LAUNCHER</div>
        <div class="card-btn" style="background: #3DDC97;">INSTALLED</div>
      </div>
      <div class="comic-card card-3">
        <div class="card-icon-circle">
          <img src="${BRAND_MARK_URI}" alt="App 3" />
        </div>
        <div class="card-name">NYAWIT ARCADE</div>
        <div class="card-btn">INSTALL &bull; FREE</div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// 11. docs/images/release-windows.png, release-linux.png, release-macos.png: 1920x1080 (3 file)
// Mockup installer per OS, teks NYAWIT LAUNCHER
// ============================================================================
function getReleaseMockupHtml(osName, osTag, osDetails) {
  return `<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<style>
  ${COMIC_CORE_CSS}
  * { box-sizing: border-box; margin: 0; padding: 0; }
  body {
    width: 1920px;
    height: 1080px;
    background: #070B10;
    overflow: hidden;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  .ai-disaster-backdrop {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: url("${AI_COMIC_PALM_URI}");
    background-size: cover;
    background-position: center center;
    opacity: 0.42;
    filter: saturate(1.8) contrast(1.4);
  }
  .comic-sunburst {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: repeating-conic-gradient(
      from 0deg,
      rgba(61, 220, 151, 0.28) 0deg 10deg,
      rgba(7, 11, 16, 0.85) 10deg 20deg
    );
  }
  .comic-dots {
    position: absolute;
    width: 100%; height: 100%;
    top: 0; left: 0;
    background-image: radial-gradient(rgba(255, 107, 0, 0.25) 22%, transparent 22%);
    background-size: 22px 22px;
  }
  .hazard-bar-top {
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 18px;
    background: repeating-linear-gradient(
      -45deg,
      #FFE600,
      #FFE600 14px,
      #000000 14px,
      #000000 28px
    );
    border-bottom: 3px solid #000;
    z-index: 10;
  }
  .top-comic-banner {
    position: relative;
    z-index: 5;
    margin-bottom: 24px;
    display: flex;
    align-items: center;
    gap: 20px;
  }
  .os-badge {
    background: #FF3344;
    color: #FFFFFF;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 28px;
    padding: 8px 24px;
    transform: rotate(-2deg);
    letter-spacing: 2px;
  }
  .top-title {
    font-size: 96px;
    transform: skew(-5deg, -1deg);
  }
  .top-title .white-text { color: #FFFFFF; }
  .top-title .emerald-text { color: #3DDC97; }

  /* Comic Window Mockup Frame */
  .comic-window {
    position: relative;
    z-index: 5;
    width: 1200px;
    height: 680px;
    background: #0E1620;
    border: 8px solid #000000;
    border-radius: 28px;
    box-shadow: 18px 18px 0px #000000, 0 0 60px rgba(61, 220, 151, 0.4);
    display: flex;
    flex-direction: column;
    overflow: hidden;
  }
  .window-titlebar {
    height: 64px;
    background: #141E2B;
    border-bottom: 6px solid #000000;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
  }
  .window-title-text {
    font-family: "Impact", "Arial Black", sans-serif;
    color: #3DDC97;
    font-size: 24px;
    letter-spacing: 1.5px;
    text-transform: uppercase;
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .window-controls {
    display: flex;
    gap: 10px;
  }
  .control-circle {
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: 3px solid #000000;
  }
  .window-body {
    flex: 1;
    display: flex;
    padding: 40px;
    gap: 40px;
    align-items: center;
  }
  .installer-emblem-col {
    width: 320px;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .installer-emblem-box {
    width: 260px;
    height: 260px;
    border-radius: 50%;
    background: #000000;
    border: 7px solid #3DDC97;
    box-shadow: 10px 10px 0px #000000, 0 0 35px rgba(61, 220, 151, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
    margin-bottom: 20px;
  }
  .installer-emblem-box img {
    width: 245px; height: 245px; object-fit: contain;
  }
  .installer-tag {
    background: #FFE600;
    color: #000000;
    border: 3.5px solid #000000;
    box-shadow: 4px 4px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 18px;
    padding: 6px 18px;
    text-transform: uppercase;
    transform: rotate(2deg);
  }
  .installer-info-col {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  .step-speech-bubble {
    background: #FFE600;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    padding: 16px 24px;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 24px;
    line-height: 1.3;
    letter-spacing: 1px;
    text-transform: uppercase;
    margin-bottom: 24px;
    position: relative;
    transform: rotate(-1deg);
  }
  .details-box {
    background: #090E14;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    padding: 20px 24px;
    font-family: "Impact", "Arial Black", sans-serif;
    color: #E2E8F0;
    font-size: 20px;
    letter-spacing: 1px;
    line-height: 1.6;
    margin-bottom: 24px;
    border-left: 8px solid #3DDC97;
  }
  .details-box .highlight { color: #3DDC97; }
  .btn-row {
    display: flex;
    gap: 16px;
  }
  .comic-btn-primary {
    background: #3DDC97;
    color: #000000;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 24px;
    padding: 12px 36px;
    text-transform: uppercase;
    letter-spacing: 1.5px;
  }
  .comic-btn-secondary {
    background: #141E2B;
    color: #FFFFFF;
    border: 4px solid #000000;
    box-shadow: 6px 6px 0px #000000;
    font-family: "Impact", "Arial Black", sans-serif;
    font-size: 24px;
    padding: 12px 28px;
    text-transform: uppercase;
    letter-spacing: 1.5px;
  }
</style>
</head>
<body>
  <div class="hazard-bar-top"></div>
  <div class="ai-disaster-backdrop"></div>
  <div class="comic-sunburst"></div>
  <div class="comic-dots"></div>

  <div class="top-comic-banner">
    <div class="os-badge">${osTag}</div>
    <div class="comic-title-large top-title">
      <span class="white-text">NYAWIT</span> <span class="emerald-text">LAUNCHER</span>
    </div>
  </div>

  <div class="comic-window">
    <div class="window-titlebar">
      <div class="window-title-text">
        <span>&#9888; NYAWIT INSTALLER &bull; ${osName.toUpperCase()}</span>
      </div>
      <div class="window-controls">
        <div class="control-circle" style="background: #FF5F56;"></div>
        <div class="control-circle" style="background: #FFBD2E;"></div>
        <div class="control-circle" style="background: #27C93F;"></div>
      </div>
    </div>
    <div class="window-body">
      <div class="installer-emblem-col">
        <div class="installer-emblem-box">
          <img src="${BRAND_MARK_URI}" alt="Emblem" />
        </div>
        <div class="installer-tag">OPEN ECOSYSTEM</div>
      </div>
      <div class="installer-info-col">
        <div class="step-speech-bubble">
          SIAP MEMASANG NYAWIT LAUNCHER KE SISTEM ${osName.toUpperCase()}!
        </div>
        <div class="details-box">
          Target: <span class="highlight">${osDetails.target}</span><br>
          Format Paket: <span class="highlight">${osDetails.format}</span><br>
          Verifikasi Keamanan: <span class="highlight">SHA-256 Otentik Langsung dari GitHub</span><br>
          Arsitektur: <span class="highlight">x86_64 / arm64 Universal</span>
        </div>
        <div class="btn-row">
          <div class="comic-btn-primary">PASANG SEKARANG &bull; FREE</div>
          <div class="comic-btn-secondary">LIHAT KODE SUMBER</div>
        </div>
      </div>
    </div>
  </div>
</body>
</html>`;
}

// ============================================================================
// Execution runner for all deliverables
// ============================================================================
async function generateAll() {
  console.log("=== GENERATING INTENSE COMIC STYLE ASSETS (DAMPAK BURUK SAWIT) ===");
  const results = [];

  // 1. assets/app-icon.png (Master Emblem already preserved at 1024x1024 RGBA)
  console.log("1. assets/app-icon.png -> Preserved original comic master emblem");

  // 2. assets/app-icons/nyawit-launcher.png: 512x512 transparent
  console.log("2. Generating assets/app-icons/nyawit-launcher.png (512x512 transparent)...");
  results.push(await renderHtmlToPng(
    getNyawitLauncherIconHtml(),
    path.join(ROOT_DIR, "assets", "app-icons", "nyawit-launcher.png"),
    512, 512, true
  ));

  // 3. play/ic_launcher-512.png: 512x512 opaque
  console.log("3. Generating play/ic_launcher-512.png (512x512 opaque)...");
  results.push(await renderHtmlToPng(
    getPlayIconHtml(),
    path.join(ROOT_DIR, "play", "ic_launcher-512.png"),
    512, 512, false
  ));

  // 4. assets/logo-new.png: 1024x256 transparent
  console.log("4. Generating assets/logo-new.png (1024x256 transparent)...");
  results.push(await renderHtmlToPng(
    getLogoNewHtml(),
    path.join(ROOT_DIR, "assets", "logo-new.png"),
    1024, 256, true
  ));

  // 5. assets/tv-banner.png & androidApp/.../tv_banner.png: 1280x720
  console.log("5. Generating assets/tv-banner.png & androidApp tv_banner.png (1280x720)...");
  const tvBannerRes = await renderHtmlToPng(
    getTvBannerHtml(),
    path.join(ROOT_DIR, "assets", "tv-banner.png"),
    1280, 720, false
  );
  results.push(tvBannerRes);
  const androidTvPath = path.join(ROOT_DIR, "androidApp", "src", "main", "res", "drawable-nodpi", "tv_banner.png");
  fs.copyFileSync(tvBannerRes.path, androidTvPath);
  console.log("   Copied identical tv_banner.png to androidApp drawable-nodpi");

  // 6. play/feature-graphic.png: 1024x500
  console.log("6. Generating play/feature-graphic.png (1024x500)...");
  results.push(await renderHtmlToPng(
    getFeatureGraphicHtml(),
    path.join(ROOT_DIR, "play", "feature-graphic.png"),
    1024, 500, false
  ));

  // 7. docs/images/readme-hero.png: 1280x400
  console.log("7. Generating docs/images/readme-hero.png (1280x400)...");
  results.push(await renderHtmlToPng(
    getReadmeHeroHtml(),
    path.join(ROOT_DIR, "docs", "images", "readme-hero.png"),
    1280, 400, false
  ));

  // 8. play/promo-graphic.png: 1280x720
  console.log("8. Generating play/promo-graphic.png (1280x720)...");
  results.push(await renderHtmlToPng(
    getPromoGraphicHtml(),
    path.join(ROOT_DIR, "play", "promo-graphic.png"),
    1280, 720, false
  ));

  // 9. docs/images/social-preview.png: 1280x640
  console.log("9. Generating docs/images/social-preview.png (1280x640)...");
  results.push(await renderHtmlToPng(
    getSocialPreviewHtml(),
    path.join(ROOT_DIR, "docs", "images", "social-preview.png"),
    1280, 640, false
  ));

  // 10. assets/ui/hero-desktop.png: 1600x600
  console.log("10. Generating assets/ui/hero-desktop.png (1600x600)...");
  results.push(await renderHtmlToPng(
    getHeroDesktopHtml(),
    path.join(ROOT_DIR, "assets", "ui", "hero-desktop.png"),
    1600, 600, false
  ));

  // 11. docs/images/release-windows.png, release-linux.png, release-macos.png (1920x1080)
  console.log("11. Generating release mockups (1920x1080)...");
  results.push(await renderHtmlToPng(
    getReleaseMockupHtml("Windows 11", "WINDOWS EDISI KHUSUS", {
      target: "Windows 10 / 11 (64-bit)",
      format: "EXE Setup & Standalone MSI Installer"
    }),
    path.join(ROOT_DIR, "docs", "images", "release-windows.png"),
    1920, 1080, false
  ));

  results.push(await renderHtmlToPng(
    getReleaseMockupHtml("Linux", "LINUX UNIVERSAL", {
      target: "Ubuntu / Debian / Fedora / Arch Linux",
      format: "DEB, RPM & Standalone Tarball App"
    }),
    path.join(ROOT_DIR, "docs", "images", "release-linux.png"),
    1920, 1080, false
  ));

  results.push(await renderHtmlToPng(
    getReleaseMockupHtml("macOS", "MACOS UNIVERSAL", {
      target: "macOS Sonoma, Ventura & Sequoia",
      format: "Apple Silicon & Intel Universal DMG Installer"
    }),
    path.join(ROOT_DIR, "docs", "images", "release-macos.png"),
    1920, 1080, false
  ));

  console.log("\nAll intense comic assets generated successfully!");
  console.table(results.map(r => ({ path: path.relative(ROOT_DIR, r.path), size: `${(r.size / 1024).toFixed(1)} KB`, res: `${r.width}x${r.height}` })));
}

generateAll().catch(err => {
  console.error("Error generating assets:", err);
  process.exit(1);
});
