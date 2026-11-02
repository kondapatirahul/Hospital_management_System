/* Small progressive enhancements for the refined CareFlow pages. */
(function(){
  'use strict';
  document.querySelectorAll('form.cf-care-task-form, form.cf-checkin-form').forEach(function(form){
    form.addEventListener('submit', function(event){
      if (!form.checkValidity()) { event.preventDefault(); form.reportValidity(); return; }
      var submit = form.querySelector('button[type="submit"]');
      if (submit && !submit.dataset.submitting) {
        submit.dataset.submitting = 'true';
        submit.dataset.originalText = submit.textContent;
        submit.textContent = 'Submitting…';
        submit.disabled = true;
      }
    });
  });
  document.querySelectorAll('[data-cf-shortcut-filter]').forEach(function(input){
    var panel = input.closest('.cf-quick-panel') || document;
    var links = Array.from(panel.querySelectorAll('.cf-quick-action'));
    var count = panel.querySelector('.cf-shortcut-count');
    function filter(){
      var query = input.value.trim().toLocaleLowerCase(); var visible = 0;
      links.forEach(function(link){ var match = link.textContent.toLocaleLowerCase().includes(query); link.hidden = !match; if(match) visible++; });
      if(count) count.textContent = visible + ' of ' + links.length + ' shortcuts';
    }
    input.addEventListener('input', filter); filter();
  });
})();
