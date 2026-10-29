/* CareFlow quality & safety UI helpers. Server-side validation remains authoritative. */
(function () {
  'use strict';

  const $ = (selector, scope = document) => scope.querySelector(selector);
  const $$ = (selector, scope = document) => Array.from(scope.querySelectorAll(selector));

  function bindCharacterCounters() {
    $$('[data-count-for]').forEach((counter) => {
      const fieldName = counter.dataset.countFor;
      const field = document.getElementById(fieldName) || $(`[name="${fieldName}"]`);
      if (!field) return;
      const max = Number(field.getAttribute('maxlength')) || 0;
      const update = () => { counter.textContent = `${field.value.length} / ${max || '—'}`; };
      field.addEventListener('input', update);
      update();
    });
  }

  function bindFilterReset() {
    const reset = $('[data-reset-filters]');
    const form = $('[data-filter-form]');
    if (!reset || !form) return;
    reset.addEventListener('click', () => {
      $$('input, select', form).forEach((control) => {
        if (control.type === 'checkbox') control.checked = false;
        else control.value = '';
      });
      form.requestSubmit();
    });
  }

  function bindConfirmations() {
    $$('[data-confirm]').forEach((control) => {
      control.addEventListener('click', (event) => {
        const prompt = control.dataset.confirm || 'Continue with this action?';
        if (!window.confirm(prompt)) event.preventDefault();
      });
    });
  }

  function bindFormValidation() {
    $$('[data-validated-form]').forEach((form) => {
      form.addEventListener('submit', (event) => {
        if (!form.checkValidity()) {
          event.preventDefault();
          form.reportValidity();
          const firstInvalid = $(':invalid', form);
          if (firstInvalid) firstInvalid.focus({ preventScroll: false });
          return;
        }
        const submitter = event.submitter;
        if (submitter && submitter.type === 'submit') {
          submitter.dataset.originalLabel = submitter.innerHTML;
          submitter.disabled = true;
          submitter.setAttribute('aria-busy', 'true');
          submitter.innerHTML = '<span class="cf-button-spinner" aria-hidden="true"></span> Saving…';
          // Native navigation is intentionally allowed; the browser leaves this page on success.
        }
      });
    });
  }

  function bindVisibleCsvExport() {
    const button = $('[data-export-visible]');
    const table = $('[data-incident-table]');
    if (!button || !table) return;
    button.addEventListener('click', () => {
      const rows = $$('tr', table).filter((row) => !row.querySelector('.cf-empty-state'));
      const matrix = rows.map((row) => $$('th, td', row).slice(0, 6).map((cell) => {
        const text = cell.innerText.replace(/\s+/g, ' ').trim();
        return `"${text.replace(/"/g, '""')}"`;
      }));
      if (matrix.length < 2) {
        window.alert('There are no visible records to export.');
        return;
      }
      const csv = '﻿' + matrix.map((row) => row.join(',')).join('\r\n');
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
      const url = URL.createObjectURL(blob);
      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = 'careflow-visible-incidents.csv';
      document.body.appendChild(anchor);
      anchor.click();
      anchor.remove();
      URL.revokeObjectURL(url);
    });
  }

  function bindLocalSearchEnhancement() {
    const input = $('[data-live-search]');
    const table = $('[data-incident-table]');
    if (!input || !table) return;
    // This only enhances already-rendered rows. Submit the form for full database search.
    input.addEventListener('keydown', (event) => {
      if (event.key === 'Enter') return;
      if (event.key === 'Escape') {
        input.value = '';
        input.focus();
      }
    });
  }

  function bindDateGuardrails() {
    $$('input[type="datetime-local"]').forEach((field) => {
      field.addEventListener('change', () => {
        field.removeAttribute('aria-invalid');
        const value = field.value;
        if (!value) return;
        const chosen = new Date(value);
        if (Number.isNaN(chosen.getTime())) {
          field.setAttribute('aria-invalid', 'true');
          field.setCustomValidity('Choose a valid date and time.');
          return;
        }
        field.setCustomValidity('');
      });
    });
  }

  function bindTextareaAutosize() {
    $$('textarea').forEach((field) => {
      const resize = () => {
        field.style.height = 'auto';
        const max = Math.min(field.scrollHeight, 440);
        field.style.height = `${Math.max(max, 74)}px`;
      };
      field.addEventListener('input', resize);
      if (field.value) resize();
    });
  }

  function bindAccessibleTableLinks() {
    $$('.cf-table tbody tr').forEach((row) => {
      row.addEventListener('keydown', (event) => {
        if (event.key !== 'Enter' || event.target.closest('a,button,input,select,textarea')) return;
        const link = $('.cf-record-title', row) || $('.cf-icon-link', row);
        if (link) link.click();
      });
    });
  }

  function markCurrentNavigation() {
    const path = window.location.pathname;
    $$('.cf-topbar nav a').forEach((link) => {
      const href = new URL(link.href, window.location.origin).pathname;
      if (href === path || (href !== '/' && path.startsWith(href))) {
        link.classList.add('active');
        link.setAttribute('aria-current', 'page');
      }
    });
  }

  function dismissibleAlerts() {
    $$('.cf-alert').forEach((alert) => {
      alert.setAttribute('role', alert.classList.contains('cf-alert-error') ? 'alert' : 'status');
      alert.setAttribute('tabindex', '-1');
    });
  }

  function preventDoubleSubmission() {
    $$('form').forEach((form) => {
      form.addEventListener('submit', (event) => {
        if (form.dataset.submitting === 'true') {
          event.preventDefault();
          return;
        }
        form.dataset.submitting = 'true';
      });
    });
  }

  function restoreSubmitOnInvalid() {
    $$('form').forEach((form) => {
      form.addEventListener('invalid', () => {
        form.dataset.submitting = 'false';
        const submitter = $('button[aria-busy="true"]', form);
        if (submitter) {
          submitter.disabled = false;
          submitter.removeAttribute('aria-busy');
          if (submitter.dataset.originalLabel) submitter.innerHTML = submitter.dataset.originalLabel;
        }
      }, true);
    });
  }

  function initialize() {
    bindCharacterCounters();
    bindFilterReset();
    bindConfirmations();
    bindFormValidation();
    bindVisibleCsvExport();
    bindLocalSearchEnhancement();
    bindDateGuardrails();
    bindTextareaAutosize();
    bindAccessibleTableLinks();
    markCurrentNavigation();
    dismissibleAlerts();
    restoreSubmitOnInvalid();
    preventDoubleSubmission();
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', initialize, { once: true });
  else initialize();
}());
