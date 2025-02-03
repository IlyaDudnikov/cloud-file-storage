const folderRenameModal = document.getElementById('folder-rename-modal')
if (folderRenameModal) {
    folderRenameModal.addEventListener('show.bs.modal', event => {
        const button = event.relatedTarget

        const path = button.getAttribute('data-bs-path')
        const folderName = button.getAttribute('data-bs-name')

        const modalTitle = folderRenameModal.querySelector('.modal-title')
        const newFolderNameField = folderRenameModal.querySelector('input[name="newFolderName"]')

        // Устанавливаем заголовок и значение в модальном окне для нового имени папки
        modalTitle.textContent = `Rename folder: ${folderName}`;
        newFolderNameField.value = folderName; // Это для поля нового имени папки

        // Получаем скрытые поля формы
        const hiddenPathField = folderRenameModal.querySelector('input[name="path"]');
        const hiddenOldFolderNameField = folderRenameModal.querySelector('input[name="oldFolderName"]');

        // if (hiddenOldFolderNameField) alert("oldFolderName found");
        // else alert("oldFolderName not found");

        // Устанавливаем значения в скрытые поля
        if (hiddenPathField) hiddenPathField.value = path;
        if (hiddenOldFolderNameField) hiddenOldFolderNameField.value = folderName;
    })
}