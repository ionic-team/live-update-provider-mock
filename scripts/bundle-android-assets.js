#!/usr/bin/env node

/**
 * Bundle Android Assets Script
 *
 * Copies webapp build outputs to Android assets directory.
 * Creates mock subdirectories (aurora, neon, paper) for each build flavor.
 *
 * Must run after:
 *   - cd webapp && npm run build:portals
 *   - cd webapp && npm run build:fedcap
 *
 * Output:
 *   - android/mock-live-update-provider/src/main/assets/portals/{aurora,neon,paper}
 *   - android/mock-live-update-provider/src/main/assets/fedcap/{aurora,neon,paper}
 */

const fs = require('fs');
const path = require('path');

const ROOT_DIR = path.resolve(__dirname, '..');
const WEBAPP_PORTALS_BUILD = path.join(ROOT_DIR, 'webapp/build-portals');
const WEBAPP_FEDCAP_BUILD = path.join(ROOT_DIR, 'webapp/build-fedcap');
const ANDROID_ASSETS_DIR = path.join(ROOT_DIR, 'android/mock-live-update-provider/src/main/assets');

const MOCK_BUNDLES = ['aurora'];

/**
 * Recursively copies directory contents.
 */
function copyDir(src, dest) {
  if (!fs.existsSync(src)) {
    throw new Error(`Source directory does not exist: ${src}`);
  }

  fs.mkdirSync(dest, { recursive: true });

  const entries = fs.readdirSync(src, { withFileTypes: true });
  for (const entry of entries) {
    const srcPath = path.join(src, entry.name);
    const destPath = path.join(dest, entry.name);

    if (entry.isDirectory()) {
      copyDir(srcPath, destPath);
    } else {
      fs.copyFileSync(srcPath, destPath);
    }
  }
}

/**
 * Removes directory recursively.
 */
function removeDir(dir) {
  if (fs.existsSync(dir)) {
    fs.rmSync(dir, { recursive: true, force: true });
  }
}

/**
 * Main bundling function.
 */
function bundleAssets() {
  console.log('🚀 Bundling Android assets...\n');

  // Verify webapp builds exist
  if (!fs.existsSync(WEBAPP_PORTALS_BUILD)) {
    console.error(`❌ Portals build not found: ${WEBAPP_PORTALS_BUILD}`);
    console.error('   Run: cd webapp && npm run build:portals');
    process.exit(1);
  }

  if (!fs.existsSync(WEBAPP_FEDCAP_BUILD)) {
    console.error(`❌ FedCap build not found: ${WEBAPP_FEDCAP_BUILD}`);
    console.error('   Run: cd webapp && npm run build:fedcap');
    process.exit(1);
  }

  // Clean existing assets directory
  console.log('🧹 Cleaning existing assets...');
  removeDir(ANDROID_ASSETS_DIR);

  // Bundle Portals builds
  console.log('📦 Bundling Portals assets...');
  for (const bundle of MOCK_BUNDLES) {
    const destDir = path.join(ANDROID_ASSETS_DIR, 'portals', bundle);
    console.log(`   → portals/${bundle}`);
    copyDir(WEBAPP_PORTALS_BUILD, destDir);
  }

  // Bundle FedCap builds
  console.log('📦 Bundling FedCap assets...');
  for (const bundle of MOCK_BUNDLES) {
    const destDir = path.join(ANDROID_ASSETS_DIR, 'fedcap', bundle);
    console.log(`   → fedcap/${bundle}`);
    copyDir(WEBAPP_FEDCAP_BUILD, destDir);
  }

  // Verify index.html exists in each bundle
  console.log('\n✅ Verifying bundles...');
  let allValid = true;
  for (const appType of ['portals', 'fedcap']) {
    for (const bundle of MOCK_BUNDLES) {
      const indexPath = path.join(ANDROID_ASSETS_DIR, appType, bundle, 'index.html');
      if (!fs.existsSync(indexPath)) {
        console.error(`❌ Missing index.html: ${appType}/${bundle}/index.html`);
        allValid = false;
      }
    }
  }

  if (allValid) {
    console.log('✅ All bundles verified!\n');
    console.log(`📁 Assets bundled to: ${ANDROID_ASSETS_DIR}\n`);
  } else {
    console.error('\n❌ Some bundles are invalid. Check the build outputs.\n');
    process.exit(1);
  }
}

// Run
try {
  bundleAssets();
} catch (error) {
  console.error('\n❌ Bundling failed:', error.message);
  process.exit(1);
}
