/* CareFlow Phase 2 — role dashboard table toolkit.
 * Adds client-side search, status filtering, row counts, reset, and explicitly
 * opted-in CSV export without replacing server-side forms or authorization.
 */
(() => {
  "use strict";
  const SELECTOR = "table[data-cf-table-tools]";
  const normalize = (value) => String(value ?? "").normalize("NFKD").toLocaleLowerCase().trim();
  const visibleRows = (table) => Array.from(table.tBodies).flatMap((body) => Array.from(body.rows))
    .filter((row) => !row.dataset.cfPlaceholder && !row.querySelector("td[colspan]"));

  function csvCell(value) {
    const text = String(value ?? "").replace(/\s+/g, " ").trim();
    // Escape formula-leading values so spreadsheet software treats them as text.
    const safe = /^[=+\-@\t\r]/.test(text) ? `'${text}` : text;
    return `"${safe.replace(/"/g, '""')}"`;
  }

  function downloadCsv(table, name) {
    const headers = Array.from(table.tHead?.rows[0]?.cells || [])
      .filter((cell) => cell.dataset.exportIgnore !== "true");
    const headerIndexes = headers.map((cell) => Array.from(cell.parentElement.cells).indexOf(cell));
    const rows = visibleRows(table).filter((row) => !row.hidden);
    const lines = [headers.map((cell) => csvCell(cell.innerText)).join(",")];
    rows.forEach((row) => {
      lines.push(headerIndexes.map((index) => {
        const cell = row.cells[index];
        if (!cell) return csvCell("");
        // Export displayed text only, never form values, hidden fields, or controls.
        const clone = cell.cloneNode(true);
        clone.querySelectorAll("form,button,input,select,textarea,[data-export-ignore]").forEach((node) => node.remove());
        return csvCell(clone.innerText || clone.textContent || "");
      }).join(","));
    });
    const blob = new Blob(["\uFEFF" + lines.join("\r\n")], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = name;
    link.hidden = true;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.setTimeout(() => URL.revokeObjectURL(url), 1000);
  }

  function setup(table, index) {
    if (table.dataset.cfToolsReady === "true") return;
    table.dataset.cfToolsReady = "true";
    const wrapper = document.createElement("section");
    wrapper.className = "cf-table-tools";
    wrapper.setAttribute("aria-label", "Table search and filters");
    const searchLabel = document.createElement("label");
    searchLabel.className = "cf-table-tools-search";
    searchLabel.textContent = "Search rows";
    const search = document.createElement("input");
    search.type = "search";
    search.placeholder = "Search this table…";
    search.autocomplete = "off";
    search.setAttribute("aria-label", "Search table rows");
    searchLabel.appendChild(search);
    wrapper.appendChild(searchLabel);

    const statusHeaderIndex = Array.from(table.tHead?.rows[0]?.cells || [])
      .findIndex((cell) => normalize(cell.innerText) === "status");
    let statusFilter = null;
    if (statusHeaderIndex >= 0) {
      const label = document.createElement("label");
      label.className = "cf-table-tools-status";
      label.textContent = "Status";
      statusFilter = document.createElement("select");
      statusFilter.setAttribute("aria-label", "Filter table by status");
      const allOption = document.createElement("option");
      allOption.value = "";
      allOption.textContent = "All statuses";
      statusFilter.appendChild(allOption);
      const statuses = new Set();
      visibleRows(table).forEach((row) => {
        const value = normalize(row.cells[statusHeaderIndex]?.innerText);
        if (value) statuses.add(value);
      });
      Array.from(statuses).sort().forEach((value) => {
        const option = document.createElement("option");
        option.value = value;
        option.textContent = value.replace(/\b\w/g, (letter) => letter.toLocaleUpperCase());
        statusFilter.appendChild(option);
      });
      label.appendChild(statusFilter);
      wrapper.appendChild(label);
    }

    const placeholderRows = Array.from(table.tBodies).flatMap((body) => Array.from(body.rows))
      .filter((row) => row.querySelector("td[colspan]"));
    placeholderRows.forEach((row) => { row.dataset.cfPlaceholder = "true"; });
    const noMatches = document.createElement("tr");
    noMatches.className = "cf-table-no-matches";
    noMatches.hidden = true;
    const noMatchesCell = document.createElement("td");
    noMatchesCell.colSpan = Math.max(1, table.tHead?.rows[0]?.cells.length || 1);
    noMatchesCell.textContent = "No rows match the current filters.";
    noMatches.appendChild(noMatchesCell);
    if (table.tBodies.length) table.tBodies[0].appendChild(noMatches);

    const count = document.createElement("span");
    count.className = "cf-table-tools-count";
    count.setAttribute("aria-live", "polite");
    count.setAttribute("role", "status");
    wrapper.appendChild(count);
    const reset = document.createElement("button");
    reset.type = "button";
    reset.className = "cf-table-tools-reset";
    reset.textContent = "Clear filters";
    wrapper.appendChild(reset);

    if (table.dataset.cfExport === "true") {
      const exportButton = document.createElement("button");
      exportButton.type = "button";
      exportButton.className = "cf-table-tools-export";
      exportButton.textContent = "Export visible CSV";
      exportButton.addEventListener("click", () => downloadCsv(table, table.dataset.cfExportName || `careflow-table-${index + 1}.csv`));
      wrapper.appendChild(exportButton);
    }

    table.parentNode.insertBefore(wrapper, table);
    const apply = () => {
      const query = normalize(search.value);
      const status = statusFilter ? statusFilter.value : "";
      let shown = 0;
      const rows = visibleRows(table);
      rows.forEach((row) => {
        const matchesText = !query || normalize(row.innerText || row.textContent).includes(query);
        const rowStatus = statusHeaderIndex >= 0 ? normalize(row.cells[statusHeaderIndex]?.innerText) : "";
        const matchesStatus = !status || rowStatus === status;
        row.hidden = !(matchesText && matchesStatus);
        if (!row.hidden) shown += 1;
      });
      noMatches.hidden = rows.length === 0 || shown > 0;
      placeholderRows.forEach((row) => { row.hidden = rows.length > 0; });
      count.textContent = `${shown} of ${rows.length} rows`;
      table.dataset.cfVisibleRows = String(shown);
    };
    search.addEventListener("input", apply);
    if (statusFilter) statusFilter.addEventListener("change", apply);
    reset.addEventListener("click", () => {
      search.value = "";
      if (statusFilter) statusFilter.value = "";
      apply();
      search.focus();
    });
    apply();
  }

  function init() {
    document.querySelectorAll(SELECTOR).forEach(setup);
  }
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init, { once: true });
  else init();
})();
