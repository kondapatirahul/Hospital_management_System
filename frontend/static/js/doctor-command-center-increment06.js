(function () {
  "use strict";
  const refreshButton = document.getElementById("cf-command-refresh");
  const statusNode = document.getElementById("cf-command-status");
  const domainsNode = document.getElementById("cf-command-domains");
  const insightsNode = document.getElementById("cf-command-insight-list");
  const countNode = document.getElementById("cf-command-count");
  if (!refreshButton || !statusNode || !domainsNode || !insightsNode) return;

  const escapeHtml = (value) => String(value ?? "").replace(/[&<>"']/g, (char) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[char]);

  function renderDomains(domains) {
    domainsNode.innerHTML = domains.map((domain) => {
      const coverage = domain.rulesEvaluated
        ? Math.round((domain.metricsAvailable / domain.rulesEvaluated) * 100) : 0;
      return `<article class="cf-command-domain">
        <strong>${escapeHtml(domain.domain.replaceAll("_", " "))}</strong>
        <span>${domain.insights.length} actionable item(s) · ${domain.metricsAvailable} signal(s) evaluated</span>
        <em>${coverage}% rule coverage</em>
      </article>`;
    }).join("");
  }

  function renderInsights(insights) {
    countNode.textContent = `${insights.length} item(s)`;
    if (!insights.length) {
      insightsNode.innerHTML = '<p class="cf-command-empty">No threshold breaches were found in the connected appointment signals.</p>';
      return;
    }
    insightsNode.innerHTML = insights.slice(0, 12).map((item) => `
      <article class="cf-command-insight" data-severity="${escapeHtml(item.severity)}">
        <span class="cf-command-insight-bar" aria-hidden="true"></span>
        <div>
          <strong>${escapeHtml(item.description)}</strong>
          <p>${escapeHtml(item.metric)}: ${escapeHtml(item.observedValue)} · threshold ${escapeHtml(item.threshold)}</p>
          <small>${escapeHtml(item.recommendedAction)}</small>
        </div>
        <span class="cf-command-severity">${escapeHtml(item.severity)}</span>
      </article>`).join("");
  }

  async function refresh() {
    refreshButton.disabled = true;
    statusNode.textContent = "Refreshing your authorized appointment signals…";
    try {
      const response = await fetch("/doctor/dashboard/command-center/summary", {
        method: "GET",
        headers: { "Accept": "application/json" },
        credentials: "same-origin"
      });
      if (!response.ok) throw new Error(`Request failed (${response.status})`);
      const data = await response.json();
      renderDomains(data.domains || []);
      renderInsights(data.insights || []);
      statusNode.textContent = `${data.appointmentsToday} appointment(s) for ${data.appointmentDate}. ${data.metricsAvailable} of ${data.rulesEvaluated} configured rule checks had connected signals. ${data.dataNotice}`;
    } catch (error) {
      statusNode.textContent = `Could not refresh command-center insights. ${error.message}`;
    } finally {
      refreshButton.disabled = false;
    }
  }
  refreshButton.addEventListener("click", refresh);
  refresh();
})();
