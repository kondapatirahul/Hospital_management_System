/* Progressive enhancements for Phase 4 operational workspaces. */
(() => {
  'use strict';
  const forms = document.querySelectorAll('form');
  forms.forEach((form) => {
    form.addEventListener('submit', (event) => {
      if (!form.checkValidity()) { event.preventDefault(); form.reportValidity(); return; }
      const submitter = event.submitter;
      if (submitter && submitter.dataset.confirm && !window.confirm(submitter.dataset.confirm)) { event.preventDefault(); return; }
      if (submitter && submitter.type === 'submit') {
        submitter.dataset.originalText = submitter.textContent;
        submitter.textContent = 'Working…';
        submitter.disabled = true;
      }
    });
  });
  document.querySelectorAll('input[maxlength], textarea[maxlength]').forEach((field) => {
    const counter = document.createElement('small');
    counter.className = 'cf4-muted'; counter.setAttribute('aria-live', 'polite');
    field.insertAdjacentElement('afterend', counter);
    const update = () => { counter.textContent = `${field.value.length} / ${field.maxLength}`; };
    field.addEventListener('input', update); update();
  });
  document.querySelectorAll('table tbody').forEach((tbody) => {
    if (!tbody.children.length) return;
    tbody.querySelectorAll('tr').forEach((row) => {
      row.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') { row.blur(); }
      });
    });
  });
})();
