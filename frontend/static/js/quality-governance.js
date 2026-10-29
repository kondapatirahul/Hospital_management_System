/* Small, dependency-free helpers for the quality workspace. */
(() => {
  'use strict';
  const today = new Date();
  const isoToday = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}-${String(today.getDate()).padStart(2,'0')}`;
  document.querySelectorAll('input[type="date"]').forEach((field) => {
    if (field.dataset.allowPast !== 'true' && field.name === 'dueDate') field.min = isoToday;
  });
  document.querySelectorAll('form').forEach((form) => {
    form.addEventListener('submit', (event) => {
      if (!form.checkValidity()) {
        event.preventDefault();
        form.reportValidity();
        return;
      }
      const submitter = event.submitter;
      if (submitter && submitter.dataset.confirm && !window.confirm(submitter.dataset.confirm)) {
        event.preventDefault();
        return;
      }
      if (submitter && submitter.dataset.busyLabel) {
        submitter.dataset.originalLabel = submitter.textContent;
        submitter.textContent = submitter.dataset.busyLabel;
        submitter.disabled = true;
      }
    });
  });
  document.querySelectorAll('[data-confirm]').forEach((button) => {
    button.addEventListener('click', (event) => {
      if (!window.confirm(button.dataset.confirm)) event.preventDefault();
    });
  });
  document.querySelectorAll('[data-copy-reference]').forEach((button) => {
    button.addEventListener('click', async () => {
      const value = button.dataset.copyReference || '';
      try {
        await navigator.clipboard.writeText(value);
        const old = button.textContent;
        button.textContent = 'Copied';
        window.setTimeout(() => { button.textContent = old; }, 1200);
      } catch (_) {
        window.prompt('Copy case reference:', value);
      }
    });
  });
})();
