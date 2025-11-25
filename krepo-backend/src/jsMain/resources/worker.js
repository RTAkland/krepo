export default {
    async fetch(request, env) {
        const url = new URL(request.url);
        const path = url.pathname;
        const proxyPrefixes = ["/api/", "/releases", "/snapshots", "/private"];
        const shouldProxy = proxyPrefixes.some(prefix => path.startsWith(prefix));

        if (shouldProxy) {
            let backendPath = path;
            if (path.startsWith("/api/")) {
                backendPath = path.replace("/api", "");
            }

            const backendUrl = "https://pkg.rtast.cn" + backendPath + (url.search || "");
            console.log("Proxy to backend:", backendUrl);

            return fetch(backendUrl, {
                method: request.method,
                headers: request.headers,
                body: request.method !== "GET" ? request.body : null,
            });
        }

        let staticResponse = await env.ASSETS.fetch(request);
        if (staticResponse.status !== 404) {
            return staticResponse;
        }

        return env.ASSETS.fetch("dist/index.html");
    },
};
