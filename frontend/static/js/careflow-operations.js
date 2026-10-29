/* CareFlow operational interactions: progressive enhancement, no framework dependency. */
(() => {
  "use strict";

  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => Array.from(root.querySelectorAll(selector));
  const escapeHtml = (value) => String(value ?? "").replace(/[&<>"']/g, (ch) => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[ch]);

  function ensureToastRegion() {
    let region = $(".cf-toast-region");
    if (!region) {
      region = document.createElement("div");
      region.className = "cf-toast-region";
      region.setAttribute("aria-live", "polite");
      region.setAttribute("aria-atomic", "false");
      document.body.appendChild(region);
    }
    return region;
  }

  function toast(message, kind = "success", duration = 4500) {
    const region = ensureToastRegion();
    const node = document.createElement("div");
    node.className = "cf-toast";
    node.dataset.kind = ["success", "error", "warning"].includes(kind) ? kind : "success";
    node.setAttribute("role", kind === "error" ? "alert" : "status");
    const text = document.createElement("span");
    text.textContent = String(message);
    const close = document.createElement("button");
    close.type = "button";
    close.setAttribute("aria-label", "Dismiss notification");
    close.textContent = "×";
    close.addEventListener("click", () => node.remove());
    node.append(text, close);
    region.appendChild(node);
    if (duration > 0) window.setTimeout(() => node.remove(), duration);
    return node;
  }

  function confirmDialog({ title = "Confirm action", message = "Are you sure you want to continue?", confirmText = "Continue", danger = false } = {}) {
    return new Promise((resolve) => {
      const backdrop = document.createElement("div");
      backdrop.className = "cf-confirm-backdrop";
      backdrop.innerHTML = `
        <section class="cf-confirm-dialog" role="dialog" aria-modal="true" aria-labelledby="cfConfirmTitle" aria-describedby="cfConfirmMessage">
          <h2 id="cfConfirmTitle"></h2>
          <p id="cfConfirmMessage"></p>
          <div class="cf-confirm-actions">
            <button type="button" class="cf-action-btn" data-cancel>Cancel</button>
            <button type="button" class="cf-action-btn ${danger ? "danger" : "primary"}" data-confirm></button>
          </div>
        </section>`;
      $("#cfConfirmTitle", backdrop).textContent = title;
      $("#cfConfirmMessage", backdrop).textContent = message;
      $("[data-confirm]", backdrop).textContent = confirmText;
      document.body.appendChild(backdrop);
      const cancel = () => { backdrop.remove(); resolve(false); };
      const accept = () => { backdrop.remove(); resolve(true); };
      $("[data-cancel]", backdrop).addEventListener("click", cancel);
      $("[data-confirm]", backdrop).addEventListener("click", accept);
      backdrop.addEventListener("click", (event) => { if (event.target === backdrop) cancel(); });
      backdrop.addEventListener("keydown", (event) => { if (event.key === "Escape") cancel(); });
      $("[data-cancel]", backdrop).focus();
    });
  }

  function setupConfirmActions() {
    $$("[data-confirm-message], form[data-confirm]").forEach((element) => {
      if (element.dataset.cfConfirmBound === "true") return;
      element.dataset.cfConfirmBound = "true";
      const isForm = element.tagName === "FORM";
      const message = element.dataset.confirmMessage || "This action may change a hospital record. Continue?";
      const title = element.dataset.confirmTitle || "Confirm action";
      const label = element.dataset.confirmLabel || (element.dataset.confirmDanger === "true" ? "Confirm change" : "Continue");
      const handler = async (event) => {
        if (element.dataset.cfConfirmedOnce === "true") {
          element.dataset.cfConfirmedOnce = "false";
          return;
        }
        event.preventDefault();
        const accepted = await confirmDialog({
          title, message, confirmText: label, danger: element.dataset.confirmDanger === "true"
        });
        if (!accepted) return;
        if (isForm) {
          element.dataset.cfConfirmedOnce = "true";
          if (typeof element.requestSubmit === "function") element.requestSubmit(event.submitter || undefined);
          else element.submit();
        } else if (element.tagName === "A" && element.href) {
          window.location.assign(element.href);
        } else {
          element.dispatchEvent(new CustomEvent("careflow:confirmed", { bubbles: true }));
        }
      };
      element.addEventListener(isForm ? "submit" : "click", handler);
    });
  }

  function statusClass(value) {
    const normalized = String(value || "").trim().toLowerCase().replace(/[\s_]+/g, "-");
    if (/(complete|paid|verified|reviewed|active|available|success|received|dispensed)/.test(normalized)) return "success";
    if (/(pending|partial|processing|ordered|in-progress|waiting|requested|draft)/.test(normalized)) return "warning";
    if (/(cancel|reject|overdue|expired|failed|void|critical|unavailable)/.test(normalized)) return "danger";
    if (/(scheduled|assigned|checked-in|in-consultation|collected|issued)/.test(normalized)) return "info";
    return "";
  }

  function decorateStatuses() {
    $$("td, .status, .badge, .status-badge, [data-status]").forEach((node) => {
      if (node.dataset.cfStatusDecorated === "true") return;
      const text = node.dataset.status || node.textContent.trim();
      if (!text || text.length > 45 || !/(pending|complete|paid|verified|reviewed|active|available|success|received|dispensed|partial|processing|ordered|waiting|requested|draft|cancel|reject|overdue|expired|failed|void|critical|unavailable|scheduled|assigned|checked|consultation|collected|issued)/i.test(text)) return;
      node.dataset.cfStatusDecorated = "true";
      node.classList.add("cf-status");
      const tone = statusClass(text);
      if (tone) node.classList.add(tone);
    });
  }

  function setupTableTools() {
    $$("table").forEach((table, index) => {
      if (table.dataset.cfTools === "true") return;
      const body = $("tbody", table);
      if (!body) return;
      table.dataset.cfTools = "true";
      table.classList.add("cf-data-table");
      let toolbar = table.closest(".cf-table-wrap");
      if (!toolbar) {
        toolbar = document.createElement("div");
        toolbar.className = "cf-table-wrap";
        table.parentNode.insertBefore(toolbar, table);
        toolbar.appendChild(table);
      }
      const tools = document.createElement("div");
      tools.className = "cf-data-toolbar";
      tools.innerHTML = `
        <div class="cf-data-tools">
          <label class="cf-sr-only" for="cfTableSearch${index}">Search this table</label>
          <input class="cf-filter-input" type="search" id="cfTableSearch${index}" placeholder="Search this table..." autocomplete="off">
          <label class="cf-sr-only" for="cfTableStatus${index}">Filter by status</label>
          <select class="cf-filter-select" id="cfTableStatus${index}">
            <option value="">All statuses</option>
            <option value="pending">Pending</option><option value="scheduled">Scheduled</option>
            <option value="completed">Completed</option><option value="paid">Paid</option>
            <option value="cancelled">Cancelled</option><option value="rejected">Rejected</option>
            <option value="overdue">Overdue</option>
          </select>
        </div>
        <div class="cf-data-tools">
          <span class="cf-result-count" aria-live="polite"><strong data-visible-count>0</strong> visible rows</span>
          <button type="button" class="cf-action-btn" data-export-table>Export CSV</button>
        </div>`;
      toolbar.parentNode.insertBefore(tools, toolbar);
      const search = $(`#cfTableSearch${index}`, tools);
      const status = $(`#cfTableStatus${index}`, tools);
      const count = $("[data-visible-count]", tools);
      const rows = () => $$("tbody tr", table).filter((row) => !row.classList.contains("cf-empty-row"));
      const filter = () => {
        const q = search.value.trim().toLocaleLowerCase();
        const selected = status.value;
        let visible = 0;
        rows().forEach((row) => {
          const text = row.textContent.toLocaleLowerCase();
          const rowStatus = (row.dataset.status || $(".cf-status", row)?.textContent || text).toLocaleLowerCase();
          const matches = (!q || text.includes(q)) && (!selected || rowStatus.includes(selected));
          row.hidden = !matches;
          if (matches) visible += 1;
        });
        count.textContent = String(visible);
        let empty = $(".cf-empty-row", body);
        if (visible === 0 && !empty) {
          empty = document.createElement("tr");
          empty.className = "cf-empty-row";
          empty.innerHTML = `<td colspan="${Math.max(1, $$("thead th", table).length)}">No records match your filters.</td>`;
          body.appendChild(empty);
        } else if (empty) {
          empty.hidden = visible !== 0;
        }
      };
      search.addEventListener("input", filter);
      status.addEventListener("change", filter);
      $("[data-export-table]", tools).addEventListener("click", () => exportTable(table));
      filter();
    });
  }

  function exportTable(table) {
    const rows = $$("tr", table).filter((row) => !row.hidden && !row.classList.contains("cf-empty-row"));
    const csv = rows.map((row) => $$("th,td", row).map((cell) => {
      const text = cell.innerText.replace(/\s+/g, " ").trim().replace(/"/g, '""');
      return `"${text}"`;
    }).join(",")).join("\r\n");
    const blob = new Blob(["\uFEFF" + csv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `careflow-export-${new Date().toISOString().slice(0, 10)}.csv`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    URL.revokeObjectURL(url);
    toast("CSV export prepared from the currently visible table rows.", "success");
  }

  function setupForms() {
    $$("form").forEach((form) => {
      if (form.dataset.cfFormBound === "true") return;
      form.dataset.cfFormBound = "true";
      $$("input, select, textarea", form).forEach((field) => {
        if (field.type === "hidden" || field.type === "submit" || field.type === "button") return;
        if (field.name && !field.id) field.id = `cf-${field.name.replace(/[^a-z0-9_-]/gi, "-")}`;
        const label = field.id ? $(`label[for="${CSS.escape(field.id)}"]`, form) : null;
        if (field.required && label && !label.querySelector(".cf-required-mark")) {
          const mark = document.createElement("span");
          mark.className = "cf-required-mark";
          mark.textContent = " *";
          mark.style.color = "#d14343";
          label.appendChild(mark);
        }
        field.addEventListener("input", () => {
          field.removeAttribute("aria-invalid");
          const error = field.parentElement && $(".cf-client-error", field.parentElement);
          if (error) error.remove();
        });
      });
      form.addEventListener("submit", (event) => {
        if (form.dataset.cfConfirmedOnce === "true") return;
        const invalid = $$("input,select,textarea", form).find((field) => {
          if (field.disabled || field.type === "hidden" || field.type === "submit" || field.type === "button") return false;
          if (field.required && !String(field.value || "").trim()) return true;
          if (field.type === "email" && field.value && !field.validity.valid) return true;
          if (field.type === "number" && field.value && !field.validity.valid) return true;
          if (field.minLength > 0 && field.value && field.value.length < field.minLength) return true;
          if (field.maxLength > 0 && field.value && field.value.length > field.maxLength) return true;
          return false;
        });
        if (invalid) {
          event.preventDefault();
          invalid.setAttribute("aria-invalid", "true");
          invalid.focus();
          toast("Please check the highlighted field before submitting.", "error");
        } else {
          const submitter = event.submitter;
          if (submitter && !submitter.dataset.noLoading) {
            submitter.dataset.originalLabel = submitter.textContent;
            submitter.classList.add("cf-loading");
            submitter.setAttribute("aria-busy", "true");
            window.setTimeout(() => {
              if (submitter.isConnected) {
                submitter.classList.remove("cf-loading");
                submitter.removeAttribute("aria-busy");
              }
            }, 12000);
          }
        }
      });
    });
  }

  function setupCharacterCounters() {
    $$("textarea[maxlength], input[maxlength]").forEach((field) => {
      if (field.dataset.cfCounter === "true") return;
      field.dataset.cfCounter = "true";
      const wrapper = field.parentElement;
      if (!wrapper) return;
      const counter = document.createElement("div");
      counter.className = "cf-field-counter";
      const name = document.createElement("span");
      name.textContent = field.dataset.counterLabel || "Character count";
      const number = document.createElement("span");
      counter.append(name, number);
      wrapper.appendChild(counter);
      const update = () => {
        number.textContent = `${field.value.length} / ${field.maxLength}`;
        number.style.color = field.value.length > field.maxLength * .9 ? "#bd3333" : "";
      };
      field.addEventListener("input", update);
      update();
    });
  }

  function setupQuickFilters() {
    $$("[data-quick-filter]").forEach((group) => {
      const targetSelector = group.dataset.target;
      const target = targetSelector ? $(targetSelector) : null;
      if (!target) return;
      const buttons = $$("button[data-filter-value]", group);
      buttons.forEach((button) => {
        button.classList.add("cf-quick-filter-button");
        button.setAttribute("aria-pressed", button.dataset.filterValue === "" ? "true" : "false");
        button.addEventListener("click", () => {
          buttons.forEach((item) => item.setAttribute("aria-pressed", String(item === button)));
          const value = button.dataset.filterValue || "";
          const rows = $$("tbody tr", target);
          rows.forEach((row) => {
            const state = (row.dataset.status || $(".cf-status", row)?.textContent || "").toLocaleLowerCase();
            row.hidden = Boolean(value) && !state.includes(value.toLocaleLowerCase());
          });
        });
      });
    });
  }

  function setupCopyableValues() {
    $$(".cf-copyable, [data-copy-value]").forEach((node) => {
      if (node.dataset.cfCopyBound === "true") return;
      node.dataset.cfCopyBound = "true";
      node.setAttribute("title", "Click to copy");
      node.addEventListener("click", async () => {
        const value = node.dataset.copyValue || node.textContent.trim();
        try {
          await navigator.clipboard.writeText(value);
          toast("Copied to clipboard.", "success", 1800);
        } catch (_) {
          toast("Clipboard access is unavailable in this browser.", "warning");
        }
      });
    });
  }

  function setupLiveClock() {
    const clocks = $$(".cf-live-clock, [data-careflow-clock]");
    if (!clocks.length) return;
    const update = () => {
      const now = new Date();
      const formatted = new Intl.DateTimeFormat(undefined, {
        weekday: "short", day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit", second: "2-digit"
      }).format(now);
      clocks.forEach((clock) => { clock.textContent = formatted; });
    };
    update();
    window.setInterval(update, 1000);
  }

  function setupDismissibleAlerts() {
    $$("[data-dismissible-alert]").forEach((alert) => {
      if ($("button[data-dismiss-alert]", alert)) return;
      const button = document.createElement("button");
      button.type = "button";
      button.dataset.dismissAlert = "true";
      button.setAttribute("aria-label", "Dismiss message");
      button.textContent = "×";
      button.style.cssText = "margin-left:auto;border:0;background:transparent;color:inherit;font-size:18px;cursor:pointer";
      button.addEventListener("click", () => alert.remove());
      alert.appendChild(button);
    });
  }

  function setupFilterClearButtons() {
    $$("[data-clear-filters]").forEach((button) => {
      button.addEventListener("click", () => {
        const target = button.dataset.target ? $(button.dataset.target) : button.closest("form");
        if (!target) return;
        $$("input,select,textarea", target).forEach((field) => {
          if (field.type === "checkbox" || field.type === "radio") field.checked = false;
          else field.value = "";
        });
        const search = $(".cf-filter-input", target);
        if (search) search.dispatchEvent(new Event("input", { bubbles: true }));
        toast("Filters cleared.", "success", 1500);
      });
    });
  }

  function setupPrintActions() {
    $$("[data-print-page]").forEach((button) => {
      button.addEventListener("click", () => window.print());
    });
  }

  function setupDateGuardrails() {
    const today = new Date();
    const localDate = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,"0")}-${String(today.getDate()).padStart(2,"0")}`;
    $$('input[type="date"][data-no-past]').forEach((field) => {
      if (!field.min) field.min = localDate;
    });
    $$('input[type="datetime-local"][data-no-past]').forEach((field) => {
      const localDateTime = `${localDate}T${String(today.getHours()).padStart(2,"0")}:${String(today.getMinutes()).padStart(2,"0")}`;
      if (!field.min) field.min = localDateTime;
    });
  }

  function setupDashboardNumbers() {
    $$("[data-count-up]").forEach((node) => {
      if (node.dataset.cfCounted === "true") return;
      const target = Number(node.dataset.countUp || node.textContent.replace(/[^\d.-]/g, ""));
      if (!Number.isFinite(target)) return;
      node.dataset.cfCounted = "true";
      const duration = Math.min(900, Math.max(250, Number(node.dataset.duration) || 650));
      const start = performance.now();
      const initial = 0;
      const update = (now) => {
        const progress = Math.min(1, (now - start) / duration);
        const eased = 1 - Math.pow(1 - progress, 3);
        node.textContent = Math.round(initial + (target - initial) * eased).toLocaleString();
        if (progress < 1) requestAnimationFrame(update);
      };
      requestAnimationFrame(update);
    });
  }

  function announceServerMessages() {
    $$("[data-flash-message]").forEach((node) => {
      if (node.dataset.cfAnnounced === "true") return;
      node.dataset.cfAnnounced = "true";
      const message = node.dataset.flashMessage || node.textContent.trim();
      const kind = node.dataset.kind || "success";
      if (message) toast(message, kind);
    });
  }

  function setupTableKeyboardFocus() {
    $$(".cf-table-wrap table").forEach((table) => {
      $$("tbody tr", table).forEach((row) => {
        if ($("a,button,input,select,textarea", row)) return;
        row.tabIndex = 0;
        row.addEventListener("keydown", (event) => {
          if (event.key === "Enter") {
            const link = $("a[href]", row);
            if (link) link.click();
          }
        });
      });
    });
  }

  function setupExternalLinks() {
    $$('a[target="_blank"]').forEach((link) => {
      const rel = new Set((link.getAttribute("rel") || "").split(/\s+/).filter(Boolean));
      rel.add("noopener");
      rel.add("noreferrer");
      link.setAttribute("rel", Array.from(rel).join(" "));
    });
  }

  function init() {
    setupConfirmActions();
    setupTableTools();
    setupForms();
    setupCharacterCounters();
    setupQuickFilters();
    setupCopyableValues();
    setupLiveClock();
    setupDismissibleAlerts();
    setupFilterClearButtons();
    setupPrintActions();
    setupDateGuardrails();
    setupDashboardNumbers();
    announceServerMessages();
    setupTableKeyboardFocus();
    setupExternalLinks();
    decorateStatuses();
  }

  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init, { once: true });
  else init();

  // Small public surface for pages that want to show a message after an async operation.
  window.CareFlowUI = Object.freeze({ toast, confirm: confirmDialog, refreshTables: setupTableTools });
})();
