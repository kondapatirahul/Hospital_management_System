(() => {
  'use strict';
  const one = (selector, root = document) => root.querySelector(selector);
  const all = (selector, root = document) => Array.from(root.querySelectorAll(selector));

  function setupQuickFilter() {
    const input = one('#quickFilter');
    const table = one('#outreachTable');
    if (!input || !table) return;
    const rows = all('tbody tr[data-search]', table);
    const empty = one('#quickEmpty', table);
    const count = one('#visibleCount');
    const update = () => {
      const query = input.value.trim().toLocaleLowerCase();
      let visible = 0;
      rows.forEach(row => {
        const text = (row.dataset.search || row.textContent || '').toLocaleLowerCase();
        row.hidden = !text.includes(query);
        if (!row.hidden) visible += 1;
      });
      if (empty) empty.hidden = visible !== 0;
      if (count) count.textContent = `${visible} ${visible === 1 ? 'record' : 'records'} shown`;
    };
    input.addEventListener('input', update);
    update();
  }

  function setupDateRanges() {
    all('form').forEach(form => {
      const from = one('input[name="from"]', form);
      const to = one('input[name="to"]', form);
      if (!from || !to) return;
      const validate = () => to.setCustomValidity(from.value && to.value && from.value > to.value ? 'End date must be on or after start date.' : '');
      from.addEventListener('change', validate);
      to.addEventListener('change', validate);
      form.addEventListener('submit', event => {
        validate();
        if (!form.checkValidity()) { event.preventDefault(); form.reportValidity(); }
      });
    });
  }

  function setupConfirmations() {
    all('form[data-confirm]').forEach(form => form.addEventListener('submit', event => {
      if (form.checkValidity() && form.dataset.confirm && !window.confirm(form.dataset.confirm)) event.preventDefault();
    }));
  }

  function setupSafetyWarnings() {
    all('form[action*="/attempt"]').forEach(form => {
      const names = ['redFlag', 'breathing', 'chestPain', 'neurological', 'pain'];
      const boxes = all('input[type="checkbox"]', form).filter(input => names.includes(input.name));
      const update = () => form.classList.toggle('co-form-has-safety-flag', boxes.some(input => input.checked));
      boxes.forEach(input => input.addEventListener('change', update));
      form.addEventListener('submit', event => {
        if (boxes.some(input => input.checked) && !window.confirm('A red-flag symptom is selected. Follow local emergency procedures if needed and ensure clinical handoff is completed. Save this record?')) event.preventDefault();
      });
    });
  }

  function setupBars() {
    const bars = all('.co-bar-fill[data-value]');
    if (!bars.length) return;
    const values = bars.map(bar => Number.parseFloat(bar.dataset.value || '0')).filter(Number.isFinite);
    const max = Math.max(1, ...values);
    bars.forEach(bar => {
      const value = Math.max(0, Number.parseFloat(bar.dataset.value || '0'));
      bar.style.width = `${Math.min(100, value / max * 100)}%`;
      bar.setAttribute('role', 'img');
      bar.setAttribute('aria-label', `${value} records`);
    });
  }

  function setupCounters() {
    all('textarea[maxlength], input[maxlength]').forEach(field => {
      const max = Number.parseInt(field.getAttribute('maxlength') || '0', 10);
      if (!max || field.dataset.counterReady) return;
      field.dataset.counterReady = 'true';
      const counter = document.createElement('small');
      counter.className = 'co-character-counter';
      counter.setAttribute('aria-live', 'polite');
      field.insertAdjacentElement('afterend', counter);
      const update = () => {
        const remaining = Math.max(0, max - field.value.length);
        counter.textContent = `${remaining.toLocaleString()} characters remaining`;
        counter.classList.toggle('co-counter-low', remaining < Math.ceil(max * .08));
      };
      field.addEventListener('input', update);
      update();
    });
  }

  function setupSubmitState() {
    all('form').forEach(form => form.addEventListener('submit', event => {
      if (event.defaultPrevented || !form.checkValidity()) return;
      const button = event.submitter;
      if (!button || button.dataset.noLoading === 'true') return;
      button.dataset.originalLabel = button.textContent;
      button.disabled = true;
      button.classList.add('co-is-submitting');
      button.textContent = 'Submitting…';
      window.setTimeout(() => {
        if (!button.isConnected) return;
        button.disabled = false;
        button.classList.remove('co-is-submitting');
        button.textContent = button.dataset.originalLabel || 'Submit';
      }, 6000);
    }));
  }

  function setupAccessibleLabels() {
    all('.co-field input, .co-field select, .co-field textarea').forEach(field => {
      if (field.id || field.getAttribute('aria-label')) return;
      const label = field.closest('label');
      const title = label && one(':scope > span', label);
      if (title) field.setAttribute('aria-label', title.textContent.replace('*', '').trim());
    });
  }

  function setupDisabledFieldsets() {
    all('fieldset:disabled').forEach(fieldset => {
      fieldset.setAttribute('aria-disabled', 'true');
      all('input, select, textarea, button', fieldset).forEach(control => control.setAttribute('title', 'This record is finalised. Reopen it before making changes.'));
    });
  }

  document.addEventListener('DOMContentLoaded', () => {
    setupQuickFilter();
    setupDateRanges();
    setupConfirmations();
    setupSafetyWarnings();
    setupBars();
    setupCounters();
    setupSubmitState();
    setupAccessibleLabels();
    setupDisabledFieldsets();
  });
})();
