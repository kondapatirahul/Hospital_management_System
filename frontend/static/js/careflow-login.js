(() => {
  const picker = document.getElementById('portalPicker');
  if (!picker) return;

  const toggle = document.getElementById('portalToggle');
  const menu = document.getElementById('portalMenu');
  const form = document.getElementById('loginForm');
  const fields = document.getElementById('formFields');
  const note = document.getElementById('formUnlockNote');
  const username = document.getElementById('username');
  const password = document.getElementById('password');
  const submit = document.getElementById('loginSubmit');
  const selectedName = document.getElementById('selectedPortalName');
  const selectedHint = document.getElementById('selectedPortalHint');
  const selectedIcon = document.getElementById('selectedPortalIcon');
  const remember = form.querySelector('input[name="remember-me"]');

  function closeMenu() {
    menu.hidden = true;
    toggle.setAttribute('aria-expanded', 'false');
  }

  toggle.addEventListener('click', () => {
    const opening = menu.hidden;
    menu.hidden = !opening;
    toggle.setAttribute('aria-expanded', String(opening));
  });

  menu.querySelectorAll('.portal-option').forEach(option => {
    option.addEventListener('click', () => {
      const role = option.dataset.role;
      selectedName.textContent = option.dataset.name;
      selectedHint.textContent = option.dataset.hint;
      selectedIcon.textContent = option.dataset.icon;
      closeMenu();

      // Reuse the existing demo-account helper so the selected portal
      // fills the matching local demo account without changing auth rules.
      if (typeof window.fillDemoCredentials === 'function') {
        window.fillDemoCredentials(role);
      }

      form.classList.remove('is-locked');
      note.hidden = true;
      fields.setAttribute('aria-hidden', 'false');
      username.disabled = false;
      password.disabled = false;
      remember.disabled = false;
      submit.disabled = false;
      username.focus();
    });
  });

  document.addEventListener('click', event => {
    if (!picker.contains(event.target)) closeMenu();
  });

  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') {
      closeMenu();
      toggle.focus();
    }
  });

  const passwordToggle = document.getElementById('togglePassword');
  passwordToggle.addEventListener('click', () => {
    const showing = password.type === 'text';
    password.type = showing ? 'password' : 'text';
    passwordToggle.setAttribute('aria-label', showing ? 'Show password' : 'Hide password');
    passwordToggle.textContent = showing ? '◎' : '◉';
  });

  document.getElementById('loginYear').textContent = new Date().getFullYear();
})();
