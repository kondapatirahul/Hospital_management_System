/* CareFlow Phase 3: clinical workflow safeguards and productivity helpers.
 * Progressive enhancement only: server validation and authorization remain authoritative.
 */
(function () {
  'use strict';

  const SELECTORS = {
    form: 'form',
    editable: 'input:not([type="hidden"]):not([type="submit"]):not([type="button"]), select, textarea',
    submit: 'button[type="submit"], input[type="submit"], button:not([type])'
  };
  const destructiveWords = /\b(cancel|delete|remove|discard|dispense|complete|close|discharge|approve|reject|finali[sz]e)\b/i;
  const dateFields = /date|due|appointment|follow.?up|expiry|expiration/i;
  const numberFields = /quantity|amount|price|dose|dosage|weight|height|temperature|pulse|rate|count|stock|age/i;
  const state = { dirtyForms: new WeakSet(), submittedForms: new WeakSet() };

  function isVisible(element) {
    return !!(element.offsetWidth || element.offsetHeight || element.getClientRects().length);
  }

  function liveRegion() {
    let region = document.getElementById('clinicalWorkflowLiveRegion');
    if (!region) {
      region = document.createElement('div');
      region.id = 'clinicalWorkflowLiveRegion';
      region.className = 'cfw-sr-only';
      region.setAttribute('role', 'status');
      region.setAttribute('aria-live', 'polite');
      document.body.appendChild(region);
    }
    return region;
  }

  function announce(message) {
    const region = liveRegion();
    region.textContent = '';
    window.setTimeout(function () { region.textContent = message; }, 20);
  }

  function fieldLabel(field) {
    if (field.labels && field.labels.length) return field.labels[0].textContent.trim().replace(/\s+/g, ' ');
    return field.getAttribute('aria-label') || field.name || field.id || 'This field';
  }

  function addFieldMessage(field, message) {
    let node = field.parentElement && field.parentElement.querySelector(':scope > .cfw-field-message');
    if (!node) {
      node = document.createElement('small');
      node.className = 'cfw-field-message';
      node.setAttribute('aria-live', 'polite');
      field.insertAdjacentElement('afterend', node);
    }
    node.textContent = message;
    field.setAttribute('aria-invalid', 'true');
    field.classList.add('cfw-invalid');
  }

  function clearFieldMessage(field) {
    const parent = field.parentElement;
    const node = parent && parent.querySelector(':scope > .cfw-field-message');
    if (node) node.remove();
    field.removeAttribute('aria-invalid');
    field.classList.remove('cfw-invalid');
  }

  function validateField(field) {
    if (!field || field.disabled || field.type === 'hidden' || !isVisible(field)) return true;
    clearFieldMessage(field);
    const value = String(field.value == null ? '' : field.value).trim();
    const label = fieldLabel(field);
    if (field.required && !value) {
      addFieldMessage(field, label + ' is required.');
      return false;
    }
    if (value && field.type === 'email' && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
      addFieldMessage(field, 'Enter a valid email address.');
      return false;
    }
    if (value && field.type === 'tel' && !/^[+()\d.\s-]{7,25}$/.test(value)) {
      addFieldMessage(field, 'Check the phone number format.');
      return false;
    }
    if (value && field.type === 'number') {
      const number = Number(value);
      if (!Number.isFinite(number)) {
        addFieldMessage(field, 'Enter a valid number.');
        return false;
      }
      if (field.min !== '' && number < Number(field.min)) {
        addFieldMessage(field, 'Value must be at least ' + field.min + '.');
        return false;
      }
      if (field.max !== '' && number > Number(field.max)) {
        addFieldMessage(field, 'Value must be no more than ' + field.max + '.');
        return false;
      }
    }
    if (value && field.maxLength > 0 && value.length > field.maxLength) {
      addFieldMessage(field, 'Use ' + field.maxLength + ' characters or fewer.');
      return false;
    }
    if (value && field.minLength > 0 && value.length < field.minLength) {
      addFieldMessage(field, 'Enter at least ' + field.minLength + ' characters.');
      return false;
    }
    if (value && (field.type === 'date' || field.type === 'datetime-local')) {
      if (field.min && value < field.min) {
        addFieldMessage(field, 'Choose a date on or after ' + field.min + '.');
        return false;
      }
      if (field.max && value > field.max) {
        addFieldMessage(field, 'Choose a date on or before ' + field.max + '.');
        return false;
      }
    }
    return true;
  }

  function enhanceRequiredFields(form) {
    form.querySelectorAll(SELECTORS.editable).forEach(function (field) {
      if (field.required) field.setAttribute('aria-required', 'true');
      field.addEventListener('blur', function () { validateField(field); });
      field.addEventListener('input', function () {
        if (field.hasAttribute('aria-invalid')) validateField(field);
      });
      if (field.tagName === 'TEXTAREA' && (field.maxLength > 0 || field.dataset.characterCount === 'true')) {
        addCharacterCount(field);
      }
      if (field.type === 'number' && numberFields.test(field.name + ' ' + field.id)) {
        field.setAttribute('inputmode', 'decimal');
        if (!field.hasAttribute('step')) field.setAttribute('step', 'any');
      }
      if ((field.type === 'date' || field.type === 'datetime-local') && dateFields.test(field.name + ' ' + field.id)) {
        field.setAttribute('autocomplete', 'off');
      }
    });
  }

  function addCharacterCount(field) {
    if (field.dataset.cfwCountReady === 'true') return;
    field.dataset.cfwCountReady = 'true';
    const count = document.createElement('small');
    count.className = 'cfw-character-count';
    count.setAttribute('aria-live', 'polite');
    field.insertAdjacentElement('afterend', count);
    const update = function () {
      const length = field.value.length;
      count.textContent = field.maxLength > 0 ? length + ' / ' + field.maxLength + ' characters' : length + ' characters';
      count.classList.toggle('cfw-count-near-limit', field.maxLength > 0 && length >= field.maxLength * 0.9);
    };
    field.addEventListener('input', update);
    update();
  }

  function markDirty(form) {
    if (form.dataset.cfwIgnoreDirty === 'true') return;
    form.querySelectorAll(SELECTORS.editable).forEach(function (field) {
      field.addEventListener('change', function () {
        state.dirtyForms.add(form);
        form.dataset.cfwDirty = 'true';
      });
      field.addEventListener('input', function () {
        state.dirtyForms.add(form);
        form.dataset.cfwDirty = 'true';
      });
    });
  }

  function confirmationFor(form, submitter) {
    const explicit = (submitter && submitter.dataset.confirm) || form.dataset.confirm;
    if (explicit) return explicit;
    const action = form.getAttribute('action') || '';
    const label = submitter ? (submitter.textContent || submitter.value || '').trim() : '';
    if (destructiveWords.test(label) || /\/(cancel|delete|remove|dispense|complete|discharge|approve|reject)(\/|$)/i.test(action)) {
      return 'Confirm this clinical workflow action? Please verify the patient and record before continuing.';
    }
    return '';
  }

  function validateForm(form) {
    let valid = true;
    form.querySelectorAll(SELECTORS.editable).forEach(function (field) {
      if (!validateField(field)) valid = false;
    });
    if (!valid) {
      const first = form.querySelector('[aria-invalid="true"]');
      if (first) {
        first.focus({ preventScroll: false });
        first.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
      announce('Please correct the highlighted fields before saving.');
    }
    return valid;
  }

  function setSubmitting(form, submitter) {
    if (state.submittedForms.has(form)) return;
    state.submittedForms.add(form);
    form.dataset.cfwSubmitting = 'true';
    if (submitter) {
      submitter.dataset.cfwOriginalText = submitter.textContent;
      submitter.disabled = true;
      if (submitter.tagName === 'BUTTON' && submitter.textContent.trim()) submitter.textContent = 'Processing…';
      submitter.setAttribute('aria-busy', 'true');
    }
    form.querySelectorAll(SELECTORS.submit).forEach(function (button) {
      if (button !== submitter) button.disabled = true;
    });
  }

  function restoreSubmitting(form) {
    state.submittedForms.delete(form);
    delete form.dataset.cfwSubmitting;
    form.querySelectorAll(SELECTORS.submit).forEach(function (button) {
      button.disabled = false;
      button.removeAttribute('aria-busy');
      if (button.dataset.cfwOriginalText) {
        button.textContent = button.dataset.cfwOriginalText;
        delete button.dataset.cfwOriginalText;
      }
    });
  }

  function enhanceForm(form) {
    if (form.dataset.cfwReady === 'true') return;
    form.dataset.cfwReady = 'true';
    enhanceRequiredFields(form);
    markDirty(form);
    form.addEventListener('submit', function (event) {
      if (state.submittedForms.has(form)) {
        event.preventDefault();
        return;
      }
      if (!validateForm(form)) {
        event.preventDefault();
        return;
      }
      const prompt = confirmationFor(form, event.submitter);
      if (prompt && !window.confirm(prompt)) {
        event.preventDefault();
        return;
      }
      setSubmitting(form, event.submitter || form.querySelector(SELECTORS.submit));
    });
    form.addEventListener('reset', function () {
      form.dataset.cfwDirty = 'false';
      state.dirtyForms.delete(form);
      window.setTimeout(function () { form.querySelectorAll('[aria-invalid="true"]').forEach(clearFieldMessage); }, 0);
    });
  }

  function enhanceWorkflowRows() {
    document.querySelectorAll('[data-cfw-add-row]').forEach(function (button) {
      if (button.dataset.cfwReady === 'true') return;
      button.dataset.cfwReady = 'true';
      button.addEventListener('click', function () {
        const target = document.querySelector(button.dataset.cfwAddRow);
        const template = document.querySelector(button.dataset.cfwTemplate || 'template[data-cfw-row-template]');
        if (!target || !template) return;
        const clone = template.content.cloneNode(true);
        const index = target.children.length;
        clone.querySelectorAll('[name], [id], [for]').forEach(function (element) {
          ['name', 'id', 'for'].forEach(function (attribute) {
            if (!element.hasAttribute(attribute)) return;
            element.setAttribute(attribute, element.getAttribute(attribute).replace(/__INDEX__/g, String(index)));
          });
        });
        target.appendChild(clone);
        target.querySelectorAll('form').forEach(enhanceForm);
        const focusable = target.querySelectorAll('input, select, textarea');
        if (focusable.length) focusable[0].focus();
        announce('A new item row was added.');
      });
    });
    document.addEventListener('click', function (event) {
      const remove = event.target.closest('[data-cfw-remove-row]');
      if (!remove) return;
      const row = remove.closest(remove.dataset.cfwRemoveRow || '[data-cfw-row]');
      if (!row) return;
      if (!window.confirm('Remove this row from the form?')) return;
      row.remove();
      announce('Row removed.');
    });
  }

  function enhanceStatusBadges() {
    document.querySelectorAll('[data-status], .enterprise-pill, .badge').forEach(function (badge) {
      if (badge.dataset.cfwStatusReady === 'true') return;
      badge.dataset.cfwStatusReady = 'true';
      const value = (badge.dataset.status || badge.textContent || '').trim().toLowerCase().replace(/[ _-]+/g, ' ');
      let category = 'neutral';
      if (/complete|approved|dispensed|paid|active|success|verified|released/.test(value)) category = 'success';
      else if (/overdue|critical|urgent|expired|rejected|failed|cancelled|error/.test(value)) category = 'danger';
      else if (/pending|progress|review|partial|scheduled|waiting|draft|open/.test(value)) category = 'warning';
      badge.classList.add('cfw-status-' + category);
      if (!badge.getAttribute('aria-label')) badge.setAttribute('aria-label', 'Status: ' + badge.textContent.trim());
    });
  }

  function addTableEmptyState() {
    document.querySelectorAll('table').forEach(function (table) {
      const body = table.tBodies && table.tBodies[0];
      if (!body || table.dataset.cfwEmptyReady === 'true') return;
      table.dataset.cfwEmptyReady = 'true';
      if (body.rows.length) return;
      const row = body.insertRow();
      const cell = row.insertCell();
      cell.colSpan = Math.max(1, table.tHead && table.tHead.rows.length ? table.tHead.rows[0].cells.length : 1);
      cell.className = 'cfw-empty-cell';
      cell.textContent = 'No records are available in this view.';
    });
  }

  function installUnsavedGuard() {
    window.addEventListener('beforeunload', function (event) {
      if (!document.querySelector('form[data-cfw-dirty="true"]')) return;
      event.preventDefault();
      event.returnValue = '';
    });
    document.addEventListener('click', function (event) {
      const link = event.target.closest('a[href]');
      if (!link || link.target === '_blank' || link.hasAttribute('download')) return;
      const dirty = document.querySelector('form[data-cfw-dirty="true"]');
      if (!dirty || link.href === window.location.href) return;
      if (!window.confirm('You have unsaved changes. Leave this page and discard them?')) event.preventDefault();
    }, true);
  }

  function init() {
    document.querySelectorAll(SELECTORS.form).forEach(enhanceForm);
    enhanceWorkflowRows();
    enhanceStatusBadges();
    addTableEmptyState();
    installUnsavedGuard();
    document.documentElement.classList.add('cfw-clinical-tools-ready');
  }

  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', init);
  else init();
})();
