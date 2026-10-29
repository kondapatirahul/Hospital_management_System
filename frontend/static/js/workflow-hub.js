(() => {
  "use strict";
  const input = document.getElementById("workflowSearch");
  const grid = document.getElementById("workflowGrid");
  if (!input || !grid) return;

  const cards = Array.from(grid.querySelectorAll(".cf-workflow-card"));
  const empty = document.getElementById("noWorkflowResults");
  const clearButton = document.getElementById("clearWorkflowSearch");
  const viewToggle = document.getElementById("viewToggle");

  function applySearch() {
    const query = input.value.trim().toLocaleLowerCase();
    let visible = 0;
    cards.forEach((card) => {
      const text = (card.dataset.search || card.textContent || "").toLocaleLowerCase();
      const matches = !query || text.includes(query);
      card.hidden = !matches;
      if (matches) visible += 1;
    });
    if (empty) empty.hidden = visible !== 0;
  }

  input.addEventListener("input", applySearch);
  if (clearButton) {
    clearButton.addEventListener("click", () => {
      input.value = "";
      applySearch();
      input.focus();
    });
  }
  if (viewToggle) {
    viewToggle.addEventListener("click", () => {
      const compact = grid.classList.toggle("cf-compact");
      viewToggle.setAttribute("aria-pressed", String(compact));
      const label = viewToggle.querySelector("span");
      if (label) label.textContent = compact ? "Comfortable view" : "Compact view";
    });
  }
  document.addEventListener("keydown", (event) => {
    if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === "k") {
      event.preventDefault();
      input.focus();
    }
    if (event.key === "Escape" && document.activeElement === input) {
      input.value = "";
      applySearch();
      input.blur();
    }
  });
  // Keyboard users can identify and activate each workflow without a mouse.
  cards.forEach((card) => {
    const link = card.querySelector(".cf-open-link");
    if (link) {
      card.addEventListener("keydown", (event) => {
        if (event.key === "Enter" && event.target === card) link.click();
      });
    }
  });
})();


/* Department board interactions */
(function () {
  "use strict";
  const all = (selector, root = document) => Array.from(root.querySelectorAll(selector));
  const one = (selector, root = document) => root.querySelector(selector);

  function boardToast(message) {
    if (window.CareFlowUI && typeof window.CareFlowUI.toast === "function") {
      window.CareFlowUI.toast(message, "success", 1800);
      return;
    }
    const live = one("[data-board-live]");
    if (live) live.textContent = message;
  }

  function initBoardChecks(board) {
    const checks = all("[data-check-item]", board);
    if (!checks.length) return;
    const role = board.querySelector(".cf-role-metadata");
    let roleKey = "careflow";
    if (role) {
      try { roleKey = JSON.parse(role.textContent).role || roleKey; } catch (_) { /* keep default */ }
    }
    const storageKey = `careflow-checklist:${roleKey}`;
    let saved = {};
    try { saved = JSON.parse(localStorage.getItem(storageKey) || "{}"); } catch (_) { saved = {}; }
    const update = () => {
      const complete = checks.filter((item) => one("input[type=checkbox]", item)?.checked).length;
      const percent = Math.round((complete / Math.max(1, checks.length)) * 100);
      all("[data-check-progress]", board).forEach((node) => { node.textContent = `${percent}%`; });
      all("[data-check-progress-bar]", board).forEach((node) => { node.style.width = `${percent}%`; });
      board.dataset.checklistProgress = String(percent);
      try {
        const state = {};
        checks.forEach((item) => {
          const input = one("input[type=checkbox]", item);
          if (input) state[input.dataset.checkKey || input.name || String(checks.indexOf(item))] = input.checked;
        });
        localStorage.setItem(storageKey, JSON.stringify(state));
      } catch (_) { /* Private browsing may disable storage; checkboxes still work for this page. */ }
    };
    checks.forEach((item, index) => {
      const input = one("input[type=checkbox]", item);
      if (!input) return;
      const key = input.dataset.checkKey || input.name || String(index);
      input.checked = Boolean(saved[key]);
      input.addEventListener("change", update);
    });
    update();
  }

  function initBoardSearch(board) {
    const input = one("[data-role-search]", board);
    const result = one("[data-role-search-results]", board);
    if (!input || !result) return;
    const cards = all("[data-action-card]", board).map((link) => ({
      href: link.getAttribute("href"),
      title: one("strong", link)?.textContent.trim() || link.textContent.trim(),
      description: one("small", link)?.textContent.trim() || "",
      node: link
    }));
    const render = () => {
      const query = input.value.trim().toLocaleLowerCase();
      const matches = cards.filter((card) => `${card.title} ${card.description}`.toLocaleLowerCase().includes(query));
      result.replaceChildren();
      if (!matches.length) {
        const empty = document.createElement("div");
        empty.className = "cf-role-search-empty";
        empty.textContent = "No matching actions. Try a shorter search term.";
        result.appendChild(empty);
        return;
      }
      matches.forEach((card) => {
        const link = document.createElement("a");
        link.className = "cf-role-search-result";
        link.href = card.href;
        const title = document.createElement("span");
        title.textContent = card.title;
        const arrow = document.createElement("span");
        arrow.textContent = "↗";
        arrow.setAttribute("aria-hidden", "true");
        link.append(title, arrow);
        result.appendChild(link);
      });
    };
    input.addEventListener("input", render);
    input.addEventListener("keydown", (event) => {
      if (event.key === "Escape") {
        input.value = "";
        render();
      }
      if (event.key === "Enter") {
        const first = one("a", result);
        if (first && !event.ctrlKey && !event.metaKey) window.location.assign(first.href);
      }
    });
    render();
  }

  function initBoardPrint(board) {
    all("[data-board-print]", board).forEach((button) => {
      button.addEventListener("click", () => window.print());
    });
  }

  function initBoardKpis(board) {
    all("[data-kpi-card]", board).forEach((card, index) => {
      card.addEventListener("keydown", (event) => {
        if ((event.key === "Enter" || event.key === " ") && event.target === card) {
          event.preventDefault();
          window.location.assign(card.href);
        }
      });
      card.style.setProperty("--cf-card-order", String(index));
    });
  }

  function initBoardShortcuts(board) {
    all("[data-action-card]", board).forEach((card) => {
      card.addEventListener("pointerenter", () => card.classList.add("is-hovered"));
      card.addEventListener("pointerleave", () => card.classList.remove("is-hovered"));
    });
  }

  function initBoard(board) {
    if (board.dataset.cfRoleBoardInitialized === "true") return;
    board.dataset.cfRoleBoardInitialized = "true";
    initBoardChecks(board);
    initBoardSearch(board);
    initBoardPrint(board);
    initBoardKpis(board);
    initBoardShortcuts(board);
  }

  function initAllBoards() {
    all(".cf-role-board").forEach(initBoard);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initAllBoards, { once: true });
  } else {
    initAllBoards();
  }

  document.addEventListener("keydown", (event) => {
    const target = event.target;
    const editing = target && (target.matches("input,textarea,select,[contenteditable=true]"));
    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === "k" && !editing) {
      const search = one("#workflowSearch") || one("[data-role-search]");
      if (search) {
        event.preventDefault();
        search.focus();
      }
    }
  });
})();
