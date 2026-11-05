document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('#rescheduleModal form');
    if (!form) return;

    const dateInput = form.querySelector('[name="newDate"]');
    const slotSelect = form.querySelector('[name="newSlot"]');
    const submitButton = form.querySelector('[type="submit"]');
    let requestId = 0;

    document.querySelectorAll('[data-modal-target="rescheduleModal"]').forEach(button => {
        button.addEventListener('click', () => {
            form.dataset.availabilityUrl = button.dataset.availabilityUrl || '';
            dateInput.value = '';
            slotSelect.replaceChildren(new Option('-- Select a date first --', ''));
            submitButton.disabled = true;
        });
    });

    dateInput.addEventListener('change', async () => {
        const currentRequestId = ++requestId;
        slotSelect.replaceChildren(new Option('-- Loading available slots --', ''));
        submitButton.disabled = true;

        if (!dateInput.value || !form.dataset.availabilityUrl) {
            slotSelect.replaceChildren(new Option('-- Select a date first --', ''));
            return;
        }

        try {
            const response = await fetch(
                `${form.dataset.availabilityUrl}?date=${encodeURIComponent(dateInput.value)}`,
                { headers: { Accept: 'application/json' } }
            );
            if (!response.ok) throw new Error('Unable to load appointment availability.');
            const slots = await response.json();
            if (currentRequestId !== requestId) return;

            slotSelect.replaceChildren(new Option(
                slots.length ? '-- Choose an available time --' : '-- No available times --',
                ''
            ));
            slots.forEach(slot => slotSelect.add(new Option(slot, slot)));
            submitButton.disabled = slots.length === 0;
        } catch (error) {
            if (currentRequestId !== requestId) return;
            slotSelect.replaceChildren(new Option('-- Availability could not be loaded --', ''));
            console.error(error);
        }
    });

    slotSelect.addEventListener('change', () => {
        submitButton.disabled = !slotSelect.value;
    });
});
