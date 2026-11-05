(function () {
    "use strict";
    const button = document.getElementById("cf-lab-refresh"), status = document.getElementById("cf-lab-status");
    const domains = document.getElementById("cf-lab-domains"), insights = document.getElementById("cf-lab-insights");
    const count = document.getElementById("cf-lab-count");
    if (!button || !status || !domains || !insights) return;
    const esc = v => String(v ?? "").replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
    async function refresh() {
        button.disabled = true; status.textContent = "Refreshing authorized laboratory worklist signals…";
        try {
            const response = await fetch("/laboratory/command-center/summary", { headers: { "Accept": "application/json" }, credentials: "same-origin" });
            if (!response.ok) throw new Error("Request failed (" + response.status + ")");
            const data = await response.json();
            domains.innerHTML = (data.domains || []).map(d => `<article><b>${esc(d.domain.replaceAll("_", " "))}</b><span>${d.insights.length} actionable · ${d.metricsAvailable} signals / ${d.rulesEvaluated} checks</span></article>`).join("");
            const rows = data.insights || []; count.textContent = rows.length + " items";
            insights.innerHTML = rows.length ? rows.slice(0, 15).map(x => `<article class="cf-lab-insight" data-severity="${esc(x.severity)}"><i></i><div><b>${esc(x.description)}</b><p>${esc(x.metric)}: ${esc(x.observedValue)} · threshold ${esc(x.threshold)}</p><small>${esc(x.recommendedAction)}</small></div><em>${esc(x.severity)}</em></article>`).join("") : '<p class="cf-lab-empty">No threshold breaches in the connected signals.</p>';
            status.textContent = `${data.reportCount} reports and ${data.sampleCount} specimens. ${data.pendingReports} pending reports. ${data.metricsAvailable} of ${data.rulesEvaluated} rule checks have connected signals. ${data.dataNotice}`;
        } catch (e) { status.textContent = "Could not refresh laboratory insights: " + e.message; }
        finally { button.disabled = false; }
    }
    button.addEventListener("click", refresh); refresh();
})();
