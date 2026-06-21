// Toggle Sidebar
document.getElementById('sidebarToggle')?.addEventListener('click', () => {
    document.getElementById('sidebar-wrapper').classList.toggle('collapsed');
});

// Auto-dismiss alerts after 4s
document.querySelectorAll('.alert').forEach(alert => {
    setTimeout(() => {
        bootstrap.Alert.getOrCreateInstance(alert)?.close();
    }, 4000);
});

// Confirm before delete
document.querySelectorAll('form[data-confirm]').forEach(form => {
    form.addEventListener('submit', e => {
        if (!confirm(form.dataset.confirm)) e.preventDefault();
    });
});
