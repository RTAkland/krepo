/*
 * Copyright © 2025 RTAkland
 * Date: 11/25/25, 10:35 PM
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

setTimeout(function () {
    const box = document.getElementById("loading-box");
    if (box) {
        box.innerHTML = `<span>Loading timeout.<br>The backend may be offline.</span><br>`;
    }
}, 15000);
