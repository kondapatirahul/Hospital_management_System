(() => {
  const root = document.querySelector('.admin-command-center');
  if (!root) return;
  const search = document.getElementById('adminDomainSearch');
  const cards = [...document.querySelectorAll('[data-admin-domain]')];
  const result = document.getElementById('adminDomainResult');
  const actions = document.getElementById('adminPriorityActions');
  const score = document.getElementById('adminHealthScore');
  const count = document.getElementById('adminAttentionCount');
  const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  search?.addEventListener('input', () => {
    const query = search.value.trim().toLowerCase();
    cards.forEach(card => { card.hidden = !card.dataset.domain.toLowerCase().includes(query); });
  });
  async function evaluate(domain) {
    result.textContent = `Reviewing ${domain}…`;
    try {
      const response = await fetch(`/api/admin/control-center/domains/${encodeURIComponent(domain)}`, {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify([])
      });
      if (!response.ok) throw new Error(`Request failed (${response.status})`);
      const data = await response.json();
      result.innerHTML = `<strong>${escapeHtml(data.domain)}</strong><p>${data.total} controls assessed · ${data.attention} requiring attention · health index ${Number(data.healthIndex).toFixed(1)}%</p><p>Signals will appear here when connected to the corresponding operational source adapters.</p>`;
    } catch (error) {
      result.textContent = `${domain}: ${error.message}. The dashboard remains available; verify that the admin API is running.`;
    }
  }
  cards.forEach(card => card.addEventListener('click', () => evaluate(card.dataset.domain)));
  document.querySelector('[data-admin-refresh]')?.addEventListener('click', async () => {
    try {
      const response = await fetch('/api/admin/control-center/snapshot', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: '[]' });
      if (!response.ok) throw new Error(`Request failed (${response.status})`);
      const data = await response.json();
      score.textContent = `${Number(data.healthIndex).toFixed(0)}%`;
      count.textContent = data.attentionRequired ?? 0;
      document.getElementById('adminHealthLabel').textContent = 'Assessment completed';
      document.getElementById('adminHealthHint').textContent = `${data.totalChecks} domain control evaluations completed. Connect source adapters to populate live evidence.`;
      const priorities = data.priorityActions || [];
      actions.innerHTML = priorities.length ? priorities.slice(0, 8).map(item => `<div class="admin-action-item">${escapeHtml(item)}</div>`).join('') : '<p class="admin-empty">No attention actions in the submitted signal set.</p>';
    } catch (error) { document.getElementById('adminHealthHint').textContent = `${error.message}. Existing dashboard data remains available.`; }
  });
})();
